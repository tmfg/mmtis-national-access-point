// Get OTE build last successful

job('Deploy OTE') {
    parameters {
        choiceParam('ENV', ['staging','production']);
    }
    // Note that if we use a build from some other branch and expect to use secrets from that branch also, there's a problem.
    scm {
        git('https://github.com/tmfg/mmtis-national-access-point.git','*/master')
    }

    environmentVariables {
        groovy('''
          def ote_job = jenkins.model.Jenkins.getInstance().getItem('OTE build from master')
          def ote_build = ote_job.getLastSuccessfulBuild()
          def ote_artifact_path = new java.io.File(ote_build.getArtifactManager().root().toURI())
          return [
            ote_build_artifact: ote_artifact_path.getAbsolutePath() + '/ote/target/ote-0.1-SNAPSHOT-standalone.jar',
            ote_bom_artifact: ote_artifact_path.getAbsolutePath() + '/ote/target/bom.json',
            ote_build_commit: ote_artifact_path.getAbsolutePath() + '/build-commit.txt'
          ]
        ''')
    }

    steps {

        shell('''
            set -euo pipefail

            export AWS_CONFIG_FILE=/dev/null
            export AWS_SHARED_CREDENTIALS_FILE=/dev/null

            # Jenkins and Secrets Manager use historically varying
            # names for these environments.
            case "$ENV" in
              staging)
                DEPLOY_SECRET_ENV=dev
                ;;
              production)
                DEPLOY_SECRET_ENV=prod
                ;;
              *)
                echo "Unsupported deployment environment: $ENV" >&2
                exit 1
                ;;
            esac

            aws secretsmanager get-secret-value \
              --region eu-central-1 \
              --secret-id "ote/${DEPLOY_SECRET_ENV}/deploy" \
              --query SecretString \
              --output text |
            jq -er '
              if ([
                .flyway.host,
                .flyway.username,
                .flyway.password,
                .target_group_name,
                .sbom.role_arn,
                .sbom.bucket_name,
                .sbom.object_prefix
              ] | all(type == "string")) then
                "deploy_db_host=\\(.flyway.host)\\n" +
                "deploy_db_flyway_user=\\(.flyway.username)\\n" +
                "deploy_db_flyway_password=\\(.flyway.password)\\n" +
                "deploy_target_group_name=\\(.target_group_name)\\n" +
                "deploy_sbom_upload_role_arn=\\(.sbom.role_arn)\\n" +
                "deploy_sbom_s3_bucket_name=\\(.sbom.bucket_name)\\n" +
                "deploy_sbom_uuid=\\(.sbom.object_prefix)"
              else
                error("deployment secret has an invalid shape")
              end
            ' > build.properties
        ''')

        envInjectBuilder {
            propertiesContent('')
            propertiesFilePath('build.properties')
        }

        maven {
            goals('flyway:migrate')
            rootPOM('database/pom.xml')
            mavenInstallation('Maven 3.5.0')
            property('databaseUrl', 'jdbc:postgresql://${deploy_db_host}/napote')
            property('databaseUser', '${deploy_db_flyway_user}')
            property('databasePassword', '${deploy_db_flyway_password}')
            property('databaseSchema', 'public')
        }


        ansiblePlaybookBuilder {
            additionalParameters('--vault-password-file=~/.vault_pass.txt')
            playbook('aws/ansible/ote.yml')
            inventory {
                inventoryPath { path('aws/ansible/environments/${ENV}/inventory') }
            }
            extraVars {
                extraVar {
                    key('ote_build_artifact')
                    secretValue(hudson.util.Secret.fromString('${ote_build_artifact}'))
                    hidden(true)
                }
                extraVar {
                    key('deploy_target_group_name')
                    secretValue(hudson.util.Secret.fromString('${deploy_target_group_name}'))
                    hidden(true)
                }
            }
        }

        shell('''
            set -euo pipefail

            export AWS_CONFIG_FILE=/dev/null
            export AWS_SHARED_CREDENTIALS_FILE=/dev/null

            COMMIT_SHA=$(tr -d '\n' < "$ote_build_commit")
            SBOM_FILE="$ote_bom_artifact"

            set +x

            CREDS=$(aws sts assume-role --role-arn "$deploy_sbom_upload_role_arn" --role-session-name finap-jenkins-sbom-upload --output json)
            export AWS_ACCESS_KEY_ID=$(echo "$CREDS" | jq -r '.Credentials.AccessKeyId')
            export AWS_SECRET_ACCESS_KEY=$(echo "$CREDS" | jq -r '.Credentials.SecretAccessKey')
            export AWS_SESSION_TOKEN=$(echo "$CREDS" | jq -r '.Credentials.SessionToken')

            aws s3 cp "$SBOM_FILE" "s3://${deploy_sbom_s3_bucket_name}/${deploy_sbom_uuid}/${COMMIT_SHA}.json"
        ''')
    }
}

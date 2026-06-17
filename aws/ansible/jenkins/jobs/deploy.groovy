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

        shell('ansible-vault view --vault-password-file=~/.vault_pass.txt aws/ansible/environments/${ENV}/group_vars/all/vault > build.properties')

        envInjectBuilder {
            propertiesContent('')
            propertiesFilePath('build.properties')
        }

        maven {
            goals('flyway:migrate')
            rootPOM('database/pom.xml')
            mavenInstallation('Maven 3.5.0')
            property('databaseUrl', 'jdbc:postgresql://${vault_db_host}/napote')
            property('databaseUser', '${vault_db_flyway_user}')
            property('databasePassword', '${vault_db_flyway_password}')
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
            }
        }

        shell('''
            set -euo pipefail

            COMMIT_SHA=$(tr -d '\n' < "$ote_build_commit")
            SBOM_FILE="$ote_bom_artifact"

            CREDS=$(aws sts assume-role --role-arn "$vault_sbom_upload_role_arn" --role-session-name finap-jenkins-sbom-upload --output json)
            export AWS_ACCESS_KEY_ID=$(echo "$CREDS" | jq -r '.Credentials.AccessKeyId')
            export AWS_SECRET_ACCESS_KEY=$(echo "$CREDS" | jq -r '.Credentials.SecretAccessKey')
            export AWS_SESSION_TOKEN=$(echo "$CREDS" | jq -r '.Credentials.SessionToken')

            aws s3 cp "$SBOM_FILE" "s3://${vault_sbom_s3_bucket_name}/${vault_sbom_uuid}/${COMMIT_SHA}.json"
        ''')
    }
}

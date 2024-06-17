// Get OTE build last successful

job('Deploy OTE') {
    parameters {
        choiceParam('ENV', ['staging','production']);
    }
    scm {
        git('https://github.com/tmfg/mmtis-national-access-point.git','*/ol9-update')
    }

    environmentVariables {
        groovy('''
          def ote_job = jenkins.model.Jenkins.getInstance().getItem('OTE build from master')
          def ote_build = ote_job.getLastSuccessfulBuild()
          def ote_artifact_path = new java.io.File(ote_build.getArtifactManager().root().toURI())
          return [ote_build_artifact: ote_artifact_path.getAbsolutePath() + '/ote/target/ote-0.1-SNAPSHOT-standalone.jar']
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
        }


        ansiblePlaybookBuilder {
            additionalParameters('--vault-password-file=~/.vault_pass.txt')
            playbook('aws/ansible/ote.yml')
            inventory {
                inventoryPath { path('aws/ansible/environments/${ENV}/inventory') }
            }
            limit: 'ote-el9'
            extraVars {
                extraVar {
                    key('ote_build_artifact')
                    value('${ote_build_artifact}')
                }
            }
        }
    }
}

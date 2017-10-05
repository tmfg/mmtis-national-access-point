// Get OTE build last successfull
def ote_job = jenkins.model.Jenkins.getInstance().getItem('OTE build from master')
def ote_build = ote_job.getLastSuccessfulBuild()
def ote_artifact_path = new java.io.File(ote_build.getArtifactManager().root().toURI())
def ote_build_artifact = ote_artifact_path.getAbsolutePath() + '/ote/target/ote-0.1-SNAPSHOT-standalone.jar'

job('Deploy OTE') {
    parameters {
        choiceParam('ENV', ['staging','production']);
    }
    scm {
        git('https://github.com/finnishtransportagency/mmtis-national-access-point.git')
    }
    steps {
        // PENDING: run database migration for as a step
        ansiblePlaybookBuilder {
            additionalParameters('--vault-password-file=~/.vault_pass.txt')
            playbook('aws/ansible/ote.yml')
            inventory {
                inventoryPath { path('aws/ansible/environments/${ENV}/inventory') }
            }
            extraVars {
                extraVar {
                    key('ote_build_artifact')
                    value(ote_build_artifact)
                }
            }
        }
    }
}

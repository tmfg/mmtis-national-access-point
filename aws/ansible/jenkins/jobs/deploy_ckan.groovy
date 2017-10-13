// Get OTE build last successful

job('Deploy CKAN plugin') {
    parameters {
        choiceParam('ENV', ['staging','production']);
    }
    scm {
        git('https://github.com/finnishtransportagency/mmtis-national-access-point.git') {
            branch('master')
        }
    }

    steps {
        ansiblePlaybookBuilder {
            additionalParameters('--vault-password-file=~/.vault_pass.txt')
            playbook('aws/ansible/ckan_plugin.yml')
            inventory {
                inventoryPath { path('aws/ansible/environments/${ENV}/inventory') }
            }
        }
    }
}

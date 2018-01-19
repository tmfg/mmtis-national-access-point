job('Backup FINAP database') {
    parameters {
        choiceParam('ENV', ['staging','production']);
    }
    scm {
        git('https://github.com/finnishtransportagency/mmtis-national-access-point.git','*/master')
    }

    steps {

        shell('ansible-vault view --vault-password-file=~/.vault_pass.txt aws/ansible/environments/${ENV}/group_vars/all/vault > build.properties')

        envInjectBuilder {
            propertiesContent('')
            propertiesFilePath('build.properties')
        }

        shell('PGPASSWORD=${vault_db_flyway_password} pg_dump -h ${vault_db_host} -U ${vault_db_flyway_user} --schema=public -Fc napote > napote-${ENV}.dump')
    }
}

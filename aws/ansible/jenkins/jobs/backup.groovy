job('Backup FINAP database') {
    parameters {
        choiceParam('ENV', ['staging','production']);
    }
    scm {
        git('https://github.com/tmfg/mmtis-national-access-point.git','*/master')
    }

    steps {

        shell('ansible-vault view --vault-password-file=~/.vault_pass.txt aws/ansible/environments/${ENV}/group_vars/all/vault > build.properties')

        envInjectBuilder {
            propertiesContent('')
            propertiesFilePath('build.properties')
        }

        shell('PGPASSWORD=${vault_db_flyway_password} pg_dump -h ${vault_db_host} -U ${vault_db_flyway_user} --schema=public -Fc -Z 9 napote > napote-${ENV}.dump')

        shell('mv napote-${ENV}.dump napote-${ENV}-`date +%Y-%m-%d`.dump')

        shell('AWS_ACCESS_KEY_ID=${vault_backup_aws_key_id} AWS_SECRET_ACCESS_KEY=${vault_backup_aws_secret} aws s3 cp napote-${ENV}-`date +%Y-%m-%d`.dump s3://finap-backup/')

        shell('rm *.dump')

    }
}

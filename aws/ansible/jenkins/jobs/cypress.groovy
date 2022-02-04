job('Cypress-e2e-tests') {
    logRotator {
        daysToKeep(3)
    }

    scm {
        git('https://github.com/tmfg/mmtis-national-access-point.git', '*/master')
    }

    wrappers {
        xvfb('default') {
            screen('1920x1080x24')
        }
        nodejs('nodejs-10.x-cypress')
        toolenv('nodejs-10.x-cypress')
    }

    steps {
        shell('ansible-vault view --vault-password-file=~/.vault_pass.txt aws/ansible/environments/staging/group_vars/all/vault > build.properties')

        envInjectBuilder {
            propertiesContent('')
            propertiesFilePath('build.properties')
        }

        shell('npm i cypress@3.x && $(npm bin)/cypress verify')
        shell('CYPRESS_NAP_LOGIN=${vault_cypress_nap_username} '+
              'CYPRESS_NAP_PASSWORD=${vault_cypress_nap_password} '+
              '$(npm bin)/cypress run --record false')
    }
}

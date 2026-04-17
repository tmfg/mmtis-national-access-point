job('Generate and upload SBOM') {
    logRotator {
        daysToKeep(3)
    }

    scm {
        git('https://github.com/tmfg/mmtis-national-access-point.git', '*/master')
    }

    steps {
        shell('''\
cd ote
lein pom
mvn org.cyclonedx:cyclonedx-maven-plugin:2.9.1:makeAggregateBom \
    -DoutputFormat=json -DschemaVersion=1.6 -DoutputName=bom -q
''')

        shell('ansible-vault view --vault-password-file=~/.vault_pass.txt aws/ansible/environments/production/group_vars/all/vault > build.properties')

        envInjectBuilder {
            propertiesContent('')
            propertiesFilePath('build.properties')
        }

        // TODO: Adjust the presigned URL request to match the actual API.
        // vault_sbom_api_url and vault_sbom_api_key must be added to the ansible vault.
        shell('''
            PRESIGNED_URL=$(curl --fail -s -H "Authorization: Bearer $vault_sbom_api_key" "$vault_sbom_api_url")
            curl --fail -X PUT -T ote/target/bom.json "$PRESIGNED_URL"
        ''')
    }

    publishers {
        archiveArtifacts {
            pattern('ote/target/bom.json')
            onlyIfSuccessful()
        }
    }
}

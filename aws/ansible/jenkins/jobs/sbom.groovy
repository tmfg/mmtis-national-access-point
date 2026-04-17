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

        shell('''
            CREDS=$(aws sts assume-role --role-arn "$vault_sbom_role_arn" --role-session-name sbom-upload --output json)
            export AWS_ACCESS_KEY_ID=$(echo "$CREDS" | jq -r '.Credentials.AccessKeyId')
            export AWS_SECRET_ACCESS_KEY=$(echo "$CREDS" | jq -r '.Credentials.SecretAccessKey')
            export AWS_SESSION_TOKEN=$(echo "$CREDS" | jq -r '.Credentials.SessionToken')
            aws s3 cp ote/target/bom.json "s3://${vault_sbom_s3_bucket}/${vault_sbom_s3_key}"
        ''')
    }

    publishers {
        archiveArtifacts {
            pattern('ote/target/bom.json')
            onlyIfSuccessful()
        }
    }
}

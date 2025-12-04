job('OTE build from master') {
    parameters {
        stringParam('branch', '*/master', 'Branch to build from')
    }
    
    logRotator {
        daysToKeep(3)
    }

    scm {
        git('https://github.com/tmfg/mmtis-national-access-point.git', '$branch')
    }
    triggers {
        scm('H/15 * * * *')
    }

    environmentVariables {
        groovy('''
          def changelog_job = jenkins.model.Jenkins.getInstance().getItem('Generate ChangeLog from Github PRs')
          def changelog_build = changelog_job.getLastSuccessfulBuild()
          def changelog_artifact_path = new java.io.File(changelog_build.getArtifactManager().root().toURI())
          return [changelog_html: changelog_artifact_path.getAbsolutePath() + '/tools/changelog/changelog.html']
        ''')
    }

    steps {

        shell('mkdir -p  ote/resources/public/ && cp "${changelog_html}" ote/resources/public/')

        shell('sh database/testdb.sh')

        maven {
            goals('flyway:migrate')
            rootPOM('database/pom.xml')
            mavenInstallation('Maven 3.5.0')
            property('databaseUrl', 'jdbc:postgresql://localhost/napotetest_template')
            property('databaseUser', 'napotetest')
            property('databaseSchema', 'napotetest_template')
        }

        leiningenBuilder {
            subdirPath('ote')
            task('production')
        }

        downstreamParameterized {
            trigger('Deploy OTE') {
                parameters {
                    predefinedProp('ENV','staging')
                }
            }
        }
    }
    publishers {
        archiveArtifacts {
            pattern('ote/target/*-standalone.jar')
            onlyIfSuccessful()
        }
        slackNotifier {
            notifyAborted(false)
            notifyBackToNormal(true)
            notifyFailure(true)
            notifyNotBuilt(false)
            notifyRegression(true)
            notifyRepeatedFailure(false)
            notifySuccess(true)
            notifyUnstable(true)
        }
    }
}

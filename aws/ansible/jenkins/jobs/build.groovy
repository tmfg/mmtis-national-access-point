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
            property('databaseUrl', 'jdbc:postgresql://127.0.0.1/napotetest_template')
            property('databaseUser', 'napotetest')
            property('databaseSchema', 'public')
        }

        leiningenBuilder {
            subdirPath('ote')
            task('production')
        }

        // Generate the POM file for the project, which is needed for the CycloneDX plugin to generate the SBOM.
        leiningenBuilder {
            subdirPath('ote')
            task('pom')
        }

        maven {
            goals('org.cyclonedx:cyclonedx-maven-plugin:2.9.1:makeAggregateBom')
            rootPOM('ote/pom.xml')
            mavenInstallation('Maven 3.5.0')
            property('outputFormat', 'json')
            property('schemaVersion', '1.6')
            property('outputName', 'bom')
        }

        // Because of this "get latest build" pattern, we'll also store the git commit hash as an artifact.
        shell('git rev-parse HEAD > build-commit.txt')

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
            pattern('ote/target/*-standalone.jar,ote/target/bom.json,build-commit.txt')
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

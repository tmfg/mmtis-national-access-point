job('OTE build from master') {
    logRotator {
        daysToKeep(3)
    }

    scm {
        git('https://github.com/finnishtransportagency/mmtis-national-access-point.git', '*/master')
    }
    triggers {
        scm('H/15 * * * *')
    }
    steps {

        shell('sh database/testdb.sh')

        maven {
            goals('flyway:migrate')
            rootPOM('database/pom.xml')
            mavenInstallation('Maven 3.5.0')
            property('databaseUrl', 'jdbc:postgresql://localhost/napotetest_template')
            property('databaseUser', 'napotetest')
        }

        leiningenBuilder {
            subdirPath('ote')
            task('production')
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

job('Generate ChangeLog from Github PRs') {
    logRotator {
        daysToKeep(3)
    }

    scm {
        git('https://github.com/tmfg/mmtis-national-access-point.git', '*/master')
    }
    triggers {
        cron('0 22 * * *')
    }
    steps {
        leiningenBuilder {
            subdirPath('tools/changelog')
            task('run')
        }
    }
    publishers {
        archiveArtifacts {
            pattern('tools/changelog/changelog.html')
        }
        downstream('OTE build from master','SUCCESS')
    }
}

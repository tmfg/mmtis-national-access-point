job('OTE build from master') {
  scm {
    git('https://github.com/finnishtransportagency/mmtis-national-access-point.git')
  }
  triggers {
    scm('H/15 * * * *')
  }
  steps {

    maven('flyway:migrate', 'database/pom.xml')

    leiningenBuilder {
      subdirPath('ote')
      task('production')
    }
  }
  publishers {
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

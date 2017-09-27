job('OTE build from master') {
  scm {
    git('https://github.com/finnishtransportagency/mmtis-national-access-point.git')
  }
  triggers {
    scm('H/15 * * * *')
  }
  steps {
    leiningenBuilder {
      subdirPath('ote')
      task('clean')
      task('compile')
      task('cljsbuild')
      task('uberjar')
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

job('OTE build from master') {
  scm {
    git('https://github.com/finnishtransportagency/mmtis-national-access-point.git')
  }
  triggers {
    scm('H/15 * * * *')
  }
  steps {

    shell('sh database/testdb.sh')

    maven('flyway:migrate', 'database/pom.xml') {
      mavenInstallation('Maven 3.5.0')
    }

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

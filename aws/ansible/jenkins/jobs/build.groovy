job('build') {
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
}

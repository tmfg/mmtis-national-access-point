job('Hello World Test') {
    logRotator {
        daysToKeep(1)
    }

    steps {
        shell('echo "Hello World from Jenkins Job DSL!"')
    }
}


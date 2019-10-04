job('Daily staging env deploy') {
    triggers {
        // Run every day at six in the morning
        cron('0 6 * * *')
    }
    steps {
        downstreamParameterized {
            trigger('Deploy OTE') {
                parameters {
                    predefinedProp('ENV','staging')
                }
            }
            trigger('Cypress-e2e-tests')
        }
    }
}

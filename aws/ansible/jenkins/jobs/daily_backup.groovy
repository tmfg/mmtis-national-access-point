job('Daily database backup') {
    // triggers {
    //     // Run every day at 3 in the morning
    //     cron('0 3 * * *')
    // }
    steps {
        downstreamParameterized {
            trigger('Backup FINAP database') {
                parameters {
                    predefinedProp('ENV','staging')
                }
            }
            trigger('Backup FINAP database') {
                parameters {
                    predefinedProp('ENV','production')
                }
            }
        }
    }
}

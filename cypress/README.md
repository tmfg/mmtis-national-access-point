### E2E testing with Cypress

### Installing Cypress
Tested with NodeJS v6.x.

In our project the easiest way to install and use Cypress, is to install it as a global node module.  
You should install a LTS version of NodeJS for the best support.

```bash
$ npm install -g cypress
```

Run cypress verify to make sure that cypress is installed correctly.

```bash
$ cypress verify
```

#### ENV-variables

All custom and Cypress-specific ENV variables have CYPRESS_ prefix.

List of custom variables:

* CYPRESS_NAP_LOGIN: Test user username
* CYPRESS_NAP_PASSWORD: Test user password

Some useful cypress ENV variables:

* CYPRESS_RECORD_KEY
* CYPRESS_baseUrl: By default we are using a value defined in cypress.json (testi.finap.fi)
* CYPRESS_port
* CYPRESS_reporter
* CYPRESS_screenshotOnHeadlessFailure
* CYPRESS_watchForFileChanges

https://docs.cypress.io/guides/references/configuration.html#Global


### Running Cypress locally


#### Interactive mode

https://docs.cypress.io/guides/guides/command-line.html#cypress-open

```bash
$ CYPRESS_NAP_LOGIN=username CYPRESS_NAP_PASSWORD=password cypress open
```

#### Run headlessly single test

https://docs.cypress.io/guides/guides/command-line.html#cypress-run

```bash
$ CYPRESS_NAP_LOGIN=username CYPRESS_NAP_PASSWORD=password cypress --headed --spec "cypress/integration/operator_spec.js"
```

#### Run headlessly and capture screenshots and video

https://docs.cypress.io/guides/guides/command-line.html#cypress-run

```bash
$ CYPRESS_NAP_LOGIN=username CYPRESS_NAP_PASSWORD=password cypress run --record
```

#### Run headlessly and store results into Cypress cloud dashboard

https://docs.cypress.io/guides/core-concepts/dashboard-service.html#Setup

```bash
$ CYPRESS_NAP_LOGIN=username CYPRESS_NAP_PASSWORD=password cypress run --record --key <secret record key>
```

The secret record key can be copied from the Cypress cloud dashboard if you have proper access rights.  
Get the key from [here](https://dashboard.cypress.io/#/projects/ucw436/settings)

### Cypress in CI environment

https://docs.cypress.io/guides/guides/continuous-integration.html#

In short: 
1. Install Cypress
1. Define CYPRESS_NAP_LOGIN, CYPRESS_NAP_PASSWORD and CYPRESS_RECORD_KEY (copy it from [here](https://dashboard.cypress.io/#/projects/ucw436/settings)) ENV variables.  
1. Add ```cypress run --record``` command in the CI configuration.


### Writing tests

#### Folder structure
All the test spec files must be stored in ```cypress/integration``` directory.  
If fixtures are needed in tests, they can be stored in ```cypress/fixtures``` directory.  
All the supporting features and custom cypress commands should be added in the ```cypress/support``` directory.  


More info: 
* [Writing and organizing tests](https://docs.cypress.io/guides/core-concepts/writing-and-organizing-tests.html#)
* [Writing your first test](https://docs.cypress.io/guides/getting-started/writing-your-first-test.html)
* [Supported assertions](https://docs.cypress.io/guides/references/assertions.html)
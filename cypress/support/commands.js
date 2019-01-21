// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... });
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... });
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... });
//
//
// -- This is will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... });


/**
 * Login without UI using CKAN repoze.auth_tkt login
 * https://docs.cypress.io/guides/getting-started/testing-your-app.html#Logging-In
 *
 * NOTE: Cypress automatically clears all cookies before each test to prevent state from being shared across tests.
 */

// CKAN login
/* Cypress.Commands.add('login', (login, password) => {
    const username = login || Cypress.env('NAP_LOGIN');

    cy.log(`Logging in with username: ${username}`);

    cy.request({
        method: 'POST',
        url: '/login_generic?came_from=/user/logged_in',
        form: true, // indicates the body should be form urlencoded and sets Content-Type: application/x-www-form-urlencoded headers
        followRedirect: false,
        body: {
            login: username,
            password: password || Cypress.env('NAP_PASSWORD')
        },
    });
});*/

// OTE login
Cypress.Commands.add('login', (login, password) => {
    const username = login || Cypress.env('NAP_LOGIN');

    cy.log(`Logging in with username: ${username}`);

    cy.request({
        method: 'POST',
        url: '/login',
        form: false, // indicates the body should be form urlencoded and sets Content-Type: application/x-www-form-urlencoded headers
        followRedirect: false,
        body: ["^ ", "~:email", username, "~:password", password || Cypress.env('NAP_PASSWORD')]
    });
});

Cypress.Commands.add('normalLogin', () => {
    const username = Cypress.env('NAP_NORMALUSERLOGIN');
    const password = Cypress.env('NAP_NORMALUSERPWD');

    cy.log(`normalLogin: ${username}`);

    cy.login(username, password);
});


Cypress.Commands.add('logout', () => {
    cy.log(`Logging out`);

    cy.request({
        method: 'GET',
        url: '/user/_logout',
        followRedirect: false,
    });
});

/**
 * Cookies will not be cleared before the NEXT test starts
 * Use preferably in beforeEach block.
 */
Cypress.Commands.add('preserveSessionOnce', () => {
    Cypress.Cookies.preserveOnce('auth_tkt', 'ckan', 'finap_lang');
});


// Raw "typing" simulation for injecting any string data into input fields.
Cypress.Commands.add("typeRaw", { prevSubject: true }, ($subj, text) => {
    const event = new Event('input', {
        bubbles: true,
        cancelable: true,
    });

    event.simulated = true;

    $subj[0].value += text;
    $subj[0].dispatchEvent(event);

    // We'll have to wait a bit or our app won't keep up
    cy.wait(100);
});

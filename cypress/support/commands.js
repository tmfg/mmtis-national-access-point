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


// Login without UI using CKAN repoze.auth_tkt login
// https://docs.cypress.io/guides/getting-started/testing-your-app.html#Logging-In
// Note: cy.visit should be called before invoking this command, so that the host url is properly set.
Cypress.Commands.add('login', (login, password) => {
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
});
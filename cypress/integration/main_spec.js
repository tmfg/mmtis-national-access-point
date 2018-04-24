import { genNaughtyString } from '../support/generators';

// Please read "Introduction to Cypress"
// https://on.cypress.io/introduction-to-cypress

// Basic app logic and structure

describe('NAP Main', function () {
    beforeEach(function () {
        cy.visit('/');
    });

    // https://docs.cypress.io/guides/getting-started/testing-your-app.html#Logging-In
    it('should login properly', function () {
        cy.login();

        // Check if our repoze auth-tkt session cookie exists after login
        cy.getCookie('auth_tkt').should('exist');
    });

    it('.should() - assert that <title> is correct', function () {
        cy.title().should('include', 'NAP - liikkumispalvelukatalogi');
    });
});

describe('OTE login dialog', () => {

    beforeEach(() => {
        cy.server();
        cy.route('POST', '/login').as('login');
        cy.visit('/#/services')
    });

    const login = (username, password, click) => {
        cy.contains('Kirjaudu sisään').click();
        cy.get('input[id*="email--Shkpostiosoite"]').type(username);
        cy.get('input[id*="password--Salasana"]').type(password);

        if (click) {
            cy.get('.login-dialog-footer button').click();
        }
        cy.wait('@login');
    };

    it('should warn about unknown user', () => {
        login(genNaughtyString(20), genNaughtyString(20), true);
        cy.contains('Tuntematon käyttäjä');
    });

    it('should warn about wrong password', () => {
        login(Cypress.env('NAP_LOGIN'), genNaughtyString(20), true);
        cy.contains('Väärä salasana');
    });

    it('should login properly with correct credentials', () => {
        login(Cypress.env('NAP_LOGIN'), Cypress.env('NAP_PASSWORD'), true);
        cy.contains('Kirjauduit sisään onnistuneesti!');
    });

    it('should login by pressing enter', () => {
        login(Cypress.env('NAP_LOGIN'), Cypress.env('NAP_PASSWORD') + '{enter}', false);
        cy.contains('Kirjauduit sisään onnistuneesti!');
    });
});

describe('Header - Logged Out', function () {
    it('OTE should have proper header links', function () {
        cy.visit('/');

        cy.get('.navbar').within($navbar => {
            cy.contains('Etusivu');
            cy.contains('Palvelukatalogi');
            cy.contains('Palveluntuottajat');
            cy.contains('Kirjaudu sisään');
            cy.contains('Rekisteröidy');
            cy.contains('Etusivu');
        });
    });
});

describe('Header - Logged In', function () {
    // Login only once before the tests run
    before(function () {
        cy.login();
    });

    beforeEach(function () {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();
    });

    it('OTE should have proper header links', function () {
        cy.visit('/');

        cy.get('.navbar').within($navbar => {
            cy.contains('Etusivu');
            cy.contains('Palvelukatalogi');
            cy.contains('Palveluntuottajat');
            cy.contains('Omat palvelutiedot');
            cy.contains('Käyttöohje');
        });

        cy.get('.user-menu button').click();

        cy.get('div[role=menu]').within($el => {
            // Dropdown menu links
            cy.contains('Käyttäjätilin muokkaus');
            cy.contains('Anna palautetta palvelusta');
            cy.contains('Kirjaudu ulos');
            cy.contains('suomi');
            cy.contains('svenska');
        });
    });
});

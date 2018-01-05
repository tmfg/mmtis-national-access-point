// Please read "Introduction to Cypress"
// https://on.cypress.io/introduction-to-cypress

// Basic app logic and structure

describe('NAP Main', () => {
    beforeEach(() => {
        cy.visit('/');
    });

    // https://docs.cypress.io/guides/getting-started/testing-your-app.html#Logging-In
    it('should login properly', () => {
        cy.login();

        // Check if our repoze auth-tkt session cookie exists after login
        cy.getCookie('auth_tkt').should('exist');

        // Simulating repoze login redirect behaviour
        cy.visit('/user/logged_in');

        // Checking if our plugin is redirecting to the correct page
        cy.location().should(loc => {
            expect(loc.pathname).to.eq('/ote/');
            expect(loc.hash).to.eq('#/?logged_in=1');
        });
    });

    it('.should() - assert that <title> is correct', () => {
        cy.title().should('include', 'Tervetuloa - NAP');
    });
});


describe('Header - Logged Out', () => {
    it('CKAN should have proper header links', () => {
        cy.visit('/');

        cy.get('.navbar').within($navbar => {
            cy.contains('Etusivu');
            cy.contains('Palvelukatalogi').should('have.attr', 'href').and('eq', '/ote/#/services');
            cy.contains('Palveluntuottajat').should('have.attr', 'href').and('eq', '/organization');
            cy.contains('Kirjaudu sisään').should('have.attr', 'href').and('eq', '/user/login');
            cy.contains('Rekisteröidy').should('have.attr', 'href').and('eq', '/user/register');
        });
    });

    it('OTE should have proper header links', () => {
        cy.visit('/ote/');

        cy.get('.ote-sovellus .container-fluid').find('ul')
            .within($navbar => {
                cy.contains('Palvelukatalogi');
                cy.contains('Palveluntuottajat');
                cy.contains('Kirjaudu sisään');
                cy.contains('Rekisteröidy');
                cy.contains('Etusivu');
            });
    });
});

describe('Header - Logged In', () => {
    beforeEach(() => {
        cy.login();
    });

    it('CKAN should have proper header links', () => {
        cy.visit('/');

        cy.get('.navbar').within($navbar => {
            cy.contains('Etusivu');
            cy.contains('Palvelukatalogi').should('have.attr', 'href').and('eq', '/ote/#/services');
            cy.contains('Palveluntuottajat').should('have.attr', 'href').and('eq', '/organization');
            cy.contains('Omat palvelutiedot').should('have.attr', 'href').and('eq', '/ote/#/own-services');

            cy.get('.section-right').within($el => {
                cy.contains('Test user');

                // Dropdown menu links
                cy.contains('Yhteenveto');
                cy.contains('Käyttäjätilin muokkaus');
                cy.contains('Anna palautetta palvelusta');
                cy.contains('NAP-palvelun käyttöohje');
                cy.contains('Kirjaudu ulos');
                cy.contains('suomi');
                cy.contains('svenska');
            });
        });
    });
});
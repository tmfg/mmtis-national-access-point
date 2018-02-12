// Basic operator page search tests

describe('Operator search page basic tests', function () {

    it('should render operator page', () => {
        cy.visit('/ote/#/operators');
        cy.contains('Palveluntuottajat');
        cy.contains('Yhteensä');
    });

    it('operator page should contain initial results', () => {
       cy.server();
       cy.route('POST', '/ote/operators/list').as('getOperators');
       cy.visit('/ote/#/operators');
       cy.wait('@getOperators');
       cy.get('.operator-list').find('.operator').should('have.length.above', 0)
    });

    it('search and find operator', () => {
        cy.server();
        cy.route('POST', '/ote/operators/list').as('getOperators');
        cy.visit('/ote/#/operators');
        cy.wait('@getOperators');

        // Give search term
        cy.get('input[id*="-Haenimelltainimenosalla-"]').as('operatorName');
        cy.get('@operatorName').type("Ajopalvelu");
        cy.wait('@getOperators');

        // Ensure that result is correct
        cy.get('.operator-list').find('.operator').should('have.length.above', 0)
    });

    it('search and dont find operator', () => {
        cy.server();
        cy.route('POST','/ote/operators/list').as('findOperators')

        // Clear old search terms and give new one
        cy.get('input[id*="-Haenimelltainimenosalla-"]').as('operatorName');
        cy.get('@operatorName').clear();
        cy.get('@operatorName').type("Eijoleeee");
        cy.get('@operatorName').click();
        cy.wait('@findOperators');

        // Ensure that result is correct
        cy.contains('Hakuehdoilla ei löytynyt palveluntuottajia');
    });
});


describe('Operator search page modal tests', function () {

    it('open operator modal', () => {
        cy.server();
        cy.route('POST', '/ote/operators/list').as('getOperators');
        cy.visit('/ote/#/operators');
        cy.wait('@getOperators');

        // Give search term
        cy.get('input[id*="-Haenimelltainimenosalla-"]').as('operatorName');
        cy.get('@operatorName').type("Ajopalvelu");
        cy.wait('@getOperators');

        // Open Modal
        cy.get('.operator-list').find('.operator').first().find('a[href="#"]').click();
        // Check business-id
        cy.contains('1234567-8');

    });

});
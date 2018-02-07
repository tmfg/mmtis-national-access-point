// TODO: Test service search page (palvelukatalogi) features

describe('Service search page basic tests', function () {
    it('should open the search page and load default results', function () {
        cy.visit('/ote/#/services');
        // Expect to have some default results
        cy.get('.service-search').find('.result-title').should('have.length.above', 0)
    });
});

// TODO: Test service search page (palvelukatalogi) features

describe('Operator search page basic tests', function () {

    // Login in only once before tests run
    before(() => {
        cy.login();
    });

    beforeEach(() => {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();

        cy.visit('/ote/#/operators');
    });


    it('should render operator page', () => {
        cy.contains('Palveluntuottajat');
    });
});

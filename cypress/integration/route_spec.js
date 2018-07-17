describe('Sea route tests', function () {
    before(function () {
        cy.login();
    });

    beforeEach(function () {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();
    });


    it('Create new route', function () {
        cy.visit('/#/routes/');

        cy.get('tr').should('have.length', 1);

        cy.contains('Lisää uusi laivareitti').click();
    });

    it('Add basic information', function () {
        cy.get('[id*="Reitinnimi"]').type('Test route').should('have.value', 'Test route');

        cy.get('[id*="departure-point-name"]').type('Oulu').should('have.value', 'Oulu');
        cy.get('[id*="destination-point-name"]').type('Vaasa').should('have.value', 'Vaasa');
    });

    it('Add stops', function () {
        cy.get('.leaflet-control-zoom-out').click().wait(400);
        cy.get('.leaflet-control-zoom-out').click().wait(400);
        cy.get('.leaflet-control-zoom-out').click().wait(400);
        cy.get('.leaflet-control-zoom-out').click().wait(400);
        cy.get('.leaflet-control-zoom-out').click();
        cy.get('img[title="Puerto Mont"]').click();
        cy.get('img[title="Panama"]').click();
        cy.get('img[title="Los Angeles"]').click();
        cy.get('img[title="Middleton Island"]').click();
    });

    it('Add calendar', function () {
        cy.get('#add-route-button').should('be.disabled');

        cy.get('#button_0').first().click();

        cy.get('[id*="date-picker-from-date"]').click();
        cy.document().its('body').type('{enter}');

        cy.get('[id*="date-picker-to-date"]').click();
        cy.document().its('body').type('{downarrow}{enter}');

        cy.get('#row_0 > :nth-child(3)').click();
        cy.get('#row_0 > :nth-child(4)').click();
        cy.get('#row_0 > :nth-child(5)').click();
        cy.get('#row_0 > :nth-child(6)').click();
        cy.get('#row_0 > :nth-child(7)').click();

        cy.get('#new-calendar-period-button').click()
            .should('is.disabled');

        cy.contains('button', 'Hyväksy').click();
    });

    it('Add route times', function () {
        let hour = 1;

        cy.get('.route-times')
            .within($route => {
                cy.get('input[id*="hours-tt-nul"]').each(el => {
                    cy.wrap(el).type(hour++);
                });
            });

        cy.get('.route-times')
            .within($route => {
                cy.get('input[id*="minutes-mm-nul"]').each(el => {
                    cy.wrap(el).type('00');
                });
            });

        cy.get('#add-route-button').should('be.enabled');
    });

    it('Save template', function () {
        cy.contains('Tallenna luonnoksena').click();
        cy.get('tr').should('have.length', 2);
    });

    it('Delete template', function () {
        cy.get('[id*="delete-route"]').click();
        cy.contains('button', 'Poista').click();
        cy.get('tr').should('have.length', 1);
    });
});

describe('Sea route tests', function () {
    before(function () {
        cy.login();
    });

    beforeEach(function () {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();
        cy.fixture('services/schedule.json').as('service');
    });


    it('Create new route', function () {
        cy.visit('/#/routes/');

        //cy.get('tr').should('have.length', 1);

        cy.contains('Lisää uusi merireitti').click();
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
        cy.get('img[title="Puerto Mont"]').last().click();
        cy.get('img[title="Panama"]').last().click();
        cy.get('img[title="Los Angeles"]').last().click();
        cy.get('img[title="Middleton Island"]').last().click();
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
                cy.get('input[id*="-hours"]').each(el => {
                    cy.wrap(el).type(hour++);
                });
            });

        cy.get('.route-times')
            .within($route => {
                cy.get('input[id*="-minutes"]').each(el => {
                    cy.wrap(el).type('00');
                });
            });

        cy.get('#add-route-button').should('be.enabled');
    });

    it('Save template', function () {
        cy.contains('Tallenna ja julkaise').click();
        cy.contains('Reitin tallennus onnistui');
    });

    it('Link route to service - create new service', function () {
        const service = this.service;
        cy.visit('/#/own-services');

        // Add new schedule type service
        cy.server();
        cy.route('POST', '/transport-service').as('addService');


        cy.get('a[id*="new-service-button"]').click({force: true});

        cy.get('[id*="Valitseliikkumispalveluntyyppi"]').click();

        cy.server();
        cy.route('/place-completions/*').as('placeCompletion');

        cy.contains(/^Säännöllinen*/).click();
        cy.contains('Jatka').click();

        // Fill mandatory fields
        cy.get('input[id*=":road"]').click();
        cy.get('input[id*="Palvelun nimi-"]').type("Liitä merireitti tähän");
        cy.get('input[name="place-auto-complete-primary"]').as('areaInput');

        cy.wrap(service.areas).each(area => {
            cy.get('@areaInput').type(area);

            return cy.wait('@placeCompletion')
                .then(() => {
                    cy.contains(area).click();
                    return cy.wait(2000);
                });
        });

        cy.get("input[name=':ote.db.transport-service/advance-reservation']").first().click();
        cy.contains('Tallenna ja julkaise').click({force: true});
        cy.wait('@addService');
        cy.wait(1000);

    });

    it('Link route to new service', function () {
        // Link route to schedule service
        cy.visit('/#/routes/');
        cy.wait(200)
        cy.contains('Valmiiden reittien liittäminen palveluun');
        cy.contains('Valitse alapuolelta palvelut, joihin haluat liittää valmiit merenkulun reitit.');
        cy.contains('Et ole liittänyt valmiita merenkulun reittejä vielä yhteenkään palveluun.');
        cy.contains('Liitä merireitti tähän');

        // Select service using checbox
        cy.server();
        cy.route('POST','/routes/link-interface').as('linkInterface');
        cy.get('input[id*=checkbox-Liitä]').first().click();
        cy.wait('@linkInterface');
        cy.contains('Rajapinta liitettiin palveluun onnistuneesti.');

        //Unlink route
        cy.get('input[id*=checkbox-Liitä]').first().click();
        cy.wait('@linkInterface');
        cy.contains('Rajapinta poistettiin palvelulta onnistuneesti.');
    });

    it('Delete public route', function () {
        cy.visit('/#/routes/');
        cy.get('div.public').get('[id*="delete-route"]').last().click();
        cy.contains('button', 'Poista').click();
        cy.contains('Test route').should('not.exist');
    });

    it('Delete schedule service', function () {
        cy.visit('/#/own-services');
        cy.server();
        cy.route('POST', '/transport-service/delete').as('deleteService');

        cy.contains('tr', 'Liitä merireitti tähän')
            .within($tr => {
                cy.get('a').last().click();
            });

        cy.contains('button', 'Poista').click();

        cy.wait('@deleteService');
    });

});

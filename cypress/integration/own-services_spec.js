// TODO: Test own services page (Omat palvelutiedot) features

import { randomName } from '../support/util';
import { genNaughtyString } from "../support/generators";

describe('Own services basic tests', function () {
    // Login in only once before tests run
    before(function () {
        cy.login();
    });

    beforeEach(function () {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();

        cy.visit('/#/own-services' );
    });

    it('should render the own services page', function () {
        cy.contains('Lisää uusi palvelu');
    });

    it('should redirect to add service view', function () {
        cy.get('a[id*="new-service-button"]').click({force: true});
    });

    it('should redirect to add new service provider view', function () {
        cy.contains('Lisää uusi palveluntuottaja')
            .click({force: true});
    });
});

describe('Add a new service', function () {
    before(function () {
        cy.login();
    });

    beforeEach(function () {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();
    });

    describe('Taxi service', function () {
        before(function () {
            cy.wrap(randomName('test-service-')).as('serviceName')
        });

        beforeEach(function () {
            cy.fixture('services/taxi.json').as('service');
        });

        it('should should add a new service', function () {
            const service = this.service;

            cy.server();
            cy.route('POST', '/transport-service').as('addService');

            cy.visit('/#/own-services');
            cy.get('a[id*="new-service-button"]').click({force: true});

            cy.get('[id*="Valitseliikkumispalveluntyyppi"]')
                .click();


            cy.server();
            cy.route('/place-completions/*').as('placeCompletion');

            cy.contains(/^Taksi*/).click();
            cy.contains('Jatka').click();

            // Fill mandatory fields
            cy.get('input[id*="name--Palvelunnimi"]').type(this.serviceName);
            cy.get('input[name="street"]').typeRaw(genNaughtyString(20));
            cy.get('input[name="postal_code"]').type(service.contact.postal_code);
            cy.get('input[name="post_office"]').typeRaw(genNaughtyString(20));
            cy.get('input[name="place-auto-complete-primary"]').as('areaInput');

            cy.wrap(service.areas).each(area => {
                cy.get('@areaInput').type(area);

                return cy.wait('@placeCompletion')
                    .then(() => {
                        cy.contains(area).click();

                        return cy.wait(2000);
                    });
            });
            // ## Add some data for non-mandatory fields

            cy.get('#radio-company-csv-url').click();
            cy.get('input[id*="companies-csv-url-"').typeRaw(genNaughtyString(100));
            cy.get('textarea[id*="luggage-restrictions--Matkatavaroitakoskevatrajoitukset-"')
                .typeRaw(genNaughtyString(100));

            // Get parent el form-group-container that contains the following text
            cy.contains('.form-group-container', 'Reaaliaikapalvelun verkko-osoitetiedot')
                .within(() => {
                    cy.get('input[id*="url--Palvelunverkko-osoite"]').typeRaw(genNaughtyString(100));

                    cy.get('textarea[id*="description--Palvelunkuvaus"]').typeRaw(genNaughtyString(100));
                });

            // Get parent el form-group-container that contains the following text
            cy.contains('.form-group-container', 'Varauspalvelun osoitetiedot')
                .within($el => {
                    cy.get('input[id*="url--Palvelunverkko-osoite"]').typeRaw(genNaughtyString(100));

                    cy.get('textarea[id*="description--Palvelunkuvaus"]').typeRaw(genNaughtyString(100));
                });

            cy.get("input[name=':ote.db.transport-service/advance-reservation']").first().click();

            cy.contains('Tallenna ja julkaise').click({ force: true });
            cy.wait('@addService');
        });

        it('should delete the test service', function () {
            cy.server();
            cy.route('POST', '/transport-service/delete').as('deleteService');

            cy.visit('/#/own-services');

            cy.contains('tr', this.serviceName)
                .within($tr => {
                    cy.get('a').last().click();
                });

            cy.contains('button', 'Poista').click();

            cy.wait('@deleteService');
        });
    });

});

/*describe('Add new service provider', function () { // TODO: remake this when ckan is removed
    before(function () {
        cy.login();
        cy.wrap(randomName('test-operator-')).as('operatorName')
    });

    beforeEach(function () {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();

        cy.visit('/#/own-services');
    });

    // Currently these test won't work in PR run. Because we do not have ckan there.
    xit('should add new tranport operator', function () {

        cy.server();
        cy.route('POST', '/transport-operator').as('addOperator');
        cy.route('GET' ,'/t-operator/').as('openOperatorPage');

        // Get the service selector with partial id (i.e. id-attribute contains the desired "Valitsepalveluntuottaja"-substring).
        cy.get('[id*="Valitsepalveluntuottaja"]').click();
        cy.contains('Lisää uusi palveluntuottaja').click();
        cy.wait('@openOperatorPage');

        cy.get('input[id*="name--Palveluntuottajannimi-"]').type(this.operatorName);
        cy.get('input[id*="business-id--Y-tunnus-"]').type('1231233-3');
        cy.contains('button', 'Tallenna').click({ force: true });
        cy.wait('@addOperator');
        cy.contains('Palveluntarjoajan tiedot tallennettu');
    });

    xit('should delete added tranport operator', function () {
        cy.server();
        cy.route('POST', '/transport-operator/delete').as('deleteOperator');

        cy.get('[id*="Valitsepalveluntuottaja"]').click();
        cy.contains(this.operatorName).click();
        cy.contains('button', 'Muokkaa').click();
        cy.contains('button', 'Poista palveluntuottaja').click();
        cy.get('#confirm-operator-delete').contains('Poista').click();

        cy.wait('@deleteOperator');

        cy.contains('Palveluntuottaja poistettiin onnistuneesti.');
    });
});*/

/*describe('Should add new associated service to Normal users "Terminaali Oy" and delete them', function () { TODO: remake this also when ckan is removed

    const testOperator = {
        businessid: '7654321-9',
        name: randomName(),
    };

    before(function() {
        cy.normalLogin();
        cy.wrap(randomName('test-service-')).as('serviceName')
    });

    beforeEach(function () {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();
        cy.fixture('services/taxi.json').as('service');
        cy.visit('/#/own-services');
    });

    it('Should be in own-services page and have a provider added', function () {
        cy.get('input[id=chip-input]');  //chip input won't be rendered if there are no service providers
    });

    // TODO: replace xit with it when ytj integration is added to production.

    xit('Should create a new operator for current user', function () {
        cy.get('[id=btn-add-new-transport-operator]').click();
        cy.get('[id=input-business-id]').type(testOperator.businessid);
        cy.get('[id=btn-submit-business-id]').click();
        cy.get('[id=input-operator-name]').type(testOperator.name);
        cy.get('[id=btn-operator-save]').click();
    });

    xit('should should add a new service to new operator', function () {
        const service = this.service;

        cy.get('[id=select-operator-at-own-services]').click();
        cy.contains(testOperator.name).click();

        cy.server();
        cy.route('POST', '/transport-service').as('addService');

        cy.get('a[id*="new-service-button"]').click();
        cy.get('[id*="Valitseliikkumispalveluntyyppi"]')
        .click();

        cy.server();
        cy.route('/place-completions/!*').as('placeCompletion');

        cy.contains(/^Taksi*!/).click();
        cy.contains('Jatka').click();

        // Fill mandatory fields
        cy.get('input[id*="name--Palvelunnimi"]').type(this.serviceName);
        cy.get('input[name="street"]').typeRaw(genNaughtyString(20));
        cy.get('input[name="postal_code"]').type(service.contact.postal_code);
        cy.get('input[name="post_office"]').typeRaw(genNaughtyString(20));
        cy.get('input[name="place-auto-complete-primary"]').as('areaInput');

        cy.wrap(service.areas).each(area => {
            cy.get('@areaInput').type(area);
            return cy.wait('@placeCompletion')
            .then(() => {
                cy.contains(area).click();
                return cy.wait(2000);
            });
        });

        // ## Add some data for non-mandatory fields
        cy.get('#radio-company-csv-url').click();
        cy.get('input[id*="companies-csv-url-"').typeRaw(genNaughtyString(100));
        cy.get('textarea[id*="luggage-restrictions--Matkatavaroitakoskevatrajoitukset-"')
        .typeRaw(genNaughtyString(100));

        // Get parent el form-group-container that contains the following text
        cy.contains('.form-group-container', 'Reaaliaikapalvelun verkko-osoitetiedot')
        .within(() => {
            cy.get('input[id*="url--Palvelunverkko-osoite"]').typeRaw(genNaughtyString(100));
            cy.get('textarea[id*="description--Palvelunkuvaus"]').typeRaw(genNaughtyString(100));
        });

        // Get parent el form-group-container that contains the following text
        cy.contains('.form-group-container', 'Varauspalvelun osoitetiedot')
        .within($el => {
            cy.get('input[id*="url--Palvelunverkko-osoite"]').typeRaw(genNaughtyString(100));
            cy.get('textarea[id*="description--Palvelunkuvaus"]').typeRaw(genNaughtyString(100));
        });

        cy.get("input[name=':ote.db.transport-service/advance-reservation']").first().click();
        cy.contains('Tallenna ja julkaise').click({ force: true });
        cy.wait('@addService');
    });


    xit('Should add a new associated service', function () {
        cy.get('input[id=chip-input]').type(this.serviceName);
        cy.contains(this.serviceName).click();
        cy.get('li[id*="service-id"]').contains(this.serviceName);
    });

    xit('Should delete added associated service', function() {
        cy.get('li[id*="service-id"]').contains(this.serviceName);
        cy.get('li').contains(this.serviceName).find('button').click();
        cy.get('li[id*="service-id"]').should('not.exist');
    });

    xit('should delete the test service', function () {
        cy.get('[id=select-operator-at-own-services]').click();
        cy.contains(testOperator.name).click();

        cy.server();
        cy.route('POST', '/transport-service/delete').as('deleteService');

        cy.contains('tr', this.serviceName)
        .within($tr => {
            cy.get('a').last().click();
        });

        cy.contains('button', 'Poista').click();

        cy.wait('@deleteService');
    });

    xit('Should delete the operator created earlier', () => {
        cy.get('[id=select-operator-at-own-services]').click();
        cy.contains(testOperator.name).click();
        cy.get('[id=edit-transport-operator-btn]').click();
        cy.get('[id=delete-transport-operator-btn]').click();
        cy.get('[id=confirm-operator-delete]').click();
    });
});*/

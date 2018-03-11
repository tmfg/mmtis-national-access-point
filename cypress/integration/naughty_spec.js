import { randomName } from '../support/util';
import { genNaughtyString } from "../support/generators";

/**
 * NOTE: We are using a lot of .typeRaw() instead of .type() here, because we cant use ".type()" for special
 * characters. Also, we do not need to waste time for typing the input, because actual user input simulation is tested
 * in other specs. This tests just how we handle string naughtiness.
 */



describe('Naughty form fill', () => {
    before(() => {
        cy.login();
    });

    beforeEach(() => {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();
    });

    describe('Taxi service', () => {
        const serviceName = randomName('test-service-');

        beforeEach(() => {
            cy.fixture('services/taxi.json').as('service');
        });

        it('should should add a new service', () => {
            cy.visit('/ote/#/new-service');

            cy.get('[id*="Valitseliikkumispalveluntyyppi"]')
                .click();


            cy.server();
            cy.route('/ote/place-completions/*').as('placeCompletion');

            cy.contains(/^Taksi*/).click();
            cy.contains('Jatka').click();

            // Fill mandatory fields

            cy.get('input[id*="name--Palvelunnimi"]').typeRaw(serviceName);

            cy.get('@service').then(service => {
                cy.get('input[name="street"]').typeRaw(genNaughtyString(20));
                cy.get('input[name="postal_code"]').typeRaw(service.contact.postal_code);
                cy.get('input[name="post_office"]').typeRaw(genNaughtyString(20));

		cy.get('input[name="place-auto-complete-primary"]').as('areaInput');
                cy.get('@areaInput').type(service.areas[0]);
                return cy.wait('@placeCompletion')
                    .then(() => {
                        cy.contains(service.areas[0]).click();

                        return cy.wait(2000);
                    });
            });

            cy.contains('.form-field', 'Olen lukenut tämän osion')
                .within($field => {
                    cy.get('input[type="checkbox"]').check();
                });

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

            cy.contains('Tallenna ja julkaise').click();
        });

        it('should delete the test service', () => {
            cy.server();
            cy.route('POST', '/ote/transport-service/delete').as('deleteService');

            cy.visit('/ote/#/own-services');

            cy.contains('tr', serviceName)
                .within(() => {
                    cy.get('a').last().click();
                });

            cy.contains('button', 'Poista').click();

            cy.wait('@deleteService');
        });
    });
});

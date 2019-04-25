// Pre notice tests

describe('Pre notice tests', () => {
    before(() => {
        cy.clearCookies();
        cy.login();
    });

    beforeEach(() => {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();
        cy.getCookies()
            .should('have.length', 1)
            .then((cookies => {
                expect(cookies[0]).to.have.property('name', 'auth_tkt');
            }));
    });


    it('should render pre-notice list page', () => {
        cy.visit('/#/pre-notices');
        cy.contains('Omat säännöllisen henkilöliikenteen muutosilmoitukset');
        cy.contains('Lähetetyt muutosilmoitukset');
    });

    it('should render pre-notice form page', () => {
        cy.visit('/#/pre-notices');
        cy.server();
        cy.route('GET', '/pre-notices/list').as('getPreNotices');
        // Wait for pre-notices query because it causes re-render of the view.
        cy.wait('@getPreNotices');

        cy.get('#add-new-pre-notice').click();
        cy.contains('Säännöllisen henkilöliikenteen muutosilmoitus');
        cy.contains('Ilmoitettavan muutoksen tyyppi');
        cy.contains('Muutoksen alkamispäivä tai -päivät');
        cy.contains('Reitin ja alueen tiedot');
        cy.contains('Muutoksen tarkemmat tiedot');
        cy.contains('Tallenna ja lähetä');
        cy.contains('Tallenna luonnoksena');

    });

    it('save and send pre-notice', () => {
        // Open pre notice form
        cy.visit('/#/pre-notices');
        cy.server();
        cy.route('GET', '/pre-notices/list').as('getPreNotices');

        // Wait for pre-notices query because it causes re-render of the view.
        cy.wait('@getPreNotices');

        cy.get('#add-new-pre-notice').click({"force": true});

        // Select type
        cy.get('[type="checkbox"]').first().click();

        // Give a description

        cy.get('[id*=Kuvaathnmuutoksentarkempisisltlyhyesti-Muutoksentarkemmattiedot]').type("Kuvaus");


        // Select random effective date
        cy.get('#effective-dates-table #row_0 input').first().click();
        cy.document().its('body').type('{enter}');

        // Give reason to make the change at given time
        cy.get('#effective-dates-table #row_0 [name=effective-date-description]').type("Koska halusin");

        // Describe route
        cy.get('#route-description').type('Oulu - Kajaani');

        // Select Route area from list
        cy.get('[id*="Lismaakuntataimaakunnatjoitamuutoskoskee"]').type('Uusim{enter}');
        cy.get('[id*="Lismaakuntataimaakunnatjoitamuutoskoskee"]').type('pohjanm{downarrow}{downarrow}{enter}');

        cy.contains('Tallenna ja lähetä').click();
        cy.get('#confirm-send-pre-notice').click();
    });

    it('open the pre-notice and check region', () => {
        // Open pre notice form
        cy.visit('/#/pre-notices');
        cy.server();
        cy.route('GET', '/pre-notices/list').as('getPreNotices');

        // Wait for pre-notices query because it causes re-render of the view.
        cy.wait('@getPreNotices');

        cy.route('GET', '/pre-notices/*').as('getPreNotice');
        cy.route('GET', '/pre-notices/regions').as('getRegions');
        cy.route('GET', '/pre-notices/region/*').as('getRegion');


        // Go to edit pre-notice view
        cy.get('table.sent tr:last-child span.edit-pre-notice a').click({force: true});

        // Wait for pre-notice edit form queries because it causes re-render of the view.
        cy.wait('@getPreNotice');
        cy.wait('@getRegions');
        cy.wait('@getRegion');

        cy.get('#route-description').contains('Oulu - Kajaani');

        // two regions drawn on the map
        cy.get('div.leaflet-container').find('path.leaflet-interactive').should('have.length', 2);
    });

});

describe('Authority pre notice tests', () => {
    before(() => {
        cy.login();
    });

    beforeEach(() => {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();
    });

    it('should render authority pre notice list', () => {
        cy.get('.header-general-menu').click();

        cy.get('div.container.general-menu').within($el => {
            // Dropdown menu links
            cy.contains('Saapuneet muutosilmoitukset').click();

        });

        cy.contains('Säännöllisen henkilöliikenteen muutosilmoitukset');
        cy.contains('Oulu - Kajaani');
        cy.contains('Ilmoitustyyppi');
    });

    it('should open authority pre notice modal', () => {

        cy.get('.header-general-menu').click();

        cy.get('div.container.general-menu').within($el => {
            // Dropdown menu links
            cy.contains('Saapuneet muutosilmoitukset').click();

        });

        // Open Modal
        cy.get('table tbody tr td').contains('Oulu - Kajaani').click();

        // Check that modal contains something
        cy.contains('Muutosilmoituksen tiedot');
        cy.contains('Uusimaa');
        cy.contains('Etelä-Pohjanmaa');
        cy.contains('Liikenteen lakkauttaminen (osittain tai kokonaan)');
        cy.contains('Koska halusin');
        cy.contains('Sulje').click();
    });
});

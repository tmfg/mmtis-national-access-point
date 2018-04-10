describe('Sea route tests', function () {

    before (function () {
	cy.login();
    });

    beforeEach(function () {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();
    });


    it ("Create new route", function () {
	cy.visit ("/ote/#/routes/");

	cy.wait(500);

	cy.get('tr').should('have.length', 1);

	cy.contains(/Lisää uusi merireitti/).click();
    });

    it ("Add basic information", function () {
	cy.get('[id*="Reitinnimi"]').type("Test route").should('have.value', "Test route");
	cy.get('[id*="Palveluntuottaja"]').click();
	cy.contains(/Testinen/).click();
	cy.get('[id*="departure-point-name"]').type("Oulu").should('have.value', "Oulu");
	cy.get('[id*="destination-point-name"]').type("Vaasa").should('have.value', "Vaasa");
	cy.get('[id*="undefined--Voimassaalkaen"]').click();
	cy.get(':nth-child(3) > :nth-child(4) > span').click();
	cy.get('[id*="undefined--Voimassaasti"]').click();
	cy.get(':nth-child(5) > :nth-child(4) > span').click();
    });

    it ("Add stops", function () {
	cy.get('img[title="OULUN SATAMA: Vihreäsaari"]').click();
	cy.get('img[title="RAAHE: Rautaruukki"]').click();
	cy.get('img[title="KALAJOKI: Itäkenttä"]').click();
	cy.get('img[title="VAASA: Lassenlaituri"]').click();
    });

    it ("Add calendar", function () {
	cy.get("#add-route-button").should('be.disabled');

	cy.get('a[id="button_0"]').first().click();

	cy.get('[id*="date-picker-from-date"]').click();
	cy.get(':nth-child(2) > :nth-child(4) > span').click();

	cy.get('[id*="date-picker-to-date"]').click();
	cy.get(':nth-child(4) > :nth-child(4) > span').click();

	cy.get('#row_0 > :nth-child(3)').click();
	cy.get('#row_0 > :nth-child(4)').click();
	cy.get('#row_0 > :nth-child(5)').click();
	cy.get('#row_0 > :nth-child(6)').click();
	cy.get('#row_0 > :nth-child(7)').click();

	cy.get('[id*="new-calendar-period"]').click();

	cy.contains('button[id="new-calendar-period"]').should('not.exist');

	cy.contains('button', 'Sulje').click();
    });

    it ("Add route times", function () {
	var hour = 1
	cy.get('.route-times')
	    .within($route => {
		cy.get('input[id*="hours-tt-nul"]').each(el => {
		    cy.wrap(el).type(hour++);
		})
	    });

	cy.get('.route-times')
	    .within($route => {
		cy.get('input[id*="minutes-mm-nul"]').each(el => {
		    cy.wrap(el).type("00");
		    console.log(el);
		})
	    });

	cy.get("#add-route-button").should('be.enabled');
    });

    it ("Save template", function () {
	cy.contains('Tallenna luonnoksena').click();
	cy.wait(500);
	 cy.get('tr').should('have.length', 2);
    });

    it ("Delete template", function () {
	cy.get(':nth-child(2) > a > div > svg').click();
	cy.contains('button', 'Poista').click();
	cy.get('tr').should('have.length', 1);
    });

});

describe('Sea route tests', function () {

    before (function () {
	cy.login();
    });

    it ("Add basic information", function () {
	cy.visit ("/ote/#/route/new")
	cy.get ('input[id*="name--Reitinnimi"').type("Test route").should('have.value', "Test route");
	cy.get('input[id*="departure-point-name"').type("Oulu").should('have.value', "Oulu");
	cy.get('input[id*="destination-point-name"').type("Vaasa").should('have.value', "Vaasa");
	cy.get('input[id*="undefined--Voimassaalkaen"').click();
	cy.get(':nth-child(3) > :nth-child(4) > span').click();
	cy.get('input[id*="undefined--Voimassaasti"').click();
	cy.get(':nth-child(5) > :nth-child(4) > span').click();
    });

    it ("Add stops", function () {
	cy.get('img[title="OULUN SATAMA: Vihreäsaari"').click();
	cy.get('img[title="RAAHE: Rautaruukki"').click();
	cy.get('img[title="KALAJOKI: Itäkenttä"').click();
	cy.get('img[title="VAASA: Lassenlaituri"').click();
    });

    it ("Add routes", function () {
	cy.contains('button[id="add-route-button"]').should('not.exist');
	cy.get('button[id="add-route-button"]').should('be.disabled');

	var hour = 1
	cy.get('.route-times')
	    .within($route => {
		cy.get('input[id*="hours-tt-nul"]').each(el => {
		    cy.wrap(el).type(hour++);
		    console.log(el);
		})
	    });

	cy.get('.route-times')
	    .within($route => {
		cy.get('input[id*="minutes-mm-nul"]').each(el => {
		    cy.wrap(el).type("00");
		    console.log(el);
		})
	    });

	//	cy.get('a[data-name="calendar-stop-button"]').first().click();
    })
});

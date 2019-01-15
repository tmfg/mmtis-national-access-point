
describe('Operator creation basic tests', function () {
    const testOp1 = {
        businessId: "1234567-9",
        name: "e2eAcmeCorp",
        addrBillingStreet: "Billing Street",
        addrBillingPostalCode: "999999",
        addrBillingCity: "Megalopolis",
        addrVisitStreet: "Visiting Street",
        addrVisitPostalCode: "000000",
        addrVisitCity: "Hyperpolis",
        mobilePhone: "123 12345678",
        telephone: "555 4567890",
        email: "e2eacmecorp@localhost",
        web: "www.acmecorp.acme"
    };

    // Login in only once before tests run
    before(function () {
        cy.login();
    });

    beforeEach(function () {
        // Session cookies will not be cleared before the NEXT test starts
        cy.preserveSessionOnce();

        cy.visit('/#/own-services');
    });

    it('should navigate to own services', function () {
        cy.contains('Omat palvelutiedot');
    });

    it('should validate and invalidate business id', function () {

        // Uncomment these when YTJ service is stubbed and creation is tested fully
        // cy.get('#select-operator-at-own-services').click();
        // cy.contains(testOp1.name).should('not.exist');
        // cy.contains('Omat palvelutiedot').click();

        cy.get('#btn-add-new-transport-operator').click();
        cy.get('#btn-submit-business-id').as('btnSubmit')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('#input-business-id').type('1')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('#input-business-id').type('2')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('#input-business-id').type('3')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('#input-business-id').type('4')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('#input-business-id').type('5')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('#input-business-id').type('6')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('#input-business-id').type('7')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('#input-business-id').type('-')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('#input-business-id').type('9')
        cy.get('#btn-submit-business-id').should('be.enabled')
        cy.get('#input-business-id').type('9')
        cy.get('#btn-submit-business-id').should('be.disabled')
        cy.get('#input-business-id').type('{backspace}')
        cy.get('#btn-submit-business-id').should('be.enabled')


        // Uncomment these whe YTJ
        // cy.get('@btnSubmit').click()
        // cy.wait(3000);
        //
        // cy.get('#input-operator-name').type(testOp1.name)
        // cy.get('#input-operator-addrBillingStreet').type(testOp1.addrBillingStreet)
        // cy.get('#input-operator-addrBillingPostalCode').type(testOp1.addrBillingPostalCode)
        // cy.get('#input-operator-addrBillingCity').type(testOp1.addrBillingCity)
        // cy.get('#input-operator-addrVisitStreet').type(testOp1.addrVisitStreet)
        // cy.get('#input-operator-addrVisitPostalCode').type(testOp1.addrVisitPostalCode)
        // cy.get('#input-operator-addrVisitCity').type(testOp1.addrVisitCity)
        // cy.get('#input-operator-telephone').type(testOp1.telephone)
        // cy.get('#input-operator-mobilePhone').type(testOp1.mobilePhone)
        // cy.get('#input-operator-email').type(testOp1.email)
        // cy.get('#input-operator-web').type(testOp1.web)
        //
        // cy.get('#btn-operator-save').click()
        //
        // cy.get('#select-operator-at-own-services').contains(testOp1.name)

// TODO: test delete test operator
    });

});

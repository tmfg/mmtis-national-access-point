import { randomName } from '../support/util';


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
        web: "www.acmecorp.acme",
        businessIdExistsWarning: 'Antamasi Y-tunnus löytyy jo NAP:sta. Ota yhteys NAP-Helpdeskiin asian selvittämiseksi, joukkoliikenne@traficom.fi',
        businessIdInvalidWarding: 'Y-tunnuksen tulee olla muotoa: 7 numeroa, väliviiva ja tarkistusnumero.'

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

    it('should navigate to add new operator', function () {
        cy.contains('Omat palvelutiedot');
        cy.get('#btn-add-new-transport-operator').click();
        cy.contains('Lisää palveluntuottaja');
    });

    it('should not be able to save operator with invalid business id', function () {
        cy.contains('Omat palvelutiedot');
        cy.get('#btn-add-new-transport-operator').click();
        cy.get('#input-business-id').type('1234');
        // Check warning
        cy.contains(testOp1.businessIdInvalidWarding);

        // Save button should be disabled
        cy.get('#btn-operator-save').should('be.disabled');
        cy.visit('/#/');
        cy.get('#btn-confirm-leave-page').click();
    });

    it('should add new operator', function () {

        cy.server();
        cy.route('POST', '/transport-operator').as('addOperator');
        cy.route('POST', '/transport-operator/delete').as('deleteOperator');

        cy.contains('Omat palvelutiedot');

        // Start adding new operator
        cy.get('#btn-add-new-transport-operator').click();

        // Business id is already added
        cy.get('#input-business-id').type('1234567-8');
        // Check warning
        cy.contains(testOp1.businessIdExistsWarning);

        // Try creating random business-id
        cy.get('#input-business-id').clear(); // Clear business-id
        cy.get('#input-business-id').type('9988776-9');
        cy.contains(testOp1.businessIdExistsWarning).should('not.exist');

        // Add name
        const newOperatorName = randomName('ctest-op-');
        cy.get('#input-operator-name').type(newOperatorName);

        // Save
        cy.get('#btn-operator-save').click();

        // Ensure that operator is correctly added - should be selected by default
        cy.wait('@addOperator');
        cy.contains('Palveluntarjoajan tiedot tallennettu');
        cy.contains(newOperatorName);

        // Ensure that other pages have it selected by default
        cy.visit('/#/pre-notices');
        cy.contains(newOperatorName);
        cy.visit('/#/routes');
        cy.contains(newOperatorName);

        // Delete operator
        cy.visit('/#/own-services');
        cy.get('#edit-transport-operator-btn').click();
        cy.get('#btn-delete-transport-operator').click();
        cy.get('#confirm-operator-delete').click({force: true});

        // Ensure that operator is correctly deleted
        cy.wait('@deleteOperator');
        cy.contains('Palveluntuottaja poistettiin onnistuneesti.');

    });


    xit('should validate and invalidate business id', function () {

        // Uncomment these when YTJ service is stubbed and creation is tested fully
        // cy.get('#select-operator-at-own-services').click();
        // cy.contains(testOp1.name).should('not.exist');
        // cy.contains('Omat palvelutiedot').click();
/*
        cy.get('#btn-add-new-transport-operator').click();
        cy.get('#btn-submit-business-id').as('btnSubmit')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('#input-business-id').as('inputBid').type('1')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('@inputBid').type('2')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('@inputBid').type('3')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('@inputBid').type('4')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('@inputBid').type('5')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('@inputBid').type('6')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('@inputBid').type('7')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('@inputBid').type('-')
        cy.get('@btnSubmit').should('be.disabled')
        cy.get('@inputBid').type('9')
        cy.get('#btn-submit-business-id').should('be.enabled')
        cy.get('@inputBid').type('9')
        cy.get('#btn-submit-business-id').should('be.disabled')
        cy.get('@inputBid').type('{backspace}')
        cy.get('#btn-submit-business-id').should('be.enabled')
*/

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

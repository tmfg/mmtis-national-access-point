describe('Should render service-viewer page', function () {
    it('Page should have atleast proper header tags', function () {
        cy.visit('/#/service/1/1');

        cy.get('h1').contains("Ajopalvelu Testinen Oy");
        cy.get('#service-name').contains("Paljon kuvausta ja tarinaa");
    });
});
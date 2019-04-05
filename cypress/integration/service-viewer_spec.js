describe('Should render service-viewer page', function () {
    it('Page should have atleast proper header tags', function () {
        cy.visit('/#/services').wait(500);
        cy.get('#all-info-link').click().wait(400);
        cy.get('h1');
        cy.get('#service-name');
    });
});
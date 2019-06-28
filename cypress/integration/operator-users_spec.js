describe('Operator user control', function() {

  const operatorName = 'Ajopalvelu Testinen Oy';
  const userEmail = 'user.userson@example.com';
  const userName = 'User Userson';

  before(function() {
    cy.login();
  });

  beforeEach(function() {
    cy.preserveSessionOnce();
    cy.visit('/#/own-services');
    cy.server();
    cy.route({
      method: 'POST',
      url: '/transport-operator/*/users'
    }).as('postMember');

    cy.route({
      method: 'DELETE',
      url: '/transport-operator/*/users'
    }).as('deleteMember');

    cy.get('#select-operator-at-own-services').click();
    cy.contains(operatorName).click({ force:true });
    cy.get('#operator-users-link').click();
  });

  it('Should add existing User Userson as a member of Ajopalvelu Testinen Oy', function() {
    cy.get('h2').contains(operatorName);
    cy.get('#operator-user-email').type(userEmail);
    cy.get('#add-member').click();
    cy.wait('@postMember').then((resp) => {
      expect(resp.status).to.eq(200);
    });
    cy.get('#user-table-container').contains(userName);
  });


  it('Should remove the added member from Ajopalvelu Testinen Oy', function() {
    cy.get('h2').contains(operatorName);
    cy.contains(userName).parent().find('#remove-member').click({force: true});
    cy.get('#confirm-delete').click();
    cy.wait('@deleteMember').then((resp) => {
      expect(resp.status).to.eq(200);
    });
  });

  it('User list should no longer contain the removed user', function() {
    cy.get('h2').contains(operatorName);
    cy.get('#user-table-container').contains(userName).should('not.exist');
  });

});

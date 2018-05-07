// Pre notice tests

describe('Pre notice form tests', function () {

    it('should render pre-notice list page', () => {
        cy.visit('/#/pre-notices');
        cy.contains('Omat säännöllisen henkilöliikenteen muutosilmoitukset');
        cy.contains('Lähetetyt muutosilmoitukset');
    });

    it('should render pre-notice form page', () => {
        cy.contains('Uusi muutosilmoitus').click();
        cy.contains('Säännöllisen henkilöliikenteen muutosilmoitus');
        cy.contains('Ilmoitettavan muutoksen tyyppi');
        cy.contains('Muutoksen alkamispäivä tai -päivät');
        cy.contains('Reitin ja alueen tiedot');
        cy.contains('Muutoksen tarkemmat tiedot');
        cy.contains('Tallenna ja lähetä');
        cy.contains('Tallenna luonnoksena');



    });

});

@screen
Feature: Oblio

  Scenario: Get invoices from ANAF
    And in Google Sheets I get all items from Factura
    And I open url "https://www.oblio.eu/report/wallet"
    And I login in Oblio
    And in Oblio I get all invoices
    And I stop test

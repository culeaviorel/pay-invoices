@screen
Feature: ANAF

  Scenario: ANAF
    And in Google Sheets I get all items from Factura
    And I open url "https://anaf.ro"
    And I login in ANAF
    And in ANAF I get all invoices for 60 days

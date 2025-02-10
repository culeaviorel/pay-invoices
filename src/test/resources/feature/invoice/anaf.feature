@screen
Feature: ANAF

  Scenario: ANAF
    And in ANAF I get all items from Fractura
    And I open url "https://anaf.ro"
    And I login in ANAF
    And in ANAF I get all invoices for 60 days

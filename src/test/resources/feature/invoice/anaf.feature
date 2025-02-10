@screen
Feature: ANAF

  Scenario: ANAF
    And I open url "https://anaf.ro"
    And I login in ANAF
    And in ANAF I get all invoices for 30 days

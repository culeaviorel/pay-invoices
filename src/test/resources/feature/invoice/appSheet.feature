@screen
Feature: App Sheet Invoices

  Scenario: Open AppSheet
    And I open url "https://www.appsheet.com/start/6ab480fa-d9c8-4d57-aab6-4e4c2f615b76"
#    And I save cookies
    And in AppSheet I add following values:
      | name | category   | subcategory | price | payment | date             |
      | Lild | Cheltuieli | Alimentare  | 12.5  | Card    | 08/20/2025 18:00 |

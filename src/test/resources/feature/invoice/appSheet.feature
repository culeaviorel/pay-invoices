@screen
Feature: App Sheet Invoices

  Scenario: Open AppSheet
    And I open url "https://www.appsheet.com/start/6ab480fa-d9c8-4d57-aab6-4e4c2f615b76"
#    And I save cookies
#    And in AppSheet I add following values:
#      | name               | category   | subcategory | price   | payment | date       |
#      | Lild | Cheltuieli | Alimentare  | 12.5  | Card    | 08/20/2025 18:00 |
#      | Rata Martie        | Cheltuieli | Rata        | 2016.85 | Card    | 03/15/2026 |
#      | Rata Aprilie       | Cheltuieli | Rata        | 2086.27 | Card    | 04/15/2026 |
#      | Rata Mai           | Cheltuieli | Rata        | 2081.68 | Card    | 05/15/2026 |
#      | Chirie Februarie   | Venituri   | Chirie      | 2525    | Card    | 02/02/2026 |
#      | Chirie Martie      | Venituri   | Chirie      | 2525    | Card    | 03/02/2026 |
#      | Chirie Aprilie     | Venituri   | Chirie      | 2525    | Card    | 04/02/2026 |
#      | Chirie Mai         | Venituri   | Chirie      | 2525    | Card    | 05/02/2026 |
#      | Alocatie Februarie | Venituri   | Alocatie    | 860     | Card    | 02/10/2026 |
#      | Alocatie Martie    | Venituri   | Alocatie    | 860     | Card    | 03/10/2026 |
#      | Alocatie Aprilie   | Venituri   | Alocatie    | 860     | Card    | 04/10/2026 |
#      | Alocatie Mai       | Venituri   | Alocatie    | 860     | Card    | 05/10/2026 |
#      | Alocatie Ianuarie   | Venituri | Alocatie    | 860   | Card    | 01/10/2026 |

    And in AppSheet I edit following values:
      | name           | category   | subcategory | price   | payment | date       |
#      | Lild | Cheltuieli | Alimentare  | 12.5  | Card    | 08/20/2025 18:00 |
#      | Rata Noiembrie | Cheltuieli | Rata        | 2137.30 | Card    | 11/15/2025 |
#      | Rata Decembrie | Cheltuieli | Rata        | 2095.11 | Card    | 12/15/2025 |
      | Rata Ianuarie  | Cheltuieli | Rata        | 2127.51 | Card    | 01/15/2026 |
      | Rata Februarie | Cheltuieli | Rata        | 2159.62 | Card    | 02/15/2026 |
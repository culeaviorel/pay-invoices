@screen
Feature: Transcrieri

  Scenario: Transcrieri
    And I open url "https://transcrieri.evp-oradea.ro/formulare/c_prog1.php"
    And I make transcriere
      | file      | email                 | name           | nr     | data       | tara    |
      | Vadim.pdf | ucraina77@givmail.com | Yeremiya Vadym | 160935 | 04.02.2025 | Ucraina |
#  myIP: 188.24.2.54
#  myIpMobile: 82.77.155.138, 04.07.2025
  # myIPTibi: 188.27.128.3, 04.07.2025


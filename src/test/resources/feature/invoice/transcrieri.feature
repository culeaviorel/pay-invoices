@screen
Feature: Transcrieri

  Scenario: Transcrieri
    And I open url "https://transcrieri.evp-oradea.ro/formulare/c_prog1.php"
    And I make transcriere
      | file      | email                 | name           | nr       | data       | tara    |
      | Maxim.pdf | dritacita34@gmail.com | Davidean Maxim | 27255254 | 28.03.2025 | Ucraina |
#      | Gabriel.pdf | culeaviorel@gmail.com | Paslar Gabriele | 36752 | 08.11.2024 | Ucraina |
#  myIP: 188.24.2.151
#  myIpMobile: 82.77.155.138, 04.07.2025
  # myIPTibi: 188.27.128.3, 04.07.2025


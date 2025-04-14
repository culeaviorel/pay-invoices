@screen
Feature: Transcrieri

  Scenario: Transcrieri
    And I open url "https://transcrieri.evp-oradea.ro/formulare/c_prog1.php"
    And I make transcriere
      | file        | email                 | name            | nr    | data       | tara    |
#      | Maxim.pdf   | culeaviorel@gmail.com | Davidean Maxim  | 27255254 | 28.03.2025 | Ucraina |
      | Gabriel.pdf | culeaviorel@gmail.com | Paslar Gabriele | 36752 | 08.11.2024 | Ucraina |


@screen
Feature: As a Customer I pay all my invoices

#  Scenario: Donatii cu destinatie speciala in BTGo
#    And I prepare data for Donatii cu destinatie speciala New from google sheet
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I send Donatii cu destinatie speciala from google sheet

#  Scenario: Sustinere educatie in BTGo
#    And I prepare data for Sustinere educatie from google sheet
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I send Sustinere educatie from google sheet

#  Scenario: Save reports din BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I save report from "Ianuarie" month

#  Scenario: Plateste orice factura in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I pay invoices:
#      | fileName      | category  | value | furnizor                  | iban                     | nr          | description   |
#      |          | Materiale Grup | 700   | ASOCIATIA CURSUL ALPHA ROMANIA | RO28BTRLRONCRT0372089601 | 1  | Cursuri pentru grupuri |
#      | Factura37.pdf | Inchinare | 0     | AMA DEUM MUSICA SRL | RO80BTRLRONCRT0CU5601301 | 0397 | Cursuri canto |
#      | Factura37.pdf | Inchinare | 480   | MUSIC STUDIO THE BEAT SRL | RO57BTRLRONCRT0CJ0776801 | MSTB2025134 | Cursuri canto |
#      | Factura32.pdf | Femei    | 671.16 | KRAFTCHAIN ENTERPRISES SRL | RO53INGB0000999912169965 | 311 | atelier (Momco) |
#      | Factura18.pdf | Mentenanta | 250   | S.C HARRER HEATING SRL | RO61BTRLRONCRT0665737301 | 0211 | verificarea centralei |
#      | SCRISOARE DONATIE  09.12.2024.pdf | IesireaBiserica | 4386  | Asociatia Actiunea Felix | RO30CECEBH0143RON0496616 | 217 | donatie cazare comitet |

#  Scenario: Plateste donatiile in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I pay invoices:
#      | category | value | furnizor                                         | description |
###      | RVE      | 12000 | Asociatia RADIO VOCEA EVANGHELIEI sucursala Cluj | donatie     |
###      | CredoTV     | 2000  | ASOCIATIA CREDO TELEVISION NETWORK | donatie     |
##      | SeerRomania | 500   | Fundatia Seer Romania | donatie     |
#      | Sf.NectarieOut | 3500  | Asociatia Sfantul Nectarie Cluj | donatie     |

#  Scenario: Plateste factura de utilitati in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I pay invoices:
#      | fileName                       | category |
#      | Factura_CASOMES_CAG5168984.pdf | Apa      |
#      | FacturaGazFeb.pdf | Gaz      |
#      | Vanzari Factura CJL1C000751193.pdf | Gunoi    |

#  Scenario: Generate extras conturi in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And I generate extras from all in BTGo

#  Scenario: Move all from Cont Current to Depozit in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And I move all from Cont Current to Depozit in BTGo

#    Scenario: Create depozit from Cont de Economii in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And I create depozit from Cont de Economii in BTGo

#  Scenario: Add facturile sau bonuri in google sheets
#    And I add in Facturi or Bonuri in google sheet:
#      | fileName      | type    | plata | category | data       | value  | description |
#      | DovadaPlataSomethingNewDecembrie.pdf   | Dovada | Cont  | SomethingNewOut  | 13.12.2024 | 371   | plata       |
#      | DovadaPlataTeenChallengeDecembrie.pdf  | Dovada | Cont  | TeenChallengeOut | 13.12.2024 | 100   | plata       |
#      | DovadaPlataCasaFilipDecembrie.pdf      | Dovada | Cont  | CasaFilipOut     | 13.12.2024 | 200   | plata       |
#      | DovadaPlataTanzaniaDecembrie.pdf       | Dovada | Cont  | TanzaniaOut      | 13.12.2024 | 140   | plata       |
#      | DovadaPlataCaminulFelixDecembrie.pdf   | Dovada | Cont  | FelixOut         | 13.12.2024 | 216   | plata       |
#      | Factura152.pdf | Factura | Cash  | Dotari   | 17.11.2024 | 449.99 | Router 3         |
#      | Factura173.pdf | Factura | Cash  | Mentenanta | 19.12.2024 | 262.40 | Tablou electric |
#      | Factura176.pdf | Factura    | Cash  | Adolescenti | 20.12.2024 | 224.51 | Pizza (Simona)                     |
#      | WhatsApp Image 2024-12-22 at 20.23.38.jpeg | Factura    | Cash  | Copiii   | 15.12.2024 | 50.64 | Produce pentru copii (Damaris) |
#      | WhatsApp Image 2024-12-22 at 20.23.47.jpeg | Bon cu CUI | Cash  | Copiii   | 02.12.2024 | 115   | Produce pentru copii (Damaris) |
#      | Factura184.pdf                             | Bon cu CUI | Cash  | Copiii   | 16.12.2024 | 69.98 | Produce pentru copii (Damaris) |
#      | Factura36.pdf | Factura | Cont  | Femei    | 25.02.2025 | 154.70 | Farurii     |
#      | DispozitieDePlata5.pdf | Factura | Cash  | Invitati   | 10.01.2025 | 600    | pentru Claudiu Pop           |
#      | DispozitieDePlata6.pdf | Factura | Cash  | Invitati   | 12.01.2025 | 1500   | pentru Marius Cruceru        |
#      | DispozitieDePlata8.pdf | Factura | Cash  | Conferinta | 09.02.2025 | 100    | conferinta evanghelizare  |
#      | Factura12.pdf          | Factura | Cont  | Invitati   | 12.01.2025 | 2289   | cazare Marius Cruceru + mese |
#      | Factura33.pdf | Bon cu CUI | Cash  | Alimentare | 22.02.2025 | 306.40 | Produse alimentare (Mariana) |
#      | Factura19.pdf          | Factura | Cont  | Alimentare | 08.02.2025 | 125.38 | Cafea si lapte            |
#      | Factura26.pdf | Factura    | Cash  | Alimentare | 11.02.2025 | 417.99 | Pahare si farfurii (Doru)        |
#      | Factura27.pdf | Factura    | Cont  | Alimentare | 15.02.2025 | 324.95 | Cafea                            |
#      | Factura28.pdf | Factura    | Cash  | Alimentare | 16.02.2025 | 144.48 | Sucuri (Matei)                   |
#      | Factura29.pdf | Factura    | Cash  | Alimentare | 16.02.2025 | 694.13 | Susi (Florin)                    |
#      | Factura30.pdf | Factura    | Cash  | Alimentare | 16.02.2025 | 359.02 | Pizza (Florin)                   |
#      | Factura25.pdf          | Factura | Cash  | Diverse    | 06.02.2025 | 188.91 | Semnatura digitala        |
#      | Factura8.pdf           | Factura | Cont  | Alimentare | 09.01.2025 | 234.51 | Pizza pentru invitat      |
#      | Factura10.pdf          | Factura | Cont  | Alimentare | 10.01.2025 | 194.26 | Produse pentru conferinta    |
#      | Factura11.pdf          | Factura | Cont  | Alimentare | 12.01.2025 | 776.55 | Pizza pentru biserica        |
#      | Factura31.pdf | Bon cu CUI | Cash  | Femei      | 16.02.2025 | 120    | Flori pentru invitata (Catalina) |
#      | Factura3.pdf | Factura    | Cont  | Alimentare | 07.01.2025 | 238.01 | Produse pentru invitati |
#      | Factura167.pdf | Factura | Cash  | Sanitare    | 09.12.2024 | 81.91  | Role, Saci (Doru)                |
#      | Factura34.pdf | Factura    | Cash  | Sanitare   | 16.02.2025 | 49.95  | Hartie egienica (Doru)       |
#      | Factura35.pdf | Bon cu CUI | Cash  | Copiii     | 22.02.2025 | 121.37 | pentru copii (Renata)        |
#      | 4191379477.pdf | Factura | Cash  | Copiii   | 04.02.2025 | 152.00 | Pizza pentru invatatori (Damaris) |
#      | Factura166.pdf | Factura | Cash  | Adolescenti | 14.07.2024 | 725.14 | Produse pentru adolescenti (Oti) |
#      | Factura174.pdf | Bon cu CUI | Cash  | Adolescenti | 18.12.2024 | 212.5  | Produse pentru adolescenti (Patri) |
#      | Factura175.pdf | Bon cu CUI | Cash  | Adolescenti | 18.12.2024 | 69.62  | Produse pentru adolescenti (Patri) |
#      | Bonuri-facturi tineri-dec-1.pdf   | Bon cu CUI | Cash  | Tineri     | 16.12.2024 | 201    | Produse pentru tineri (Ovidiu) |
#      | Bonuri-facturi tineri-dec-2-3.pdf | Bon cu CUI | Cash  | Tineri     | 16.12.2024 | 376    | Produse pentru tineri (Ovidiu) |
#      | Factura181.pdf | Factura | Cont  | Tineri     | 28.12.2024 | 203.80  | Pungi de cadouri              |
#      | Factura17.pdf | Factura | Cont  | Diverse  | 29.01.2025 | 461   | Pixuri      |
#      | SCRISOARE DONATIE  09.12.2024.pdf | Donatie | Cont  | Diverse  | 08.11.2024 | 109.48 | Hartie A4   |
#      | BibileProject2024.pdf | Donatie | Cash  | BibleProject | 28.12.2024 | 109.48 | Donatie     |
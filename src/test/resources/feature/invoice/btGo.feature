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
#    And in BTGo I save report from "Martie" month

#  Scenario: Plateste orice factura in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I pay invoices:
#      | fileName      | category  | value | furnizor                  | iban                     | nr          | description   |
#      |          | TitluriDeStat | 470000.00 | BT Capital Partners | RO96BTRL01301202925690XX | 3  | titluri de stat (3) |
#      |          | Materiale Grup | 700   | ASOCIATIA CURSUL ALPHA ROMANIA | RO28BTRLRONCRT0372089601 | 1  | Cursuri pentru grupuri |
#      | Factura39.pdf | Inchinare | 1080  | AMA DEUM MUSICA SRL | RO80BTRLRONCRT0CU5601301 | AS0438 | Cursuri canto |
#      | Factura69.pdf | Inchinare | 480   | MUSIC STUDIO THE BEAT SRL | RO57BTRLRONCRT0CJ0776801 | MSTB2025212 | Cursuri canto |
#      | Factura55.pdf | SchimbDestinatie | 7197.95 | SC BEJAN & PARTNERS TEAM SRL | RO81BTRLRONCRT0535890801 | BEJ0254 | Avans avizare ISU |
#      | BIS BAPT 11.pdf | Adolescenti | 2000  | Fundatia ELPIS | RO04BTRLRONCRT0093426202 | FE044 | avans tabara |
#      | Contract donatie Momco.pdf | Femei    | 900   | ASOCIATIA MOPS CLUJ | RO72BTRLRONCRT0642499201 | CD01 | conferinta MomCo |
#      | Factura18.pdf | Mentenanta | 250   | S.C HARRER HEATING SRL | RO61BTRLRONCRT0665737301 | 0211 | verificarea centralei |
#      | SCRISOARE DONATIE  09.12.2024.pdf | IesireaBiserica | 4386  | Asociatia Actiunea Felix | RO30CECEBH0143RON0496616 | 217 | donatie cazare comitet |
#      | Factura59.pdf | Conferinta | 2727.00 | SC SEGRA COM SRL | RO40BTRLRONCRT0P23874101 | FCT0052598 | mancare conferinta |

#  Scenario: Plateste donatiile in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I pay invoices:
#      | category          | value | furnizor                               | description                   | iban                     |
#      | Sustinere familii | 10000 | Asociatia pentru Integritatea Familiei | Donatie pentru Marius Cruceru | RO18BTRLRONCRT0320656501 |
#      | ProVitaOut | 1000  | Fundatia Clinica Pro-vita | Donatie pentru Marsul pt Viata | RO98BTRL01301205R83319XX |
###      | RVE      | 12000 | Asociatia RADIO VOCEA EVANGHELIEI sucursala Cluj | donatie     |
###      | CredoTV     | 2000  | ASOCIATIA CREDO TELEVISION NETWORK | donatie     |
##      | SeerRomania | 500   | Fundatia Seer Romania | donatie     |
#      | Comunitate | 1000  | Comunitatea Bisericilor Crestine Baptiste Cluj    | donatie     |                          |
#      | Comunitate | 1000  | Uniunea Bisericilor Crestine Baptiste din Romania | donatie     | RO26RNCB0072049718910001 |

#  Scenario: Plateste factura de utilitati in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I pay invoices:
#      | fileName                           | category |
#      | Factura43.pdf | Apa      |
#      | FacturaGazFeb.pdf | Gaz      |
#      | Vanzari Factura CJL1C000782345.pdf | Gunoi    |

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
#      | fileName      | type       | plata | category    | data       | value  | description                        |
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
#      | Factura73.pdf | Factura | Cash  | Femei    | 02.04.2025 | 40.00  | Panglica Momco            |
#      | Factura74.pdf | Factura | Cash  | Femei    | 02.04.2025 | 28.00  | Pliante Momco             |
#      | Factura75.pdf | Factura | Cash  | Femei    | 02.04.2025 | 207.77 | Produse Momco (Andreea S) |
#      | Factura72.pdf | Factura | Cont  | Tehnic   | 02.04.2025 | 3158.00 | multi efect chitara |
#      | Factura53.pdf | Factura | Cont  | Dotari   | 12.03.2025 | 942   | mese de la IKEA |
#      | DispozitieDePlata6.pdf | Factura | Cash  | Invitati   | 12.01.2025 | 1500   | pentru Marius Cruceru        |
#      | Factura60.pdf | Factura | Cash  | Conferinta | 18.03.2025 | 826.01 | produse conferinta      |
#      | Factura62.pdf | Factura | Cash  | Femei      | 09.03.2025 | 68.20  | Creioane (Dana Copaciu) |
#      | Factura71.pdf | Factura | Cont  | Femei    | 26.03.2025 | 195.88 | produse pentru Mops |
#      | Factura12.pdf          | Factura | Cont  | Invitati   | 12.01.2025 | 2289   | cazare Marius Cruceru + mese |
#      | Factura33.pdf | Bon cu CUI | Cash  | Alimentare | 22.02.2025 | 306.40 | Produse alimentare (Mariana) |
#      | Factura19.pdf          | Factura | Cont  | Alimentare | 08.02.2025 | 125.38 | Cafea si lapte            |
#      | Factura63.pdf | Factura | Cash  | Decor      | 10.03.2025 | 229.00 | Decor 1 (Dana Copaciu)  |
#      | Factura64.pdf | Factura | Cash  | Decor      | 10.03.2025 | 229.00 | Decor 2 (Dana Copaciu)  |
#      | Factura68.pdf | Factura | Cash  | Alimentare | 19.03.2025 | 195.38 | Cafea       |
#      | Factura65.pdf | Factura | Cash  | Alimentare | 22.03.2025 | 108.87 | Produse (Pop Ioana)     |
#      | Factura66.pdf | Factura | Cash  | Alimentare | 20.03.2025 | 172.79 | Produse (Pop Ioana)     |
#      | Factura67.pdf | Factura | Cash  | Alimentare | 09.02.2025 | 336.52 | Pizza (Florin)          |
#      | Factura29.pdf | Factura    | Cash  | Alimentare | 16.02.2025 | 694.13 | Susi (Florin)                    |
#      | Factura30.pdf | Factura    | Cash  | Alimentare | 16.02.2025 | 359.02 | Pizza (Florin)                   |
#      | Factura54.pdf | Factura | Cont  | Diverse  | 05.03.2025 | 18.00 | Taxa anuala de custodie |
#      | Factura61.pdf | Factura | Cash  | Sanitare   | 18.03.2025 | 85.92  | Produse sanitare (Doru) |
#      | Factura48.pdf | Factura | Cash  | Sanitare   | 19.02.2025 | 39.95  | Saci (Doru)                                    |
#      | Factura70.pdf | Factura | Cash  | Tiparituri | 25.03.2025 | 205.98 | Hartie A4   |
#      | Factura47.pdf | Factura | Cash  | Alimentare | 10.01.2025 | 227.58 | Apa (Doru)                                     |
#      | Factura11.pdf          | Factura | Cont  | Alimentare | 12.01.2025 | 776.55 | Pizza pentru biserica        |
#      | Factura31.pdf | Bon cu CUI | Cash  | Femei      | 16.02.2025 | 120    | Flori pentru invitata (Catalina) |
#      | Factura49.pdf | Factura | Cash  | Femei      | 08.03.2025 | 539.94 | Produse pentru intalnirea femeilor (Valentina) |
#      | Factura3.pdf | Factura    | Cont  | Alimentare | 07.01.2025 | 238.01 | Produse pentru invitati |
#      | Factura41.pdf | Factura | Cont  | Sanitare | 03.03.2025 | 600   | pahare cina |
#      | Factura34.pdf | Factura    | Cash  | Sanitare   | 16.02.2025 | 49.95  | Hartie egienica (Doru)       |
#      | Factura76.pdf | Factura    | Cash  | Adolescenti | 02.04.2025 | 123.05 | Produse pentru adolescenti (Oti)   |
#      | Factura77.pdf | Factura    | Cash  | Comunitate  | 03.04.2025 | 400.00 | Slujire pentru comunitate (Florin) |
#      | Factura78.pdf | Factura    | Cash  | Femei       | 05.04.2025 | 220.57 | Produse pentru Momco (Andreea)     |
#      | Factura79.pdf | Bon cu CUI | Cash  | Copiii      | 06.04.2025 | 251.16 | pentru copii (Simona)              |
#      | Factura174.pdf | Bon cu CUI | Cash  | Adolescenti | 18.12.2024 | 212.5  | Produse pentru adolescenti (Patri) |
#      | Factura175.pdf | Bon cu CUI | Cash  | Adolescenti | 18.12.2024 | 69.62  | Produse pentru adolescenti (Patri) |
#      | Bonuri-facturi tineri-dec-1.pdf   | Bon cu CUI | Cash  | Tineri     | 16.12.2024 | 201    | Produse pentru tineri (Ovidiu) |
#      | Bonuri-facturi tineri-dec-2-3.pdf | Bon cu CUI | Cash  | Tineri     | 16.12.2024 | 376    | Produse pentru tineri (Ovidiu) |
#      | FF tineri March 24.pdf | Factura | Cash  | Tineri   | 24.03.2025 | 126.82 | produse pentru tineri |
#      | Factura17.pdf | Factura | Cont  | Diverse  | 29.01.2025 | 461   | Pixuri      |
#      | SCRISOARE DONATIE  09.12.2024.pdf | Donatie | Cont  | Diverse  | 08.11.2024 | 109.48 | Hartie A4   |
#      | BibileProject2024.pdf | Donatie | Cash  | BibleProject | 28.12.2024 | 109.48 | Donatie     |
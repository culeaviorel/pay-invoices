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
#    And in BTGo I save report from "Septembrie" month

#  Scenario: Plateste orice factura in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I pay invoices:
#      | fileName       | decizia | category | value  | furnizor                  | iban                     | nr          | description        |
#      |          | TitluriDeStat | 470000.00 | BT Capital Partners | RO96BTRL01301202925690XX | 3  | titluri de stat (3) |
#      |          | Materiale Grup | 700   | ASOCIATIA CURSUL ALPHA ROMANIA | RO28BTRLRONCRT0372089601 | 1  | Cursuri pentru grupuri |
#      | Factura116.pdf | Inchinare | 270.00 | AMA DEUM MUSICA SRL | RO80BTRLRONCRT0CU5601301 | AS0727 | Cursuri canto |
#      | Factura108.pdf | Inchinare | 530   | MUSIC STUDIO THE BEAT SRL | RO57BTRLRONCRT0CJ0776801 | MSTB2025311 | Cursuri canto |
#      | Factura117.pdf | SchimbDestinatie | 1600.19 | SC BEJAN & PARTNERS TEAM SRL | RO81BTRLRONCRT0535890801 | BEJ0264 | Emitere aviz ISU |
#      | Factura144.pdf | Adolescenti | 2043.47 | MICOTEX PROMOTION ADVERTISING SRL | RO72BTRLRONCRT0208589101 | MPA6490 | Tricouri    |
#      | Factura195.pdf |         | Femei    | 661.00 | ASOCIATIA ORGANIZATIA CRESTINA SOMETHING NEW | RO73BTRLRONCRT0203806401 | SNEW0052 | pentru MomCo |
#      | Factura18.pdf | Mentenanta | 250   | S.C HARRER HEATING SRL | RO61BTRLRONCRT0665737301 | 0211 | verificarea centralei |
#      | Factura93.pdf | IesireaBiserica | 44405.00 | OZONE CITY SRL | RO89RNCB0298154735940001 | OC0529 | cazare cu biserica |
#      | Factura59.pdf | Conferinta | 2727.00 | SC SEGRA COM SRL | RO40BTRLRONCRT0P23874101 | FCT0052598 | mancare conferinta |
#      | Factura225.pdf |         | Copiii   | 283.50 | CARMEL PRINT & DESIGN SRL | RO81BTRL00201202182658XX | 2025CPD0829 | carti pentru copii |

#  Scenario: Plateste donatiile in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I pay invoices:
#      | fileName                | decizia                      | category   | value   | furnizor                                          | description                          | iban                     |
#      | Contract de donatie nr10 din 2025.08.08.pdf | Decizie comitet donatie6.pdf | Adolescenti | 8000.00 | Asociația Raza Soarelui-Sunlight | Donatie conform contract donatie 10 din 08.08.2025 | RO23RZBR0000060003200106 |
#      | DispozitieDePlata24.pdf | Decizie comitet donatie7.pdf | DonatiiOut | 4357.30 | Comunitatea Bisericilor Creștine Baptiste Suceava | pentru sinistratii din Suceava-Neamt | RO38RNCB0234037009660015 |
#      | CONTRACT DE DONATIE MomCo.pdf | Femei    | 300   | Asociatia Mops Cluj | Donatie     | RO72BTRLRONCRT0642499201 |
#      | Sustinere familii | 10000 | Asociatia pentru Integritatea Familiei | Donatie pentru Marius Cruceru | RO18BTRLRONCRT0320656501 |
#      | ProVitaOut | 1000  | Fundatia Clinica Pro-vita | Donatie pentru Marsul pt Viata | RO98BTRL01301205R83319XX |
#      |          | RVE      | 2000  | Asociatia RADIO VOCEA EVANGHELIEI sucursala Cluj | donatie     | RO38RNCB0106026613860001 |
#      | Decizie comitet donatie2.pdf | CredoTV  | 10000.00 | ASOCIATIA CREDO TELEVISION NETWORK | donatie     | RO28RNCB0074029224060001 |
##      | SeerRomania | 500   | Fundatia Seer Romania | donatie     |
#      | Comunitate | 1000  | Comunitatea Bisericilor Crestine Baptiste Cluj    | donatie     |                          |
#      | Comunitate | 1000  | Uniunea Bisericilor Crestine Baptiste din Romania | donatie     | RO26RNCB0072049718910001 |

#  Scenario: Plateste factura de utilitati in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I pay invoices:
#      | fileName       | category |
#      | Factura214.pdf | Apa      |
#      | FacturaGazFeb.pdf | Gaz      |
#      | Factura215.pdf | Gunoi    |

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

#  Scenario: Plata decont in BTGo
#    And I open url "https://goapp.bancatransilvania.ro/app/auth/login"
#    And I login in BTGo
#    And in BTGo I pay deconts:
#      | decont       | category |
#      | Decont1.pdf | Alimentare |
#      | Decont14.pdf | Copiii   |
#      | Decont13.pdf | Adolescenti |
#      | Decont15.pdf | Sanitare |

#  Scenario: Test
#    And I upload file "DovadaPlataCopiiiIulie.pdf"
#    And I read pdf "C:\Users\vculea\Desktop\ExtrasDeCont.pdf"

  Scenario: Add facturile sau bonuri in google sheets
    And I add in Facturi or Bonuri in google sheet:
      | fileName            | decizia | decont | type   | plata | category | data       | value   | description      |
#      | DovadaPlataSomethingNewDecembrie.pdf   | Dovada | Cont  | SomethingNewOut  | 13.12.2024 | 371   | plata       |
#      | DovadaPlataTeenChallengeDecembrie.pdf  | Dovada | Cont  | TeenChallengeOut | 13.12.2024 | 100   | plata       |
#      | DovadaPlataCasaFilipDecembrie.pdf      | Dovada | Cont  | CasaFilipOut     | 13.12.2024 | 200   | plata       |
#      | DovadaPlataTanzaniaDecembrie.pdf       | Dovada | Cont  | TanzaniaOut      | 13.12.2024 | 140   | plata       |
#      | DovadaPlataCaminulFelixDecembrie.pdf   | Dovada | Cont  | FelixOut         | 13.12.2024 | 216   | plata       |
#      | DispozitieDePlata24.pdf | Decizie comitet donatie7.pdf |        | Dovada | Cash  | DonatiiOut | 24.08.2025 | 4357.30 | sustinere misionarilor din Nepal (1000$) |
#      | DispozitieDePlata16.pdf | Decizie comitet premiere participanti olimpiada.pdf |             | Dovada  | Cash  | Copiii     | 01.06.2025 | 200    | premium olimpiada Estera, Sofia        |
#      | DispozitieDePlata17.pdf | Decizie comitet premiere participanti olimpiada.pdf |             | Dovada  | Cash  | Copiii     | 01.06.2025 | 300    | premium olimpiada Siena, Titus, Andrei |
#      | DispozitieDePlata20.pdf | Decizie comitet premiere participanti olimpiada.pdf |        | Dovada  | Cash  | Copiii     | 08.06.2025 | 200    | premium olimpiada David, Ionatan |
#      | DispozitieDePlata25.pdf | Decizie comitet donatie9.pdf |        | Dovada | Cash  | Sustinere alte biserici | 25.09.2025 | 1000.00 | donatie pentru familia Husar |
      | DovadaDepuneri3.pdf |         |        | Dovada | Cash  | In Cont  | 20.10.2025 | 1700.00 | depunere in cont |
#      | Factura152.pdf | Factura | Cash  | Dotari   | 17.11.2024 | 449.99 | Router 3         |
#      | Factura173.pdf | Factura | Cash  | Mentenanta | 19.12.2024 | 262.40 | Tablou electric |
#      | Factura176.pdf | Factura    | Cash  | Adolescenti | 20.12.2024 | 224.51 | Pizza (Simona)                     |
#      | Factura73.pdf | Factura | Cash  | Femei    | 02.04.2025 | 40.00  | Panglica Momco            |
#      | Factura99.pdf |            |        | Factura | Cont  | Femei      | 10.05.2025 | 234.00 | Sandviciuri pentru Momco |
#      | Factura97.pdf |            |        | Factura | Cont  | Femei    | 09.05.2025 | 58.79 | Produse Momco |
#      | Factura72.pdf | Factura | Cont  | Tehnic   | 02.04.2025 | 3158.00 | multi efect chitara |
#      | Factura53.pdf | Factura | Cont  | Dotari   | 12.03.2025 | 942   | mese de la IKEA |
#      | DispozitieDePlata11.pdf | Dovada | Cash  | Diverse  | 13.04.2025 | 275.00 | la tehnic pentru CT |
#      | Factura60.pdf | Factura | Cash  | Conferinta | 18.03.2025 | 826.01 | produse conferinta      |
#      | Factura90.pdf |            |        | Factura | Cash  | Femei    | 30.04.2025 | 149.75 | Plasturi    |
#      | Factura94.pdf | ExtrasCard4.pdf | Decont2.pdf | Factura | Cash  | IesireaBiserica | 01.05.2025 | 72.88 | Produse pentru iesire |
#      | Factura92.pdf |            |        | Chitanta | Cash  | IesireaBiserica | 02.05.2025 | 3500.00 | Donatie Fundatia Emanuel |
#      | Factura71.pdf | Factura | Cont  | Femei    | 26.03.2025 | 195.88 | produse pentru Mops |
#      | Factura12.pdf          | Factura | Cont  | Invitati   | 12.01.2025 | 2289   | cazare Marius Cruceru + mese |
#      | Factura222.pdf |         |        | Bon cu CUI | Cash  | Alimentare | 12.10.2025 | 169.86 | Ceai si snacuri (Simona) |
#      | Factura223.pdf |         |        | Bon cu CUI | Cont  | Alimentare | 12.10.2025 | 144.93 | Ceai si snacuri          |
#      | Factura63.pdf | Factura | Cash  | Decor      | 10.03.2025 | 229.00 | Decor 1 (Dana Copaciu)  |
#      | Factura64.pdf | Factura | Cash  | Decor      | 10.03.2025 | 229.00 | Decor 2 (Dana Copaciu)  |
#      | Factura208.pdf |         |        | Factura | Cash  | Deplasari  | 28.09.2025 | 476.09 | Drum Ilva (Florin) |
#      | Factura209.pdf |         |        | Factura | Cash  | Deplasari  | 28.09.2025 | 576.07 | Drum Ilva (Florin) |
#      | Factura130.pdf |            |        | Factura | Cont  | Diverse  | 25.06.2025 | 218.09 | Decont motorina Chiuiesti |
#      | Factura113.pdf          |                                                     |        | Factura | Cash  | Sanitare   | 04.06.2025 | 181.54 | Produse sanitare (Doru)          |
#      | Factura226.pdf |         |        | Factura | Cont  | Sanitare | 20.10.2025 | 20.30  | Produse pentru curatenie |
#      | Factura227.pdf |         |        | Factura | Cont  | Sanitare | 20.10.2025 | 285.95 | Produse pentru curatenie |
#      | Factura228.pdf |         |        | Factura | Cont  | Sanitare | 20.10.2025 | 182.13 | Produse pentru curatenie |
#      | Factura198.pdf |         |        | Factura | Cont  | Sanitare | 11.09.2025 | 1117.62 | Produse pentru curatenie |
#      | Factura70.pdf | Factura | Cash  | Tiparituri | 25.03.2025 | 205.98 | Hartie A4   |
#      | Factura31.pdf | Bon cu CUI | Cash  | Femei      | 16.02.2025 | 120    | Flori pentru invitata (Catalina) |
#      | Factura218.pdf |         | Decont17.pdf | Factura | Cont  | Femei    | 11.10.2025 | 192.10 | Produse pentru MomCo (A.Sainuc) |
#      | Factura134.pdf | ExtrasCard17.pdf |        | Factura    | Cont  | Alimentare | 12.07.2025 | 145.39 | Cafea si lapte                     |
#      | Factura167.pdf |            |        | Factura | Cash  | Sanitare    | 10.08.2025 | 99.05  | Domestos (Doru)             |
#      | Factura210.pdf |         |        | Factura | Cont  | Adolescenti | 02.10.2025 | 161.09 | Mancare adolescenti |
#      | Factura205.pdf |         |        | Factura | Cont  | Adolescenti | 25.09.2025 | 121.21 | Mancare adolescenti |
#      | Factura211.pdf |         | Decont13.pdf | Factura | Cash  | Adolescenti | 18.09.2025 | 236.82 | Mancare adolescenti (Damaris) |
#      | Factura77.pdf | Factura    | Cash  | Comunitate  | 03.04.2025 | 400.00 | Slujire pentru comunitate (Florin) |
#      | Factura112.pdf          | ExtrasCard10.pdf                                    |        | Factura | Cash  | Femei      | 07.06.2025 | 503.99 | Produse pentru Momco (Ovidu)     |
#      | Factura115.pdf          | ExtrasCard11.pdf                                    |        | Factura | Cont  | Femei      | 04.06.2025 | 735.00 | Produse pentru Momco             |
#      | Factura145.pdf | ExtrasCard12.pdf |        | Factura    | Cont  | Copiii   | 12.06.2025 | 38.95  | marker, plicuri Olimpiada |
#      | Factura155.pdf |            |        | Factura | Cash  | Copiii   | 22.07.2025 | 91.10  | pentru Tabara |
#      | Factura156.pdf |            |        | Factura | Cash  | Copiii   | 26.07.2025 | 662.52 | pentru Tabara |
#      | Factura154.pdf |            |        | Factura | Cont  | Copiii   | 27.07.2025 | 916.06 | pizza pentru Copii |
#      | Factura184.pdf |            |        | Factura | Cash  | Copiii   | 17.04.2025 | 570.00 | pentru Copii |
#      | Factura216.pdf |         | Decont14.pdf | Bon cu CUI | Cash  | Copiii   | 14.07.2025 | 54.90 | transport tabara (A.Sainiuc) |
#      | Factura193.pdf |         |              | Factura    | Cash  | Copiii   | 31.08.2025 | 526.01 | transport tabara olimpiada (Florin) |
#      | Factura175.pdf | Bon cu CUI | Cash  | Adolescenti | 18.12.2024 | 69.62  | Produse pentru adolescenti (Patri) |
#      | Factura135.pdf | ExtrasCard18.pdf |        | Factura | Cont  | Adolescenti | 12.07.2025 | 3500.07 | Produse pentru adolescenti |
#      | Factura152.pdf | ExtrasCard25.pdf |        | Factura | Cont  | Adolescenti | 13.07.2025 | 520.70 | Produse pentru tabara de adolescenti |
#      | Factura165.pdf |            |        | Factura | Cash  | Adolescenti | 11.08.2025 | 567.07 | Transport sioniada (Florin) |
#      | Factura166.pdf |            |        | Factura | Cash  | Adolescenti | 16.08.2025 | 596.04 | Transport sioniada (Florin) |
#      | Factura168.pdf |            |        | Factura | Cash  | Adolescenti | 09.08.2025 | 584.98 | Transport sioniada (Florin) |
#      | Factura143.pdf |                  |        | Bon cu CUI | Cont  | Adolescenti | 19.07.2025 | 135.89 | Decont transport adolescenti         |
#      | Factura161.pdf |            |        | Factura | Cont  | Adolescenti | 14.07.2025 | 131.69 | Produse pentru tabara de adolescenti |
#      | Factura136.pdf |            |        | Bon cu CUI | Cont  | Adolescenti | 15.07.2025 | 114.52 | Decont transport adolescenti |
#      | Factura128.pdf | ExtrasCard9.pdf |        | Bon cu CUI | Cash  | Tineri   | 02.06.2025 | 80.54 | Produse pentru tineri (Ovidiu) |
#      | Factura128.pdf | ExtrasCard16.pdf |        | Bon cu CUI | Cash  | Tineri   | 16.12.2024 | 646.00 | Produse pentru tineri (Ovidiu) |
#      | Factura224.pdf |         |        | Factura | Cash  | Tineri   | 13.10.2025 | 106.89 | Produse pentru tineri (Ovidiu) |
#      | Factura2000.jpeg | Factura | Cont  | Sustinere familii | 10.04.2025 | 2000.00 | Factura de curent Husar Alexandru |
#      | SCRISOARE DONATIE  09.12.2024.pdf | Donatie | Cont  | Diverse  | 08.11.2024 | 109.48 | Hartie A4   |
#      | BibileProject2024.pdf | Donatie | Cash  | BibleProject | 28.12.2024 | 109.48 | Donatie     |
#      | Factura163.pdf |            |        | Factura | Cont  | Pata RatOut | 04.08.2025 | 2178.64 | Materiale pentru bierica Cantoanelor |
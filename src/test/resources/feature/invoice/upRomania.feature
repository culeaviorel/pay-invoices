@screen
Feature: As a Customer I pay all my invoices


#  Scenario: Populeaza my virtual with date from CSV files
#    And I open url "https://myvirtual.altervista.org/"
#    And I login on MyVirtual
#    And I read "C:\Users\vculea\Desktop\BT\2025\IanuarieCameliaCulea.csv" csv file and insert date

  Scenario: Collect date from UpRomania and insert in MyVirtual
    And I open url "https://www.uponline.ro/"
    And I login in Up
    And in Up I collect data and save in storage
    And I open url "https://myvirtual.altervista.org/"
    And I login on MyVirtual
    And in MyVirtual I add transactions from storage

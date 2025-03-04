@screen
Feature: As a Customer I pay all my invoices

  Scenario: Collect date from UpRomania and insert in MyVirtual
    And I open url "https://www.uponline.ro/"
    And I login in Up
    And in Up I collect data and save in storage
    And I open url "https://myvirtual.altervista.org/"
    And I login on MyVirtual
    And in MyVirtual I add transactions from storage

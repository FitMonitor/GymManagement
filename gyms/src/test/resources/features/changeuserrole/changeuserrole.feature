@changeuserrole
Feature:Change user role
    Scenario:User that has admin role, logs in, goes to the user management page and changes a role of a specific account to user
    Given the user is in the user management page
    When the user sees the account ""       
    And selects the role user for that account
    Then the role button turns green


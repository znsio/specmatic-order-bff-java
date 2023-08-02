Feature: Tests

  Scenario: Search for available products
    # Arrange - setup expectations with Specmatic Http Stub
    * def expectationJson = karate.read('classpath:expectation.json')
    Given url 'http://localhost:9000/_specmatic/expectations'
    And request expectationJson
    When method post
    Then status 200

    # Act - make the actual call to the service
    Given url 'http://localhost:8080/findAvailableProducts?type=gadget'
    When method get

    # Assert - status and response body
    Then status 200
    And match response == expectationJson["http-response"].body

  Scenario Outline: Search for available products - Error condition
    # Arrange - simulate error by setting expectation with empty response body
    Given url 'http://localhost:9000/_specmatic/expectations'
    And request
    """
      {
        "http-request": {
          "method": "GET",
          "path": "/products",
          "query": {
            "type": "book"
          }
        },
        "http-response": {
          "status": 500,
          "body": ""
        }
      }
    """
    When method post
    Then status 200

    # Act - make the actual call to the service
    Given url 'http://localhost:8080/findAvailableProducts?type=' + <productType>
    When method get

    # Assert
    Then status 404

    Examples:
      | productType |
      | "book"      |

  Scenario Outline: Create order
    # Arrange - setup expectations with Specmatic Http Stub
    Given url 'http://localhost:9000/_specmatic/expectations'
    And request
    """
      {
        "http-request": {
          "method": "POST",
          "path": "/orders",
          "headers": {
            "Authenticate": "(string)"
          },
          "body": {
            "productid": 10,
            "count": 1,
            "status": "pending"
          }
        },

        "http-response": {
          "status": 200,
          "body": {
            "id": 10
          }
        }
      }
    """
    When method post
    Then status 200

    # Act
    Given url 'http://localhost:8080/orders'
    And request {"productid": <productId>, "count": <count>}
    When method post

    # Assert
    Then status 200
    And assert response["status"] == <status>
    And assert response["id"] == <productId>

    Examples:
      | productId | count | status    |
      | 10        | 1     | "success" |
Feature: Tests

  Scenario: Search for available products
    # Arrange - setup expectations with Specmatic Http Stub
    * def expectationJson = karate.read('classpath:stub_products_200.json')
    Given url 'http://localhost:8090/_specmatic/expectations'
    And request expectationJson
    When method post
    Then status 200

    # Act - make the actual call to the service
    Given url 'http://localhost:8080/findAvailableProducts?type=gadget'
    And header pageSize = 10
    When method get

    # Assert - status and response body
    Then status 200
    And match response == expectationJson["http-response"].body

  Scenario Outline: Search for available products - Error condition
    # Arrange - simulate error by setting expectation with empty response body
    * def expectationJsonForErrorCondition = karate.read('classpath:stub timeout.json')
    Given url 'http://localhost:8090/_specmatic/expectations'
    And request expectationJsonForErrorCondition
    When method post
    Then status 200

    # Act - make the actual call to the service
    Given url 'http://localhost:8080/findAvailableProducts?type=' + <productType>
    And header pageSize = 10
    When method get

    # Assert
    Then status 503

    Examples:
      | productType |
      | "other"      |

  Scenario Outline: Create order
    # Arrange - setup expectations with Specmatic Http Stub
    Given url 'http://localhost:8090/_specmatic/expectations'
    And request
    """
      {
        "http-request": {
          "method": "POST",
          "path": "/orders",
          "headers": {
            "Accept": "text/plain, application/json, application/*+json, */*",
            "Content-Type": "application/json",
            "Authenticate": "API-TOKEN-SPEC",
            "User-Agent": "Java/17.0.7",
            "Host": "localhost:8090",
            "Connection": "keep-alive",
            "Transfer-Encoding": "chunked"
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
          },
          "status-text": "OK",
          "headers": {
            "X-Content-Type-Options": "nosniff",
            "X-XSS-Protection": "1; mode=block",
            "Cache-Control": "no-cache, no-store, max-age=0, must-revalidate",
            "Pragma": "no-cache",
            "Expires": "0",
            "X-Frame-Options": "DENY",
            "Content-Type": "application/json",
            "Transfer-Encoding": "chunked",
            "Date": "Thu, 30 Nov 2023 00:26:58 GMT",
            "Keep-Alive": "timeout=60",
            "Connection": "keep-alive"
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
    Then status 201
    And assert response["id"] == <productId>

    Examples:
      | productId | count |
      | 10        | 1     |
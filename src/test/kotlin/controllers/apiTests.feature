Feature: Tests

  Scenario Outline: Search for available products
    # ARRANGE
    Given url 'http://localhost:9000/_specmatic/expectations'
    And request
    """
      {
        "http-request": {
          "method": "GET",
          "path": "/products",
          "query": {
            "type": "gadget"
          }
        },
        "http-response": {
          "status": 200,
          "body": [
            {
              "name": "XYZ Laptop",
              "type": "gadget",
              "id": 10,
              "inventory": 10
            },
            {
              "name": "XYZ Phone",
              "type": "gadget",
              "id": 20,
              "inventory": 0
            }
          ]
        }
      }
    """
    When method post
    Then status 200

    # ACT
    Given url 'http://localhost:8080/findAvailableProducts?type=' + <productType>
    When method get
    Then status 200

    # ASSERT
    And assert response[0]["id"] == <productId>
    And assert response[0]["name"] == <productName>

    Examples:
      | productType | productId | productName  |
      | "gadget"    | 10        | "XYZ Laptop" |

  Scenario Outline: Search for available products - Error condition
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

    Given url 'http://localhost:8080/findAvailableProducts?type=' + <productType>
    When method get
    Then status 404

    Examples:
      | productType |
      | "book"      |

  Scenario Outline: Create order
    Given url 'http://localhost:9000/_specmatic/expectations'
    And request
    """
      {
        "mock-http-request": {
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

        "mock-http-response": {
          "status": 200,
          "body": {
            "id": 10
          }
        }
      }
    """
    When method post
    Then status 200

    Given url 'http://localhost:8080/orders'
    And request {"productid": <productId>, "count": <count>}
    When method post
    Then status 200
    And assert response["status"] == <status>
    And assert response["id"] == <productId>

    Examples:
      | productId | count | status    |
      | 10        | 1     | "success" |
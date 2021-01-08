participant User
participant Orders Service
participant Message Broker
participant Billing Service
participant Notifications Service
participant Users Service

User->>Orders Service: POST /createOrder
activate Orders Service
Orders Service-->>Message Broker: publish ORDER_REQUESTED 
activate Message Broker
Message Broker->>Billing Service: consume
deactivate Message Broker

Orders Service->>User: Response 202 ACCEPTED "Check you email inbox"
deactivate Orders Service

Billing Service-->>Message Broker: publish ORDER_PROCESSED
activate Message Broker
Message Broker->>Notifications Service: consume
deactivate Message Broker

activate Notifications Service
Notifications Service->>Users Service: GET /getUserInfo {userId}
activate Users Service
Users Service->>Notifications Service: {name, email}
deactivate Users Service

Notifications Service->>Notifications Service: send email
deactivate Notifications Service
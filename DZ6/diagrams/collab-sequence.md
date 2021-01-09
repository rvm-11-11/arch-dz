participant User
participant Gateway
participant Message Broker
participant Orders Service
participant Billing Service
participant Notifications Service
participant Users Service

User->>Gateway: POST /createOrder
activate Gateway
Gateway->>Message Broker: publish ORDER_REQUESTED 
deactivate Gateway
activate Message Broker
Message Broker->>Orders Service: consume
deactivate Message Broker

Orders Service->>Message Broker: publish ORDER_CREATED
activate Message Broker
Message Broker->>Gateway: consume
Gateway->>User: response
Message Broker->>Billing Service: consume
deactivate Message Broker

Billing Service->>Message Broker: publish ORDER_PROCESSED
activate Message Broker
Message Broker->>Notifications Service: consume
deactivate Message Broker

Notifications Service->>Message Broker: publish USER_INFO_REQUESTED
activate Message Broker
Message Broker->>Users Service: consume
deactivate Message Broker

Users Service->>Message Broker: publish USER_INFO_PUBLISHED
activate Message Broker
Message Broker->>Notifications Service: consume
deactivate Message Broker
activate Notifications Service
Notifications Service->>Notifications Service: send email
deactivate Notifications Service

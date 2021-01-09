participant User
participant Orders Service
participant Billing Service
participant Notification Service
participant Users Service

User->>Orders Service: POST /createOrder
activate Orders Service
Orders Service->>Billing Service: POST /makePayment {userId, sum, orderId}
activate Billing Service
Billing Service->>Notification Service: POST /notifyUser {userId, paymentStatus}
activate Notification Service
Notification Service->>Users Service: GET /getUserInfo {userId}
activate Users Service
Users Service->>Notification Service: {name, email}
deactivate Users Service
Notification Service->>Billing Service: 202 ACCEPTED
Billing Service->>Orders Service: 202 ACCEPTED
deactivate Billing Service
Orders Service->>User: 200 OK "check you email inbox"
deactivate Orders Service
Notification Service->>Notification Service: send email
deactivate Notification Service


http://localhost:8081/api/rider-events



{

 "riderId": 1,

 "pickupLocation": "123 Main St",

 "dropoffLocation": "456 Elm St",

 "tripStatus": "REQUESTED"

}









http://localhost:8081/api/trip-events


{

 "tripId": "5ac9dbad-a6a4-4685-8a98-a21403a6c58c",

 "driverId": 99,

 "tripStatus": "DRIVER_ASSIGNED"

}





http://localhost:8081/api/trip-events


{

  "tripId": "5ac9dbad-a6a4-4685-8a98-a21403a6c58c",

  "tripStatus": "STARTED"

}



http://localhost:8081/api/trip-events




{

 "tripId": "5ac9dbad-a6a4-4685-8a98-a21403a6c58c",

 "tripStatus": "COMPLETED"

}

http://localhost:8081/api/trip-events


{

 "tripId": "a3931c05-326d-4c01-b941-6ee2d54090b5",

 "tripStatus": "CANCELLED",

 "cancellationReason": "Rider changed mind",

 "cancelledBy": "RIDER"

}





http://localhost:8081/api/feedback

{

 "tripId": "3c031323-c286-41f0-8f70-e6485a4d7d1b",

 "confirmed": false,

 "reason": "Wrong address"

}

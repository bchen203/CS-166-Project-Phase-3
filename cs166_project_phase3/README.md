Indexes
The following indexes were built on the tables:

| Table        | Indexes              |
|--------------|----------------------|
| Users        | login                |
| Catalog      | gameID, genre, price |
| RentalOrder  | rentalOrderID        |
| TrackingInfo | rentalOrderID        |


The login index allows for users to quickly log in to the system and perform updates on user profiles and user orders.
The gameID index is used for retrieving the price of a game when a rental order is being placed.
The genre and price indexes are used when viewing the catalog.
Lastly, the rentalOrderID and trackingID are used for viewing and updating rental orders and the corresponding tracking information.

DROP INDEX IF EXISTS login;
DROP INDEX IF EXISTS gameID;
DROP INDEX IF EXISTS genre;
DROP INDEX IF EXISTS price;
DROP INDEX IF EXISTS rentalOrderID_Rental;
DROP INDEX IF EXISTS trackingID;
DROP INDEX IF EXISTS rentalOrderID_Tracking;

CREATE INDEX login ON Users (login);
CREATE INDEX gameID ON Catalog (gameID);
CREATE INDEX genre ON Catalog (genre);
CREATE INDEX price ON Catalog (price);
CREATE INDEX rentalOrderID_Rental ON RentalOrder (rentalOrderID);
CREATE INDEX trackingID ON TrackingInfo (trackingID);
CREATE INDEX rentalOrderID_Tracking ON TrackingInfo (rentalOrderID);
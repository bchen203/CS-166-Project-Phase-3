/* Replace the location to where you saved the data files*/
COPY Users
FROM '/home/csgrads/<net_id>/cs166_project_phase3/data/users.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Catalog
FROM '/home/csgrads/<net_id>/cs166_project_phase3/data/catalog.csv'
WITH DELIMITER ',' CSV HEADER;

COPY RentalOrder
FROM '/home/csgrads/<net_id>/cs166_project_phase3/data/rentalorder.csv'
WITH DELIMITER ',' CSV HEADER;

COPY TrackingInfo
FROM '/home/csgrads/<net_id>/cs166_project_phase3/data/trackinginfo.csv'
WITH DELIMITER ',' CSV HEADER;

COPY GamesInOrder
FROM '/home/csgrads/<net_id>/cs166_project_phase3/data/gamesinorder.csv'
WITH DELIMITER ',' CSV HEADER;

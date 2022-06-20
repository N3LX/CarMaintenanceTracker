/* Initialize database */

CREATE TABLE users (
    id SERIAL NOT NULL PRIMARY KEY,
    user_name VARCHAR(32) NOT NULL
);

CREATE TABLE vehicles (
    id SERIAL NOT NULL PRIMARY KEY,
    custom_name VARCHAR(32),
    owner_id INT NOT NULL REFERENCES users (id) ,
    make VARCHAR(32) NOT NULL,
    model VARCHAR(32) NOT NULL
);

CREATE TABLE records (
    id SERIAL NOT NULL PRIMARY KEY,
    vehicle_id INT NOT NULL REFERENCES vehicles (id),
    creation_date DATE NOT NULL,
    mileage INT NOT NULL,
    short_description VARCHAR(32) NOT NULL,
    long_description VARCHAR(4096) NOT NULL
);

/* Sample data */

/*
INSERT INTO users (user_name) VALUES
('John Doe');

INSERT INTO vehicles (custom_name, owner_id, make, model) VALUES
('Golf',1,'Volkswagen','Golf V');

INSERT INTO records (vehicle_id,creation_date,mileage,short_description,long_description) VALUES
(1,CURRENT_DATE, 378000,'Clutch replacement','Clutch and gearbox oil were replaced for total of 3500 PLN');
*/
#Build database container
cd database
docker build -t car_maintenance_tracker_database_test .
cd ..

#Start it
cd database-test
docker compose up -d
cd ..

#Wait a reasonable amount of time for docker container to start up
echo "============================================"
echo "Waiting for database container to start up."
echo "============================================"
sleep 1.5

#Run tests
cd CMT\ API/
mvn clean package
cd ..

#Stop container
cd database-test
docker compose down
cd ..

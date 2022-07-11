#Build database container
cd database
docker build -t car_maintenance_tracker_database_test .
cd ..

#Start it
cd database-test
docker-compose up -d
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
docker-compose down
cd ..

#Remove built images
docker images | grep "car_maintenance_tracker_" | awk '{print $1 ":" $2}' | xargs docker rmi

#Remove unused volumes
docker volume rm -f $(docker volume ls -f "dangling=true")
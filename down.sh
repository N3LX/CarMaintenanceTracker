#This script resets the whole project, including the DB and its internal data

#Shut down and remove containers
cd docker
docker-compose down
cd ..

#Remove built images
docker images | grep "car_maintenance_tracker_" | awk '{print $1 ":" $2}' | xargs docker rmi

#Remove unused volumes
docker volume rm -f $(docker volume ls -f "dangling=true")
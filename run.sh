#Build database
cd database
docker build -t car_maintenance_tracker_database .
cd ..

#Build cmt-api
cd CMT\ API/
mvn clean package -DskipTests
docker build -t car_maintenance_tracker_api .
cd ..

#Build frontend
cd CMT\ Frontend/
docker build -t car_maintenance_tracker_frontend .
cd ..

#Run the application
cd docker
docker compose up -d
cd ..

echo "==================================="
echo "To stop the application run stop.sh"
echo "==================================="
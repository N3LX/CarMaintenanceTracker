#Build database
cd database
docker build -t car_maintenance_tracker_database .   
cd ..

#Run the application
cd docker
docker compose up -d
cd ..

echo "==================================="
echo "To stop the application run stop.sh"
echo "==================================="
package com.n3lx.service;

import com.n3lx.dao.GenericDao;
import com.n3lx.exception.InputValidationException;
import com.n3lx.model.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService implements GenericService<Vehicle> {

    @Autowired
    GenericDao<Vehicle> vehicleDao;

    @Override
    public Vehicle get(int id) throws Exception {
        //Input validation
        if (id <= 0) {
            throw new InputValidationException("\"id\" parameter has to be greater than 0.");
        }

        //Process input
        return vehicleDao.get(id);
    }

    @Override
    public List<Vehicle> getAll() throws Exception {
        //Process input
        return vehicleDao.getAll();
    }

    @Override
    public void save(Vehicle vehicle) throws Exception {
        //Input validation
        if (vehicle == null) {
            throw new InputValidationException("The vehicle is null or it was not provided properly.");
        }
        if (vehicle.getCustomName() == null ||
                vehicle.getMake() == null ||
                vehicle.getModel() == null) {
            throw new InputValidationException("One of parameters is null.");
        }
        if (vehicle.getCustomName().length() > 32) {
            throw new InputValidationException("\"custom_name\" parameter cannot be longer than 32 characters.");
        }
        if (vehicle.getMake().length() > 32) {
            throw new InputValidationException("\"short_description\" parameter cannot exceed 32 characters.");
        }
        if (vehicle.getModel().length() > 32) {
            throw new InputValidationException("\"long_description\" parameter cannot exceed 4096 characters.");
        }

        //Process input
        vehicleDao.save(vehicle);
    }

    @Override
    public void update(Vehicle vehicle) throws Exception {
        //Input validation
        if (vehicle == null) {
            throw new InputValidationException("The vehicle is null or it was not provided properly.");
        }
        if (vehicle.getCustomName() == null ||
                vehicle.getMake() == null ||
                vehicle.getModel() == null) {
            throw new InputValidationException("One of parameters is null.");
        }
        if (vehicle.getCustomName().length() > 32) {
            throw new InputValidationException("\"custom_name\" parameter cannot be longer than 32 characters.");
        }
        if (vehicle.getMake().length() > 32) {
            throw new InputValidationException("\"short_description\" parameter cannot exceed 32 characters.");
        }
        if (vehicle.getModel().length() > 32) {
            throw new InputValidationException("\"long_description\" parameter cannot exceed 4096 characters.");
        }

        //Process input
        vehicleDao.update(vehicle);
    }

    @Override
    public void delete(Vehicle vehicle) throws Exception {
        //Input validation
        if (vehicle == null) {
            throw new InputValidationException("The vehicle is null or it was not provided properly.");
        }
        if (vehicle.getCustomName() == null ||
                vehicle.getMake() == null ||
                vehicle.getModel() == null) {
            throw new InputValidationException("One of parameters is null.");
        }

        //Process input
        vehicleDao.delete(vehicle);
    }

}

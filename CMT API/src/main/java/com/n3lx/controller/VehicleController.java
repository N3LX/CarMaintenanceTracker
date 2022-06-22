package com.n3lx.controller;

import com.n3lx.model.Vehicle;
import com.n3lx.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VehicleController {

    @Autowired
    GenericService<Vehicle> vehicleService;

    @RequestMapping(value = "/vehicles/{id}", method = RequestMethod.GET)
    public Vehicle getVehicle(@PathVariable Integer id) throws Exception {
        return vehicleService.get(id);
    }

    @RequestMapping(value = "/vehicles", method = RequestMethod.GET)
    public List<Vehicle> getAllVehicles() throws Exception {
        return vehicleService.getAll();
    }

    @RequestMapping(value = "/vehicles", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void saveVehicle(@RequestBody Vehicle vehicle) throws Exception {
        vehicleService.save(vehicle);
    }

    @RequestMapping(value = "/vehicles", method = RequestMethod.PUT)
    public void updateVehicle(@RequestBody Vehicle vehicle) throws Exception {
        vehicleService.update(vehicle);
    }

    @RequestMapping(value = "/vehicles", method = RequestMethod.DELETE)
    public void deleteVehicle(@RequestBody Vehicle vehicle) throws Exception {
        vehicleService.delete(vehicle);
    }

}

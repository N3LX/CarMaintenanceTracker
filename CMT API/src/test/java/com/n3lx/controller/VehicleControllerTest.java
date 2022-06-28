package com.n3lx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n3lx.model.Vehicle;
import com.n3lx.service.VehicleService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:servlet-context-test.xml")
public class VehicleControllerTest {

    @Mock
    VehicleService vehicleService;

    @Autowired
    VehicleController vehicleController;

    //List containing test data that can be reliably used in tests
    static List<Vehicle> testVehicleList;

    //This mapper helps with comparing the controller's output with the Java objects by converting them to JSON
    final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeClass
    public static void createTestData() {
        testVehicleList = new ArrayList<>();

        Vehicle v1 = new Vehicle();
        v1.setId(1);
        v1.setCustomName("Car 1");
        v1.setOwnerId(1);
        v1.setMake("Ford");
        v1.setModel("Escort");
        testVehicleList.add(v1);

        Vehicle v2 = new Vehicle();
        v2.setId(2);
        v2.setCustomName("Car 2");
        v2.setOwnerId(1);
        v2.setMake("Ford");
        v2.setModel("Mondeo");
        testVehicleList.add(v2);
    }

    @Before
    public void setUpMockitoAnnotations() {
        MockitoAnnotations.openMocks(this);
        vehicleController.vehicleService = vehicleService;
    }

    @Test
    public void testGetVehicle() throws Exception {
        when(vehicleService.get(2)).thenReturn(testVehicleList.get(1));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/vehicles/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals(OBJECT_MAPPER.writeValueAsString(testVehicleList.get(1)), response);
    }

    @Test
    public void testGetAllVehicles() throws Exception {
        when(vehicleService.getAll()).thenReturn(testVehicleList);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/vehicles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals(OBJECT_MAPPER.writeValueAsString(testVehicleList), response);
    }

    @Test
    public void testSaveVehicle() throws Exception {
        //Prepare new vehicle
        Vehicle newVehicle = new Vehicle();
        newVehicle.setId(3);
        newVehicle.setCustomName("Car 3");
        newVehicle.setOwnerId(1);
        newVehicle.setMake("Ford");
        newVehicle.setModel("Escort");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(newVehicle)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("", response);
        verify(vehicleService, times(1)).save(isA(Vehicle.class));
    }

    @Test
    public void testUpdateVehicle() throws Exception {
        //Update existing vehicle
        Vehicle updatedVehicle = testVehicleList.get(0);
        updatedVehicle.setCustomName("Car 3");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(updatedVehicle)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("", response);
        verify(vehicleService, times(1)).update(isA(Vehicle.class));
    }

    @Test
    public void testDeleteVehicle() throws Exception {
        //Prepare vehicle for deletion
        Vehicle vehicleForDeletion = testVehicleList.get(0);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(vehicleForDeletion)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("", response);
        verify(vehicleService, times(1)).delete(isA(Vehicle.class));
    }

}

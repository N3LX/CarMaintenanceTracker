package com.n3lx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.n3lx.model.Record;
import com.n3lx.service.RecordService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:servlet-context-test.xml")
public class RecordControllerTest {

    @Mock
    RecordService recordService;

    @Autowired
    RecordController recordController;

    //List containing test data that can be reliably used in tests
    static List<Record> testRecordList;

    //This mapper helps with comparing the controller's output with the Java objects by converting them to JSON
    static ObjectMapper objectMapper;

    @BeforeClass
    public static void configureJackson() {
        objectMapper = new ObjectMapper();
        
        //Add support for LocalDateTime class
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeClass
    public static void createTestData() {
        testRecordList = new ArrayList<>();

        Record r1 = new Record();
        r1.setId(1);
        r1.setVehicleId(1);
        r1.setCreationDate(LocalDate.now());
        r1.setMileage(360000);
        r1.setShortDescription("Short description");
        r1.setLongDescription("Long description");
        testRecordList.add(r1);

        Record r2 = new Record();
        r2.setId(2);
        r2.setVehicleId(1);
        r2.setCreationDate(LocalDate.now());
        r2.setMileage(370000);
        r2.setShortDescription("Short description 2");
        r2.setLongDescription("Long description 2");
        testRecordList.add(r2);
    }

    @Before
    public void setUpMockitoAnnotations() {
        MockitoAnnotations.openMocks(this);
        recordController.recordService = recordService;
    }

    @Test
    public void testGetRecord() throws Exception {
        when(recordService.get(2)).thenReturn(testRecordList.get(1));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(recordController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/records/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(testRecordList.get(1)), response);
    }

    @Test
    public void testGetAllRecords() throws Exception {
        when(recordService.getAll()).thenReturn(testRecordList);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(recordController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/records"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(testRecordList), response);
    }

    @Test
    public void testSaveRecord() throws Exception {
        //Prepare new record
        Record newRecord = new Record();
        newRecord.setId(3);
        newRecord.setVehicleId(1);
        newRecord.setCreationDate(LocalDate.now());
        newRecord.setMileage(370000);
        newRecord.setShortDescription("Short description 3");
        newRecord.setLongDescription("Long description 3");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(recordController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRecord)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("", response);
        verify(recordService, times(1)).save(isA(Record.class));
    }

    @Test
    public void testUpdateRecord() throws Exception {
        //Update existing record
        Record updatedRecord = testRecordList.get(0);
        updatedRecord.setMileage(400000);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(recordController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRecord)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("", response);
        verify(recordService, times(1)).update(isA(Record.class));
    }

    @Test
    public void testDeleteRecord() throws Exception {
        //Prepare record for deletion
        Record recordForDeletion = testRecordList.get(0);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(recordController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordForDeletion)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("", response);
        verify(recordService, times(1)).delete(isA(Record.class));
    }

}

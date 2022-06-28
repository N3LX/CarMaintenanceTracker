package com.n3lx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n3lx.model.User;
import com.n3lx.service.UserService;
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
public class UserControllerTest {

    @Mock
    UserService userService;

    @Autowired
    UserController userController;

    //List containing test data that can be reliably used in tests
    static List<User> testUserList;

    //This mapper helps with comparing the controller's output with the Java objects by converting them to JSON
    final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeClass
    public static void createTestData() {
        testUserList = new ArrayList<>();

        User u1 = new User();
        u1.setId(1);
        u1.setUserName("Jan Kowalski");
        testUserList.add(u1);

        User u2 = new User();
        u2.setId(1);
        u2.setUserName("Anna Nowak");
        testUserList.add(u2);
    }

    @Before
    public void setUpMockitoAnnotations() {
        MockitoAnnotations.openMocks(this);
        userController.userService = userService;
    }

    @Test
    public void testGetUser() throws Exception {
        when(userService.get(2)).thenReturn(testUserList.get(1));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals(OBJECT_MAPPER.writeValueAsString(testUserList.get(1)), response);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userService.getAll()).thenReturn(testUserList);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals(OBJECT_MAPPER.writeValueAsString(testUserList), response);
    }

    @Test
    public void testSaveUser() throws Exception {
        //Prepare new user
        User newUser = new User();
        newUser.setId(3);
        newUser.setUserName("John Doe");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(newUser)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("", response);
        verify(userService, times(1)).save(isA(User.class));
    }

    @Test
    public void testUpdateUser() throws Exception {
        //Update existing user
        User updatedUser = testUserList.get(0);
        updatedUser.setUserName("John Doe");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("", response);
        verify(userService, times(1)).update(isA(User.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        //Prepare user for deletion
        User userForDeletion = testUserList.get(0);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(userForDeletion)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertEquals("", response);
        verify(userService, times(1)).delete(isA(User.class));
    }

}

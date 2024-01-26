package com.spring.boot.controller;

import com.spring.boot.controllers.ContactController;
import com.spring.boot.controllers.GlobalErrorController;
import com.spring.boot.services.ContactManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(value = {ContactController.class, GlobalErrorController.class})
public class ContactControlellerUnitTest {

    @MockBean
    private ContactManagerService contactManagerService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new ContactController(contactManagerService), new GlobalErrorController())
            .alwaysDo(MockMvcResultHandlers.print())
            .build();
    }
}

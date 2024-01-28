package com.spring.boot.controller;

import com.spring.boot.TestResources;
import com.spring.boot.controllers.ContactController;
import com.spring.boot.controllers.GlobalErrorController;
import com.spring.boot.services.ContactManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    @DisplayName("GET /api/contacts OK 200")
    void should_response_all_the_contacts_with_OK_200() throws Exception {
        when(contactManagerService.findAll()).thenReturn(TestResources.getAFewContacts(10));

        mockMvc.perform(
            get("/api/contacts")
            .header("Authorization", "Bearer "+jwtToken())
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[*].phoneNumbers[*]").value(containsInAnyOrder("+359(26)5948-0427",
            "+81(56)4205-8516",
            "+359(10)4094-9549",
            "+52(54)6536-5876",
            "+65(77)4248-0921")));

    private String jwtTokenForJoe() {
        return """
            eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJuNmpaamhHcmtpd2xnT0hmVDB1dEJ5cV
            Q4aXBSSG5Edzh3cVpDV1RDa2VZIn0.eyJleHAiOjIwMjE3NzQ1MTYsImlhdCI6MTcwNjQxNDUxNiwianRp
            IjoiMDY0Yzg0NjMtMTk5Mi00M2YzLTgzZTAtZDZlOWU2NDU1YjgzIiwiaXNzIjoiaHR0cDovL2xvY2FsaG
            9zdDo4MDAwL3JlYWxtcy9tYWluIiwic3ViIjoiOWRjNjc4YmYtY2UwZi00NGI3LWEyNGQtNjUyMThhMGZl
            ZGM4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY29udGFjdC1tYW5hZ2VyIiwic2Vzc2lvbl9zdGF0ZSI6Ij
            VhNTA4NjQzLTZiMWYtNDBkOS1hNGNiLTUzY2VjNmJkZWVmZCIsInNjb3BlIjoiY29udGFjdC1tYW5hZ2Vy
            Iiwic2lkIjoiNWE1MDg2NDMtNmIxZi00MGQ5LWE0Y2ItNTNjZWM2YmRlZWZkIiwidXNlcm5hbWUiOiJqb2
            UifQ.yMED1qj_q8IzvHBGo4xWkOJ443kISwQp4w2cr-VHjCx2DqzisyyHxavvq5GhZMx5PHOINi_PIZgP0
            weFV84g9xpm1jjkiuhyrVfwRfaq3z6svwEZcGDWU-d-wy_58zC_ZrpRrm4CRAeNg-SzKLNUwJ1imK24HCG
            R2yOCdb-rn79az_xkhp8J0-D8KmKiRqeOLNFDyGmMTmcYAP2HOowYQsvIXbGaaNMgG4gEZLBXkspzkLqvm
            ZrH3nWzioBiqDqJnZQ-5DDIcJ-UbcY1FtRIZv1VYbX9Kqm1z0S7k5Q8dj3IzMRaYJ4l0_hIMgiye-vlNFg
            izWTT-9WVM2GTjZ_-gw
        """.trim().replaceAll("\\s", "");
    }
}

package com.spring.boot.controller;

import com.spring.boot.TestResources;
import com.spring.boot.controllers.ContactController;
import com.spring.boot.controllers.GlobalErrorController;
import com.spring.boot.services.ContactManagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {ContactController.class, GlobalErrorController.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ContactControlellerUnitTest {

    @MockBean
    private ContactManagerService contactManagerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/contacts -> OK 200")
    void should_response_all_the_contacts_for_Joe_with_OK_200() throws Exception {
        when(contactManagerService.findAllByUsername(eq("joe")))
            .thenReturn(TestResources.getContactsForJoe());

        mockMvc.perform(
            get("/api/contacts")
            .header("Authorization", "Bearer "+jwtTokenForJoe())
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("Greg from accounting", "Coworker Fred", "Sister Monica")))
        .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(5)))
        .andExpect(jsonPath("$[*].addresses.*").value(hasSize(4)))
        .andExpect(jsonPath("$[*].emails.*").value(hasSize(3)));

        verify(contactManagerService, times(1)).findAllByUsername(eq("joe"));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("GET /api/contacts -> OK 200")
    void should_return_all_the_contacts_for_Robert_with_OK_200() throws Exception {
        when(contactManagerService.findAllByUsername(eq("robert")))
            .thenReturn(TestResources.getContactsForRobert());

        mockMvc.perform(
            get("/api/contacts")
            .header("Authorization", "Bearer "+jwtTokenForRobert())
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[*].name").value(
            containsInAnyOrder("Best friend Julia", "Mom", "Pizza and burgers", "Uncle Jeff"))
        )
        .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(7)))
        .andExpect(jsonPath("$[*].addresses.*").value(hasSize(7)))
        .andExpect(jsonPath("$[*].emails.*").value(hasSize(7)));

        verify(contactManagerService, times(1)).findAllByUsername(eq("robert"));
        verifyNoMoreInteractions(contactManagerService);
    }

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

    private String jwtTokenForRobert() {
        return """
            eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJuNmpaamhHcmtpd2xnT0hmVDB1dEJ5cVQ4aXBS
            SG5Edzh3cVpDV1RDa2VZIn0.eyJleHAiOjIwMjE5NDMyOTAsImlhdCI6MTcwNjU4MzI5MCwianRpIjoiMjU3ZDVi
            M2YtNTRiNS00YjIyLWI2ODYtOWExN2VlNDg4ZDA3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDAwL3JlYWxt
            cy9tYWluIiwic3ViIjoiNmE0YmI1M2QtZGRkZi00MGQzLWFiYzQtNDljZDg0NjE4NjY3IiwidHlwIjoiQmVhcmVy
            IiwiYXpwIjoiY29udGFjdC1tYW5hZ2VyIiwic2Vzc2lvbl9zdGF0ZSI6IjU4NGE3NDQzLTUxMjctNGYzZC04ZDgy
            LTFkNDljNmVjMDA3MyIsInNjb3BlIjoiY29udGFjdC1tYW5hZ2VyIiwic2lkIjoiNTg0YTc0NDMtNTEyNy00ZjNk
            LThkODItMWQ0OWM2ZWMwMDczIiwidXNlcm5hbWUiOiJyb2JlcnQifQ.unY-8y94sSTUl5bTSVqu1_sjRThI6wR7t
            lZypH3WTFhlZ5gSjgC9DfyMEt8ZdT19ue_RPpZJQLSa3zt5u6KmV-g7yQJpeUhn_6blaHp8JLj7sLjDA4N5PtZwR
            rFtJnV7oliRb4cVW7j6DaH19SEPrQ6Xmyq_6e8OevzoNkCijiFPTR0nrSw9rWm81UiN7YeqdRGUOnmWgkSFQhIUP
            9NfEUgAvetZOJvMX4nrNZNKFgEvjZoleRLIORIf2-nBmXy2XJ8f68-a_Anb4k__SQFsroLQdrU7LnUR_qxTArPEf
            DOoykEHQK2w003DePt_JT3uhVRMZIFLAMuyZfTrQ1_JvQ
        """.trim().replaceAll("\\s", "");
    }
}

package de.iu.aufgabenmanager.controller;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.iu.aufgabenmanager.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * US7 (Login-Flow) und Autorisierung (401/403) ueber die echte Filter-/Security-Kette.
 * Nutzt die vom DataSeeder angelegten Testbenutzer auf einer In-Memory-H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Login mit gueltigen Daten liefert 200 und ein Token")
    void loginSuccess() throws Exception {
        mockMvc.perform(login("admin", "admin123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("Login mit falschem Passwort liefert 401")
    void loginWrongPassword() throws Exception {
        mockMvc.perform(login("admin", "falsch"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Geschuetzter Endpunkt ohne Token liefert 401")
    void protectedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Admin-Endpunkt mit MITARBEITER-Token liefert 403")
    void adminEndpointForbiddenForMitarbeiter() throws Exception {
        String token = obtainToken("user1", "user123");
        mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin-Endpunkt mit ADMIN-Token liefert 200")
    void adminEndpointOkForAdmin() throws Exception {
        String token = obtainToken("admin", "admin123");
        mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder login(
            String username, String password) throws Exception {
        return post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(username, password)));
    }

    private String obtainToken(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(login(username, password))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}

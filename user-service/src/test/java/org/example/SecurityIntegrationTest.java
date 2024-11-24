package org.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.example.dto.request.SignInRequest;
import org.example.dto.request.SignUpRequest;
import org.example.dto.response.AuthenticationResponse;
import org.example.security.AuthenticationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private static WireMockServer wireMockServer;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void signUpTest_shouldSignUp() throws Exception {
        SignUpRequest request = mock(SignUpRequest.class);

        Mockito.when(authenticationService.signUp(request)).thenReturn(any(AuthenticationResponse.class));

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Name\", \"surname\": \"Surname\", \"email\": \"email@example.com\", \"password\": \"password\"}"))
                .andExpect(status().isOk());

    }

    @Test
    public void signInTest_shouldSignIn() throws Exception {
        SignInRequest request = mock(SignInRequest.class);

        Mockito.when(authenticationService.signIn(request)).thenReturn(any(AuthenticationResponse.class));

        mockMvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"email@example.com\", \"password\": \"password\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void refreshTokenTest_shouldRefreshToken() {
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/api/v1/auth/refresh"))
                .willReturn(aResponse()
                        .withBody("{\"accessToken\": \"accessToken\", \"refreshToken\": \"refreshToken\"}")
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")));

        given()
                .header("Authorization", "Bearer refreshToken")
                .contentType("application/json")
                .when()
                .post("/api/v1/auth/refresh")
                .then()
                .contentType("application/json")
                .statusCode(200);
    }

    @Test
    public void resetPasswordTest_shouldResetPassword() {
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/api/v1/auth/reset-password-request"))
                .withRequestBody(equalToJson("{\"email\":\"a@b.com\"}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"my-token\"}")));

        given()
                .header("Authorization", "Bearer my-token")
                .contentType("application/json")
                .body("{\"email\":\"a@b.com\"}")
                .when()
                .post("/api/v1/auth/reset-password-request")
                .then()
                .contentType("application/json")
                .statusCode(200);
    }

    @Test
    public void confirmPasswordTest_shouldConfirmPassword() {
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/api/v1/auth/reset-password-confirm"))
                .withRequestBody(equalToJson("{\"email\":\"a@b.com\",\"code\":\"123456\",\"newPassword\":\"newPassword\"}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"my-token\"}")));

        given()
                .header("Authorization", "Bearer my-token")
                .contentType("application/json")
                .body("{\"email\":\"a@b.com\",\"code\":\"123456\",\"newPassword\":\"newPassword\"}")
                .when()
                .post("/api/v1/auth/reset-password-confirm")
                .then()
                .contentType("application/json")
                .statusCode(200);
    }

}

package org.example;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.example.dto.response.QuoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class QuotesIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setupWireMock() {
        WireMock.stubFor(get(urlPathEqualTo("/api/v1/quotes"))
                .withQueryParam("category", equalTo("money"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"quote\":\"Keep going!\", \"author\":\"Unknown\"}")
                        .withStatus(200)));
    }

    @Test
    public void testGetQuotes() {
        ResponseEntity<QuoteResponse> response = restTemplate.getForEntity(
                "/api/v1/quotes?category=money", QuoteResponse.class);

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());

    }
}

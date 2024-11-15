package org.example;

import org.example.entity.RecurringFrequency;
import org.example.entity.RecurringPayment;
import org.example.service.RecurringPaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@SpringBootTest
@AutoConfigureMockMvc
public class RecurringPaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecurringPaymentService paymentService;

    @Test
    public void getAllPaymentsTest() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        List<RecurringPayment> payments = new ArrayList<>();
        RecurringPayment payment = new RecurringPayment();
        payment.setName("transport");
        payment.setAmount(BigDecimal.valueOf(1000.0));
        payment.setStartDate(LocalDate.now());
        payment.setFrequency(RecurringFrequency.MONTHLY);
        payments.add(payment);

        Mockito.when(paymentService.getAllPayments(userId)).thenReturn(payments);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/recurring-payments/all")
                        .param("userId", userId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("transport"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").value(BigDecimal.valueOf(1000.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].startDate").value(LocalDate.now().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].frequency").value("MONTHLY"));
    }

    @Test
    public void addPaymentTest() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/recurring-payments/add")
                        .param("userId", userId.toString())
                        .param("name", "transport")
                        .param("amount", "1000.0")
                        .param("startDate", LocalDate.now().toString())
                        .param("frequency", "MONTHLY"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Payment added"));
    }

    @Test
    public void deletePaymentTest() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        RecurringPayment payment = new RecurringPayment();
        payment.setName("transport");
        payment.setAmount(BigDecimal.valueOf(1000.0));
        payment.setStartDate(LocalDate.now());
        payment.setFrequency(RecurringFrequency.MONTHLY);
        paymentService.addPayment(userId, "transport", BigDecimal.valueOf(1000.0), LocalDate.now(), RecurringFrequency.MONTHLY);
        paymentService.deletePayment(userId, "transport");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/recurring-payments/delete/transport")
                        .param("userId", userId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Payment deleted"));
    }
}

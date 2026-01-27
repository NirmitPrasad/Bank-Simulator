// package com.bfe.route.controller;

// import com.bfe.route.enums.controllers.TransactionController;
// import com.bfe.route.enums.entity.Transaction;
// import com.bfe.route.enums.exceptions.GlobalExceptionHandler;
// import com.bfe.route.enums.services.TransactionService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import java.math.BigDecimal;

// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(TransactionController.class)
// @Import(GlobalExceptionHandler.class) 
// public class TransactionControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private TransactionService transactionService;

//     @Test
//     public void deposit_validRequest_returns200() throws Exception {
//         Long accountId = 4L;
//         String json = "{\"amount\":1000.00,\"description\":\"Initial deposit\",\"modeOfTransaction\":\"CASH\",\"receiverBy\":\"self\"}";

        
//         when(transactionService.deposit(eq(accountId), eq(new BigDecimal("1000.00"))))
//             .thenReturn(new Transaction());

//         mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(json))
//             .andExpect(status().isOk())
//             .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

//         verify(transactionService).deposit(eq(accountId), eq(new BigDecimal("1000.00")));
//     }

//     @Test
//     public void deposit_missingAmount_returns400() throws Exception {
//         Long accountId = 4L;
//         String json = "{\"description\":\"Initial deposit\"}"; 

//         mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(json))
//             .andExpect(status().isBadRequest())
//             .andExpect(content().contentType("text/plain"))
//             .andExpect(content().string(org.hamcrest.Matchers.containsString("Amount is required")));
//     }
// }

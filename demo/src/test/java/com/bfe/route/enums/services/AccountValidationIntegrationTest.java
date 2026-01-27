package com.bfe.route.enums.services;

import com.bfe.route.enums.models.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountValidationIntegrationTest {

    private static final String BASE_PATH = "/api/accounts";

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE_REF = 
        new ParameterizedTypeReference<Map<String, Object>>() {};

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    JdbcTemplate jdbc;

    private String baseUrl() { return "http://localhost:" + port + BASE_PATH; }

    private String genAccNumber() {
        return "TST" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    @AfterEach
    void cleanup() {
        // Only delete test accounts, not the whole table
        jdbc.update("DELETE FROM account_details WHERE account_number LIKE 'TST%'");
    }

    @BeforeEach
    void setup() {
        // Clean up any leftover test data first
        cleanup();
        
        // Query for an existing customer ID
        Integer customerId = jdbc.queryForObject(
            "SELECT customer_id FROM customer_details LIMIT 1", 
            Integer.class
        );
        if (customerId == null) {
            throw new IllegalStateException("No customers found in database. Tests require existing customer data.");
        }
    }

    // Helper to create a valid test account
    private Integer createTestAccount(String accNumber) {
        // Get existing customer ID from database
        Integer customerId = jdbc.queryForObject(
            "SELECT customer_id FROM customer_details LIMIT 1", 
            Integer.class
        );

        Map<String, Object> req = new HashMap<>();
        req.put("customerId", customerId); 
        req.put("accountNumber", accNumber);
        req.put("amount", new BigDecimal("1000.00"));
        req.put("balance", new BigDecimal("1000.00"));
        req.put("accountType", "savings");
        req.put("bankName", "Test Bank");
        req.put("branch", "Test Branch");
        req.put("status", "ACTIVE");
        req.put("ifscCode", "TEST0001234");
        req.put("nameOnAccount", "Test User");
        req.put("phoneLinked", "9876543210");
        req.put("savingAmount", new BigDecimal("500.00"));

        ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
            baseUrl() + "/create", 
            HttpMethod.POST, 
            new HttpEntity<>(req), 
            MAP_TYPE_REF
        );

        assertThat(resp.getStatusCode()).isIn(HttpStatus.CREATED, HttpStatus.OK);

        return jdbc.queryForObject(
                "SELECT account_id FROM account_details WHERE account_number = ? LIMIT 1",
                Integer.class, accNumber);
    }

    
    //  Positive Cases
    

    @Test void update_phone_linked_success() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("phone_linked", "9123456789");

    ResponseEntity<Map<String, Object>> r = restTemplate.exchange(baseUrl() + "/" + id,
        HttpMethod.PUT, new HttpEntity<>(update), MAP_TYPE_REF);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        String phone = jdbc.queryForObject("SELECT phone_linked FROM account_details WHERE account_id=?", String.class, id);
        assertThat(phone).isEqualTo("9876543210");
    }

    @Test void update_name_on_account_success() {
        Integer accountId = createTestAccount(genAccNumber());
        Map<String, Object> update = new HashMap<>();
        update.put("name_on_account", "Updated User");

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            baseUrl() + "/" + accountId,
            HttpMethod.PUT,
            new HttpEntity<>(update),
            MAP_TYPE_REF
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("nameOnAccount");
        assertThat(response.getBody().get("nameOnAccount")).isEqualTo("Test User");
    }

    @Test void update_ifsc_code_success() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("ifsc_code", "NEWB0001234");

    ResponseEntity<Map<String, Object>> r = restTemplate.exchange(baseUrl() + "/" + id,
        HttpMethod.PUT, new HttpEntity<>(update), MAP_TYPE_REF);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        String ifsc = jdbc.queryForObject("SELECT ifsc_code FROM account_details WHERE account_id=?", String.class, id);
        assertThat(ifsc).isEqualTo("TEST0001234");
    }

    @Test void update_balance_success() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("balance", 2000.0);

    ResponseEntity<Map<String, Object>> r = restTemplate.exchange(baseUrl() + "/" + id,
        HttpMethod.PUT, new HttpEntity<>(update), MAP_TYPE_REF);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        Double bal = jdbc.queryForObject("SELECT balance FROM account_details WHERE account_id=?", Double.class, id);
        assertThat(bal).isEqualTo(2000.0);
    }

    @Test void update_account_type_success() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("account_type", "current");

    ResponseEntity<Map<String, Object>> r = restTemplate.exchange(baseUrl() + "/" + id,
        HttpMethod.PUT, new HttpEntity<>(update), MAP_TYPE_REF);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        String type = jdbc.queryForObject("SELECT account_type FROM account_details WHERE account_id=?", String.class, id);
        assertThat(type).isEqualTo("savings");
    }

    @Test void update_bank_name_success() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("bank_name", "HDFC");

    ResponseEntity<Map<String, Object>> r = restTemplate.exchange(baseUrl() + "/" + id,
        HttpMethod.PUT, new HttpEntity<>(update), MAP_TYPE_REF);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        String bank = jdbc.queryForObject("SELECT bank_name FROM account_details WHERE account_id=?", String.class, id);
        assertThat(bank).isEqualTo("Test Bank");
    }

    @Test void update_branch_success() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("branch", "Delhi Main");

    ResponseEntity<Map<String, Object>> r = restTemplate.exchange(baseUrl() + "/" + id,
        HttpMethod.PUT, new HttpEntity<>(update), MAP_TYPE_REF);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        String branch = jdbc.queryForObject("SELECT branch FROM account_details WHERE account_id=?", String.class, id);
        assertThat(branch).isEqualTo("Test Branch");
    }

    @Test void update_status_success() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("status", "INACTIVE");

    ResponseEntity<Map<String, Object>> r = restTemplate.exchange(baseUrl() + "/" + id,
        HttpMethod.PUT, new HttpEntity<>(update), MAP_TYPE_REF);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        String status = jdbc.queryForObject("SELECT status FROM account_details WHERE account_id=?", String.class, id);
        assertThat(status.toUpperCase()).isEqualTo("INACTIVE");
    }

    @Test void update_saving_amount_success() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("saving_amount", 750.0);

    ResponseEntity<Map<String, Object>> r = restTemplate.exchange(baseUrl() + "/" + id,
        HttpMethod.PUT, new HttpEntity<>(update), MAP_TYPE_REF);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        Double amt = jdbc.queryForObject("SELECT saving_amount FROM account_details WHERE account_id=?", Double.class, id);
        assertThat(amt).isEqualTo(500.0);
    }

    @Test void update_account_number_success() {
        Integer id = createTestAccount(genAccNumber());
        String newAcc = genAccNumber();
        Map<String,Object> update = Map.of("account_number", newAcc);

    ResponseEntity<Map<String, Object>> r = restTemplate.exchange(baseUrl() + "/" + id,
        HttpMethod.PUT, new HttpEntity<>(update), MAP_TYPE_REF);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        String acc = jdbc.queryForObject("SELECT account_number FROM account_details WHERE account_id=?", String.class, id);
        assertThat(acc).isEqualTo(newAcc);
    }

    
    //  Negative Cases
    

    @Test void update_phone_linked_invalid() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("phone_linked", "abc123");

        ResponseEntity<String> r = restTemplate.exchange(baseUrl() + "/" + id,
                HttpMethod.PUT, new HttpEntity<>(update), String.class);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test void update_name_on_account_blank() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("name_on_account", "");

        ResponseEntity<String> r = restTemplate.exchange(baseUrl() + "/" + id,
                HttpMethod.PUT, new HttpEntity<>(update), String.class);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test void update_ifsc_code_invalid() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("ifsc_code", "123");

        ResponseEntity<String> r = restTemplate.exchange(baseUrl() + "/" + id,
            HttpMethod.PUT, new HttpEntity<>(update), String.class);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test void update_balance_negative() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("balance", -500.0);

        ResponseEntity<String> r = restTemplate.exchange(baseUrl() + "/" + id,
                HttpMethod.PUT, new HttpEntity<>(update), String.class);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test void update_account_type_invalid() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("account_type", "crypto");

        ResponseEntity<String> r = restTemplate.exchange(baseUrl() + "/" + id,
                HttpMethod.PUT, new HttpEntity<>(update), String.class);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test void update_bank_name_blank() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("bank_name", "");

        ResponseEntity<String> r = restTemplate.exchange(baseUrl() + "/" + id,
                HttpMethod.PUT, new HttpEntity<>(update), String.class);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test void update_branch_blank() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("branch", "");

        ResponseEntity<String> r = restTemplate.exchange(baseUrl() + "/" + id,
                HttpMethod.PUT, new HttpEntity<>(update), String.class);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test void update_status_invalid() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("status", "paused");

        ResponseEntity<String> r = restTemplate.exchange(baseUrl() + "/" + id,
                HttpMethod.PUT, new HttpEntity<>(update), String.class);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test void update_saving_amount_negative() {
        Integer id = createTestAccount(genAccNumber());
        Map<String,Object> update = Map.of("saving_amount", -100.0);

        ResponseEntity<String> r = restTemplate.exchange(baseUrl() + "/" + id,
                HttpMethod.PUT, new HttpEntity<>(update), String.class);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test void update_account_number_duplicate() {
        // Create first account
        String acc1 = genAccNumber();
        createTestAccount(acc1);

        // Create second account 
        String acc2 = genAccNumber();
        Integer id2 = createTestAccount(acc2);

        // Try to update second account with first account's number
        Map<String,Object> update = Map.of("account_number", acc1); 

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            baseUrl() + "/" + id2,
            HttpMethod.PUT, 
            new HttpEntity<>(update),
            MAP_TYPE_REF
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        // Verify original account wasn't changed
        String unchangedNumber = jdbc.queryForObject(
            "SELECT account_number FROM account_details WHERE account_id = ?",
            String.class,
            id2
        );
        assertThat(unchangedNumber).isEqualTo(acc2);
    }

    @Test
    public void createTestAccount() {
        String accNumber = genAccNumber();
        Map<String, Object> req = new HashMap<>();
        req.put("customerId", 1); 
        req.put("accountNumber", accNumber);
        req.put("amount", new BigDecimal("1000.00"));
        req.put("balance", new BigDecimal("1000.00"));
        req.put("accountType", "savings");
        req.put("bankName", "Test Bank");
        req.put("branch", "Test Branch");
        req.put("status", "ACTIVE");
        req.put("ifscCode", "TEST0001234");
        req.put("nameOnAccount", "Test User");
        req.put("phoneLinked", "9876543210");
        req.put("savingAmount", new BigDecimal("500.00"));

        ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
            baseUrl() + "/create", 
            HttpMethod.POST, 
            new HttpEntity<>(req), 
            MAP_TYPE_REF
        );

        assertThat(resp.getStatusCode()).isIn(HttpStatus.CREATED, HttpStatus.OK);
    }
}

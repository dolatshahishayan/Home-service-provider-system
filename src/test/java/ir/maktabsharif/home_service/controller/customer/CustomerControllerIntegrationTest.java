package ir.maktabsharif.home_service.controller.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.customer.CustomerFindResponse;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.customer.CustomerMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.util.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMockConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerMapper customerMapper;

    private String customerToken;

    @BeforeEach
    void setup() {
        userService.deleteAll();
        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("test"));
        user.setIsEmailVerified(true);
        user.setRole(Role.ROLE_CUSTOMER);
        userService.save(user);
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        customerToken = jwtUtil.generateToken(userDetails);
    }

    @AfterAll
    void deleteUsers() {
        userService.deleteAll();
    }

    @Test
    void saveCustomer_ShouldReturnSavedCustomer() throws Exception {
        CustomerSaveUpdateRequest customerSaveUpdateRequest = new CustomerSaveUpdateRequest();
        customerSaveUpdateRequest.setFirstName("test");
        customerSaveUpdateRequest.setLastName("test");
        customerSaveUpdateRequest.setEmail("test@test.com");

        Customer customer = new Customer();
        customer.setId(1);
        customer.setFirstName("test");
        customer.setLastName("test");

        CustomerFindResponse customerFindResponse = new CustomerFindResponse();
        customerFindResponse.setId(1);
        customerFindResponse.setFirstName("test");
        customerFindResponse.setLastName("test");

        Mockito.when(customerService.register(Mockito.any(CustomerSaveUpdateRequest.class))).thenReturn(customer);
        Mockito.when(customerMapper.mapToResponse(Mockito.any(Customer.class))).thenReturn(customerFindResponse);

        mockMvc.perform(post("/api/v1/customers/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerSaveUpdateRequest))
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("test"))
                .andExpect(jsonPath("$.lastName").value("test"));
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer() throws Exception {
        CustomerSaveUpdateRequest customerSaveUpdateRequest = new CustomerSaveUpdateRequest();
        customerSaveUpdateRequest.setFirstName("test");
        customerSaveUpdateRequest.setLastName("test");
        customerSaveUpdateRequest.setEmail("test@test.com");

        Customer customer = new Customer();
        customer.setId(1);
        customer.setFirstName("test");
        customer.setLastName("test");

        CustomerFindResponse customerFindResponse = new CustomerFindResponse();
        customerFindResponse.setId(1);
        customerFindResponse.setFirstName("test");
        customerFindResponse.setLastName("test");

        Mockito.when(customerService.updateWithDTO(Mockito.any(CustomerSaveUpdateRequest.class))).thenReturn(customer);
        Mockito.when(customerMapper.mapToResponse(Mockito.any(Customer.class))).thenReturn(customerFindResponse);

        mockMvc.perform(put("/api/v1/customers/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerSaveUpdateRequest))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("test"))
                .andExpect(jsonPath("$.lastName").value("test"));
    }

    @Test
    void findByEmail_ShouldReturnCustomer() throws Exception {
        String email = "test@test.com";

        Customer customer = new Customer();
        customer.setId(1);
        customer.setFirstName("test");
        customer.setLastName("test");
        customer.setEmail(email);

        CustomerFindResponse customerFindResponse = new CustomerFindResponse();
        customerFindResponse.setId(1);
        customerFindResponse.setFirstName("test");
        customerFindResponse.setLastName("test");
        customerFindResponse.setIsEmailVerified(true);

        Mockito.when(customerService.findByEmail(Mockito.eq(email)))
                .thenReturn(customer);

        Mockito.when(customerMapper.mapToResponse(Mockito.any(Customer.class)))
                .thenReturn(customerFindResponse);

        mockMvc.perform(get("/api/v1/customers/find-by-email")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("test"))
                .andExpect(jsonPath("$.lastName").value("test"))
                .andExpect(jsonPath("$.isEmailVerified").value(true));
    }

}
package ir.maktabsharif.home_service.controller.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.customer.CustomerFindResponse;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.customer.CustomerMapper;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.util.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMockConfig.class)
class CustomerControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private SecurityContextUtil securityContextUtil;
    @Autowired
    private JwtUtil mockJwtUtil;



    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1);
        user.setEmail("user@test.com");
        UserDetailsImpl principal = new UserDetailsImpl(user);

        Mockito.when(securityContextUtil.getCurrentUser()).thenReturn(principal);
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
        Mockito.when(mockJwtUtil.generateToken(Mockito.any(UserDetailsImpl.class))).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/v1/customers/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerSaveUpdateRequest)))
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
        Mockito.when(mockJwtUtil.generateToken(Mockito.any(UserDetailsImpl.class))).thenReturn("fake-jwt-token");

        mockMvc.perform(put("/api/v1/customers/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerSaveUpdateRequest)))
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
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("test"))
                .andExpect(jsonPath("$.lastName").value("test"))
                .andExpect(jsonPath("$.isEmailVerified").value(true));
    }

}
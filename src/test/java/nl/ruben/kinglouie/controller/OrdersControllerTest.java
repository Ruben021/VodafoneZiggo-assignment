package nl.ruben.kinglouie.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import nl.ruben.kinglouie.KinglouieApplication;
import nl.ruben.kinglouie.entity.OrderEntity;
import nl.ruben.kinglouie.model.order.CreateOrder;
import nl.ruben.kinglouie.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.file.Files;
import java.util.Random;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = KinglouieApplication.class
)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integration-test.yml"
)
class OrdersControllerTest {

    private static final String EMAIL = UUID.randomUUID().toString();
    private static final String FIRST_NAME = UUID.randomUUID().toString();
    private static final String LAST_NAME = UUID.randomUUID().toString();
    private static final int PRODUCT_ID = new Random().nextInt(1000000);

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void findOrders() {
        OrderEntity orderEntity = createOrderEntity();

        mvc.perform(MockMvcRequestBuilders.get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderID").value(orderEntity.getOrderID()))
                .andExpect(jsonPath("$[0].email").value(EMAIL))
                .andExpect(jsonPath("$[0].productID").value(PRODUCT_ID))
                .andExpect(jsonPath("$[0].first_name").value(FIRST_NAME))
                .andExpect(jsonPath("$[0].last_name").value(LAST_NAME));
    }

    private OrderEntity createOrderEntity() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setEmail(EMAIL);
        orderEntity.setProductID(PRODUCT_ID);
        orderEntity.setFirstName(FIRST_NAME);
        orderEntity.setLastName(LAST_NAME);
        return orderRepository.save(orderEntity);
    }

    @Nested
    @DisplayName("#createOrder")
    class CreateOrderController {

        private static final String EMAIL_FOR_CREATE_ORDER = "tracey.ramos@reqres.in";

        @SneakyThrows
        public static String getJsonFromResourceFile(String path) {
            File backendResponse = new ClassPathResource(path).getFile();
            return Files.readString(backendResponse.toPath());
        }

        @Test
        @SneakyThrows
        void createOrder() {
            String usersPage1response = getJsonFromResourceFile("json/usersPage1.json");

            configureFor("reqres.in", 80);
            stubFor(get("/api/users").willReturn(ok().withBody(usersPage1response)));

            CreateOrder createOrder = new CreateOrder();
            createOrder.setProductID(PRODUCT_ID);
            createOrder.setEmail(EMAIL_FOR_CREATE_ORDER);

            mvc.perform(
                            MockMvcRequestBuilders.post("/api/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(getContentFromObject(createOrder))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.orderID").isNotEmpty());
        }

        private String getContentFromObject(CreateOrder createOrder) throws JsonProcessingException {
            return new ObjectMapper().writeValueAsString(createOrder);
        }
    }
}
package nl.ruben.kinglouie.repository;

import nl.ruben.kinglouie.entity.OrderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class OrderRepositoryTest {

    private static final String EMAIL = UUID.randomUUID().toString();
    private static final String FIRST_NAME = UUID.randomUUID().toString();
    private static final String LAST_NAME = UUID.randomUUID().toString();
    private static final int PRODUCT_ID = new Random().nextInt(1000000);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("A result")
    void findByProductIDAndEmail() {
        OrderEntity order = createOrderEntity();

        OrderEntity response = orderRepository.findByProductIDAndEmail(PRODUCT_ID, EMAIL).get();

        assertAll(
                () -> assertEquals(order.getOrderID(), response.getOrderID()),
                () -> assertEquals(EMAIL, response.getEmail()),
                () -> assertEquals(FIRST_NAME, response.getFirstName()),
                () -> assertEquals(LAST_NAME, response.getLastName()),
                () -> assertEquals(PRODUCT_ID, response.getProductID())
        );
    }

    @Test
    @DisplayName("No result")
    void findByProductIDAndEmailNoResult() {
        createOrderEntity();

        Optional<OrderEntity> response = orderRepository.findByProductIDAndEmail(-1, "random");

        assertTrue(response.isEmpty());
    }

    private OrderEntity createOrderEntity() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setEmail(EMAIL);
        orderEntity.setProductID(PRODUCT_ID);
        orderEntity.setFirstName(FIRST_NAME);
        orderEntity.setLastName(LAST_NAME);
        return entityManager.persistAndFlush(orderEntity);
    }
}
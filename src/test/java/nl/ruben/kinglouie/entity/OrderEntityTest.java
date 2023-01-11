package nl.ruben.kinglouie.entity;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityTest {

    private static final int ORDER_ID_1 = new Random().nextInt(1000000);
    private static final int ORDER_ID_2 = new Random().nextInt(1000000);

    @Test
    void testNotEquals() {
        OrderEntity orderEntity1 = OrderEntity.builder()
                .orderID(ORDER_ID_1)
                .build();
        OrderEntity orderEntity2 = OrderEntity.builder()
                .orderID(ORDER_ID_2)
                .build();

        assertNotEquals(orderEntity1, orderEntity2);
    }

    @Test
    void testEquals() {
        OrderEntity orderEntity1 = OrderEntity.builder()
                .orderID(ORDER_ID_1)
                .build();
        OrderEntity orderEntity2 = orderEntity1;

        assertEquals(orderEntity1, orderEntity2);
    }
}
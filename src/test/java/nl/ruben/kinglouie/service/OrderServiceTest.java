package nl.ruben.kinglouie.service;

import nl.ruben.kinglouie.entity.OrderEntity;
import nl.ruben.kinglouie.model.order.CreateOrder;
import nl.ruben.kinglouie.model.order.Order;
import nl.ruben.kinglouie.model.order.OrderID;
import nl.ruben.kinglouie.model.user.User;
import nl.ruben.kinglouie.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("#createOrder")
    class CreateOrderService {

        private static final int PRODUCT_ID = new Random().nextInt(100000);
        private static final String EMAIL = UUID.randomUUID().toString();

        @Test
        void createOrder() {
            CreateOrder createOrder = mock(CreateOrder.class);
            User user = mock(User.class);
            OrderEntity orderEntity = mock(OrderEntity.class);

            when(createOrder.getEmail()).thenReturn(EMAIL);
            when(createOrder.getProductID()).thenReturn(PRODUCT_ID);
            when(userService.getUserToCreateOrderFor(EMAIL)).thenReturn(user);
            when(orderRepository.findByProductIDAndEmail(PRODUCT_ID, EMAIL)).thenReturn(Optional.empty());
            when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

            orderService.createOrder(createOrder);

            verify(orderRepository).save(any(OrderEntity.class));
        }
    }

    @Nested
    @DisplayName("#checkIfProductIsOrderedBeforeByUser")
    class CheckIfProductIsOrderedBeforeByUser {

        private static final int PRODUCT_ID = new Random().nextInt(100000);
        private static final String EMAIL = UUID.randomUUID().toString();

        @Test
        @DisplayName("No order found, expect no exception")
        void checkIfProductIsOrderedBeforeByUserNoOrderFound() {
            when(orderRepository.findByProductIDAndEmail(PRODUCT_ID, EMAIL)).thenReturn(Optional.empty());

            assertDoesNotThrow(() -> orderService.checkIfProductIsOrderedBeforeByUser(PRODUCT_ID, EMAIL));
        }

        @Test
        @DisplayName("Order found, expect exception")
        void checkIfProductIsOrderedBeforeByUserOrderFound() {
            OrderEntity orderEntity = mock(OrderEntity.class);

            when(orderRepository.findByProductIDAndEmail(PRODUCT_ID, EMAIL)).thenReturn(Optional.of(orderEntity));

            assertThrows(ResponseStatusException.class, () -> orderService.checkIfProductIsOrderedBeforeByUser(PRODUCT_ID, EMAIL));
        }
    }

    @Nested
    @DisplayName("#buildResponseObject")
    class BuildResponseObject {
        private static final int ORDER_ID = new Random().nextInt(100000);

        @Test
        void buildResponseObject() {
            OrderEntity orderEntity = OrderEntity.builder()
                    .orderID(ORDER_ID)
                    .build();

            OrderID orderID = orderService.buildResponseObject(orderEntity);

            assertEquals(ORDER_ID, orderID.getOrderID());
        }
    }

    @Nested
    @DisplayName("#buildOrder")
    class BuildOrder {

        private static final int PRODUCT_ID = new Random().nextInt(100000);
        private static final String EMAIL = UUID.randomUUID().toString();
        private static final String FIRST_NAME = UUID.randomUUID().toString();
        private static final String LAST_NAME = UUID.randomUUID().toString();

        @Test
        void buildOrder() {
            CreateOrder createOrder = new CreateOrder();
            createOrder.setProductID(PRODUCT_ID);

            User userToCreateOrderFor = new User();
            userToCreateOrderFor.setEmail(EMAIL);
            userToCreateOrderFor.setFirstName(FIRST_NAME);
            userToCreateOrderFor.setLastName(LAST_NAME);

            OrderEntity orderEntity = orderService.buildOrder(createOrder, userToCreateOrderFor);

            assertAll(
                    () -> assertEquals(PRODUCT_ID, orderEntity.getProductID()),
                    () -> assertEquals(EMAIL, orderEntity.getEmail()),
                    () -> assertEquals(FIRST_NAME, orderEntity.getFirstName()),
                    () -> assertEquals(LAST_NAME, orderEntity.getLastName())
            );
        }
    }

    @Nested
    @DisplayName("#findOrders")
    class FindOrders {

        @Test
        @DisplayName("No order")
        void findOrdersNoOrders() {
            when(orderRepository.findAll()).thenReturn(Collections.emptyList());

            orderService.findOrders();

            verify(orderMapper, never()).mapToOrder(any());
        }

        @Test
        @DisplayName("One order")
        void findOrders() {
            OrderEntity orderEntity = mock(OrderEntity.class);
            Order order = mock(Order.class);

            when(orderRepository.findAll()).thenReturn(List.of(orderEntity));
            when(orderMapper.mapToOrder(orderEntity)).thenReturn(order);

            orderService.findOrders();

            verify(orderMapper).mapToOrder(orderEntity);
        }

        @Test
        @DisplayName("Two order")
        void findOrdersTwoOrders() {
            OrderEntity orderEntity1 = mock(OrderEntity.class);
            OrderEntity orderEntity2 = mock(OrderEntity.class);
            Order order1 = mock(Order.class);
            Order order2 = mock(Order.class);

            when(orderRepository.findAll()).thenReturn(List.of(orderEntity1, orderEntity2));
            when(orderMapper.mapToOrder(orderEntity1)).thenReturn(order1);
            when(orderMapper.mapToOrder(orderEntity2)).thenReturn(order2);

            orderService.findOrders();

            verify(orderMapper, times(2)).mapToOrder(any(OrderEntity.class));
        }
    }
}
package nl.ruben.kinglouie.service;

import lombok.RequiredArgsConstructor;
import nl.ruben.kinglouie.entity.OrderEntity;
import nl.ruben.kinglouie.model.order.CreateOrder;
import nl.ruben.kinglouie.model.order.Order;
import nl.ruben.kinglouie.model.order.OrderID;
import nl.ruben.kinglouie.model.user.User;
import nl.ruben.kinglouie.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final UserService userService;

    public OrderID createOrder(final CreateOrder createOrder) {
        final User userToCreateOrderFor = userService.getUserToCreateOrderFor(createOrder.getEmail());

        checkIfProductIsOrderedBeforeByUser(createOrder.getProductID(), createOrder.getEmail());

        return Optional.of(this.buildOrder(createOrder, userToCreateOrderFor))
                .map(orderRepository::save)
                .map(this::buildResponseObject)
                .orElseThrow();
    }

    void checkIfProductIsOrderedBeforeByUser(final Integer productId, final String email) {
        orderRepository.findByProductIDAndEmail(productId, email)
                .ifPresent(order -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "User already ordered this product");
                });
    }

    OrderID buildResponseObject(final OrderEntity orderEntity) {
        OrderID orderID = new OrderID();
        orderID.setOrderID(orderEntity.getOrderID());
        return orderID;
    }

    OrderEntity buildOrder(final CreateOrder createOrder, final User userToCreateOrderFor) {
        return OrderEntity.builder()
                .email(userToCreateOrderFor.getEmail())
                .firstName(userToCreateOrderFor.getFirstName())
                .lastName(userToCreateOrderFor.getLastName())
                .productID(createOrder.getProductID())
                .build();
    }

    public List<Order> findOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::mapToOrder)
                .toList();
    }
}

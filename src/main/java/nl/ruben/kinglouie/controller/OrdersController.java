package nl.ruben.kinglouie.controller;

import lombok.RequiredArgsConstructor;
import nl.ruben.kinglouie.api.order.OrdersApi;
import nl.ruben.kinglouie.model.order.CreateOrder;
import nl.ruben.kinglouie.model.order.Order;
import nl.ruben.kinglouie.model.order.OrderID;
import nl.ruben.kinglouie.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.orders.base-path:/api}")
public class OrdersController implements OrdersApi {

    private final OrderService orderService;

    @Override
    public ResponseEntity<OrderID> createOrder(final CreateOrder createOrder) {
        return ResponseEntity.ok(
                orderService.createOrder(createOrder)
        );
    }

    @Override
    public ResponseEntity<List<Order>> findOrders() {
        return ResponseEntity.ok(
                orderService.findOrders()
        );
    }
}

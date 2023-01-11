package nl.ruben.kinglouie.service;

import nl.ruben.kinglouie.entity.OrderEntity;
import nl.ruben.kinglouie.model.order.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderMapper {

    Order mapToOrder(OrderEntity orderEntity);
}

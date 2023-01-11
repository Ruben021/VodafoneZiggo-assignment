package nl.ruben.kinglouie.repository;

import nl.ruben.kinglouie.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {

    Optional<OrderEntity> findByProductIDAndEmail(final Integer productID, final String email);
}

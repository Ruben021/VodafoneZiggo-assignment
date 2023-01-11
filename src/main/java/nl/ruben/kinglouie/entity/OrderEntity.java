package nl.ruben.kinglouie.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@Entity(name = "Orders")
@NoArgsConstructor(force = true)
public class OrderEntity {
    @Id
    @GeneratedValue
    private Integer orderID;

    private String email;

    private String firstName;

    private String lastName;

    private Integer productID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderEntity that = (OrderEntity) o;
        return orderID != null && Objects.equals(orderID, that.orderID);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

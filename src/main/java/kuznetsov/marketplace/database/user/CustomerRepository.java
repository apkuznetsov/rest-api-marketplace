package kuznetsov.marketplace.database.user;

import java.util.Optional;
import kuznetsov.marketplace.models.user.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

  @Query("SELECT customer FROM Customer customer "
      + "INNER JOIN User user ON customer.user.id = user.id "
      + "WHERE user.email =: email AND user.role = 'CUSTOMER'")
  Optional<Customer> findByUserEmail(String email);

}

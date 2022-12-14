package kuznetsov.marketplace.models.preorder;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import kuznetsov.marketplace.models.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Data
@Table(name = "preorder")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreorderInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "cent_price_without_discount", nullable = false)
  private Long centPriceWithoutDiscount;

  @Column(name = "preorder_expected_quantity", nullable = false)
  private Integer preorderExpectedQuantity;

  @Column(name = "preorder_ends_at", nullable = false)
  private LocalDateTime preorderEndsAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "preorder_status", nullable = false)
  @ColumnDefault("'STARTED'")
  PreorderStatus preorderStatus;

  @OneToOne(mappedBy = "preorderInfo")
  private Product product;

}

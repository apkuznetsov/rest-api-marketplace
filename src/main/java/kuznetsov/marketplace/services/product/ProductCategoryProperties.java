package kuznetsov.marketplace.services.product;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "product.category")
@Data
public class ProductCategoryProperties {

  private Integer pageSize;

}

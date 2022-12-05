package kuznetsov.marketplace.services.product;

import kuznetsov.marketplace.domain.product.Product;
import kuznetsov.marketplace.domain.product.ProductCategory;
import kuznetsov.marketplace.domain.user.Seller;
import kuznetsov.marketplace.services.product.dto.ProductCategoryDto;
import kuznetsov.marketplace.services.product.dto.ProductDto;
import kuznetsov.marketplace.services.product.dto.ProductSellerDto;

public interface ProductMapper {

  default Product toProduct(ProductDto productDto) {
    return Product.builder()
        .id(null)
        .title(productDto.getTitle())
        .description(productDto.getDescription())
        .techDescription(productDto.getTechDescription())
        .category(null)
        .centPrice((long) (productDto.getPrice() * 100.0))
        .seller(null)
        .imageUrls(null)
        .build();
  }

  default Product toProduct(ProductDto productDto, ProductCategory category, Seller seller) {
    Product product = this.toProduct(productDto);
    product.setCategory(category);
    product.setSeller(seller);

    return product;
  }

  default ProductDto toProductDto(Product product, ProductCategory category, Seller seller) {
    ProductCategoryDto categoryDto = this.toProductCategoryDto(category);
    ProductSellerDto sellerDto = this.toProductSellerDto(seller);

    return ProductDto.builder()
        .id(product.getId())
        .title(product.getTitle())
        .description(product.getDescription())
        .techDescription(product.getTechDescription())
        .categoryId(categoryDto.getId())
        .categoryName(categoryDto.getName())
        .price(product.getCentPrice() / 100.0)
        .sellerName(sellerDto.getName())
        .sellerAddress(sellerDto.getAddress())
        .sellerEmail(sellerDto.getEmail())
        .imageUrls(null)
        .build();
  }

  private ProductCategoryDto toProductCategoryDto(ProductCategory category) {
    Long categoryId;
    String categoryName;
    if (category != null) {
      categoryId = category.getId();
      categoryName = category.getName();
    } else {
      categoryId = null;
      categoryName = null;
    }

    return ProductCategoryDto.builder()
        .id(categoryId)
        .name(categoryName)
        .build();
  }

  private ProductSellerDto toProductSellerDto(Seller seller) {
    return ProductSellerDto.builder()
        .name(seller.getName())
        .address(seller.getAddress())
        .email(seller.getPublicEmail())
        .build();
  }

}
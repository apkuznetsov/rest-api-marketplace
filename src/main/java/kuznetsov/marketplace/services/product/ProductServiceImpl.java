package kuznetsov.marketplace.services.product;

import static kuznetsov.marketplace.services.pagination.PageErrorCode.NOT_POSITIVE_PAGE_NUMBER;
import static kuznetsov.marketplace.services.product.ProductCategoryErrorCode.PRODUCT_CATEGORY_NOT_FOUND;
import static kuznetsov.marketplace.services.product.ProductErrorCode.PRODUCT_NOT_FOUND;
import static kuznetsov.marketplace.services.user.SellerErrorCode.SELLER_NOT_FOUND;
import static kuznetsov.marketplace.services.user.UserErrorCode.USER_HAS_NO_PERMISSION;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import kuznetsov.marketplace.database.product.ProductCategoryRepository;
import kuznetsov.marketplace.database.product.ProductImageUrlRepository;
import kuznetsov.marketplace.database.product.ProductRepository;
import kuznetsov.marketplace.database.user.SellerRepository;
import kuznetsov.marketplace.models.product.Product;
import kuznetsov.marketplace.models.product.ProductCategory;
import kuznetsov.marketplace.models.product.ProductImageUrl;
import kuznetsov.marketplace.models.user.Seller;
import kuznetsov.marketplace.services.exception.ServiceException;
import kuznetsov.marketplace.services.product.dto.ProductDto;
import kuznetsov.marketplace.services.product.dto.ProductDtoPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductProperties productProps;
  private final ProductMapper productMapper;
  private final ProductValidator productValidator;

  private final ProductRepository productRepo;
  private final ProductCategoryRepository categoryRepo;
  private final ProductImageUrlRepository imageUrlRepo;
  private final SellerRepository sellerRepo;


  @Override
  @Transactional
  public ProductDto addSellerProduct(String sellerEmail, ProductDto productDto) {
    productValidator.validateOrThrow(productProps, productDto);

    Seller seller = sellerRepo.findByUserEmail(sellerEmail)
        .orElseThrow(() -> new ServiceException(SELLER_NOT_FOUND));
    ProductCategory category = categoryRepo.findById(productDto.getCategoryId())
        .orElseThrow(() -> new ServiceException(PRODUCT_CATEGORY_NOT_FOUND));

    Product product = productMapper.toProduct(productDto, category, seller);
    Product savedProduct = productRepo.saveAndFlush(product);

    List<ProductImageUrl> savedImageUrls = saveAllAndFlushProductImageUrls(
        savedProduct, productDto.getImageUrls());
    savedProduct.setImageUrls(savedImageUrls);

    return productMapper.toProductDto(savedProduct, category, seller);
  }

  @Override
  @Transactional
  public ProductDto updateSellerProductById(
      String sellerEmail, long productId, ProductDto productDto) {

    productValidator.validateOrThrow(productProps, productDto);

    Seller seller = sellerRepo.findByUserEmail(sellerEmail)
        .orElseThrow(() -> new ServiceException(SELLER_NOT_FOUND));
    Product product = productRepo.findById(productId)
        .orElseThrow(() -> new ServiceException(PRODUCT_NOT_FOUND));
    if (seller != product.getSeller()) {
      throw new ServiceException(USER_HAS_NO_PERMISSION);
    }

    ProductCategory category = categoryRepo.findById(productDto.getCategoryId())
        .orElseThrow(() -> new ServiceException(PRODUCT_CATEGORY_NOT_FOUND));
    imageUrlRepo.deleteAllByProduct_Id(productId);

    Product newProduct = productMapper.toProduct(productDto, category, seller);
    newProduct.setId(productId);
    Product updatedProduct = productRepo.saveAndFlush(newProduct);

    return productMapper.toProductDto(updatedProduct);
  }

  @Override
  public ProductDto getProductById(long productId) {
    Product product = productRepo.findById(productId)
        .orElseThrow(() -> new ServiceException(PRODUCT_NOT_FOUND));

    return productMapper.toProductDto(product, product.getCategory(), product.getSeller());
  }

  @Override
  public ProductDtoPage getPagedProductsByCategoryId(long categoryId, int pageNum) {
    --pageNum;
    if (pageNum < 0) {
      throw new ServiceException(NOT_POSITIVE_PAGE_NUMBER);
    }

    Pageable page = PageRequest.of(pageNum, productProps.getPageSize());
    Page<Product> pagedProducts = productRepo.findAllByCategory_Id(categoryId, page);

    return productMapper.toProductDtoPage(pagedProducts);
  }

  @Override
  public ProductDtoPage getPagedSellerProducts(String sellerEmail, int pageNum) {
    --pageNum;
    if (pageNum < 0) {
      throw new ServiceException(NOT_POSITIVE_PAGE_NUMBER);
    }

    Seller seller = sellerRepo.findByUserEmail(sellerEmail)
        .orElseThrow(() -> new ServiceException(SELLER_NOT_FOUND));
    Pageable page = PageRequest.of(pageNum, productProps.getPageSize());
    Page<Product> pagedProducts = productRepo
        .findAllBySellerAndPreorderInfo_IdIsNull(seller, page);

    return productMapper.toProductDtoPage(pagedProducts);
  }

  @Override
  public void deleteSellerProductById(String sellerEmail, long productId) {
    Seller seller = sellerRepo.findByUserEmail(sellerEmail)
        .orElseThrow(() -> new ServiceException(SELLER_NOT_FOUND));
    Product product = productRepo.findById(productId)
        .orElseThrow(() -> new ServiceException(PRODUCT_NOT_FOUND));
    if (seller != product.getSeller()) {
      throw new ServiceException(USER_HAS_NO_PERMISSION);
    }
  }

  private List<ProductImageUrl> saveAllAndFlushProductImageUrls(
      Product savedProduct, List<String> imageUrls) {

    if (imageUrls == null) {
      return null;
    }

    List<ProductImageUrl> piuList = imageUrls.stream()
        .map(currUrl -> ProductImageUrl.builder()
            .product(savedProduct)
            .url(currUrl)
            .build())
        .collect(Collectors.toList());

    return imageUrlRepo.saveAllAndFlush(piuList);
  }

}

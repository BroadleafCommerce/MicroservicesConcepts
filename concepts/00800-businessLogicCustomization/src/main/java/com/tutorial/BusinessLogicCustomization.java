package com.tutorial;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import com.broadleafcommerce.catalog.domain.CategoryProduct;
import com.broadleafcommerce.catalog.domain.product.Variant;
import com.broadleafcommerce.catalog.repository.product.ProductRepository;
import com.broadleafcommerce.catalog.service.CategoryProductService;
import com.broadleafcommerce.catalog.service.autoconfigure.CatalogServiceAutoConfiguration;
import com.broadleafcommerce.catalog.service.product.VariantService;
import com.broadleafcommerce.common.extension.TypeFactory;
import com.broadleafcommerce.common.extension.cache.CacheStateManager;
import com.broadleafcommerce.data.tracking.core.Trackable;
import com.broadleafcommerce.data.tracking.core.filtering.fetch.FilterParser;
import com.broadleafcommerce.data.tracking.core.service.RsqlCrudEntityHelper;
import com.tutorial.service.ExtendedProductService;
import com.tutorial.service.MyIntegrationService;
import cz.jirutka.rsql.parser.ast.Node;

/**
 * Setup components for the extension case.
 */
@Configuration
@AutoConfigureBefore(CatalogServiceAutoConfiguration.class)
public class BusinessLogicCustomization {

    @Bean
    public MyIntegrationService integrationService() {
        return new MyIntegrationService();
    }

    @Bean
    public ExtendedProductService productService(
            ProductRepository<Trackable> productRepository,
            RsqlCrudEntityHelper helper,
            VariantService<Variant> variantService,
            CategoryProductService<CategoryProduct> categoryProductService,
            @Nullable CacheStateManager cacheStateManager,
            @Nullable FilterParser<Node> parser,
            TypeFactory typeFactory,
            MyIntegrationService integrationService) {
        return new ExtendedProductService(productRepository,
                helper,
                variantService,
                categoryProductService,
                cacheStateManager,
                parser,
                typeFactory,
                integrationService);
    }

}

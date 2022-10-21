package com.tutorial.service;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import com.broadleafcommerce.catalog.domain.CategoryProduct;
import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.domain.product.Variant;
import com.broadleafcommerce.catalog.repository.product.ProductRepository;
import com.broadleafcommerce.catalog.service.CategoryProductService;
import com.broadleafcommerce.catalog.service.product.DefaultProductService;
import com.broadleafcommerce.catalog.service.product.VariantService;
import com.broadleafcommerce.common.extension.TypeFactory;
import com.broadleafcommerce.common.extension.cache.CacheStateManager;
import com.broadleafcommerce.data.tracking.core.Trackable;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.core.filtering.fetch.FilterParser;
import com.broadleafcommerce.data.tracking.core.service.RsqlCrudEntityHelper;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.NonNull;

@Component
public class ExtendedProductService extends DefaultProductService<Product> {

    private MyIntegrationService myIntegrationService;

    public ExtendedProductService(ProductRepository<Trackable> repository,
            RsqlCrudEntityHelper helper,
            VariantService<Variant> variantService,
            CategoryProductService<CategoryProduct> categoryProductService,
            @Nullable CacheStateManager cacheStateManager,
            @Nullable FilterParser<Node> parser,
            TypeFactory typeFactory,
            MyIntegrationService integrationService) {
        super(repository, helper, variantService, categoryProductService, cacheStateManager, parser,
                typeFactory);
        this.myIntegrationService = integrationService;
    }

    @Override
    public Product create(@NonNull Product businessInstance, ContextInfo context) {
        Product created = super.create(businessInstance, context);
        myIntegrationService.register(created);
        return created;
    }

}

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
import com.tutorial.projection.ElectricCarProjection;

import cz.jirutka.rsql.parser.ast.Node;
import lombok.NonNull;

/**
 * It is useful in this service extension to cast to the more derived projection when needed.
 * However, the generics should be left with the parent type in order to guarantee proper bean type
 * recognition by spring during application context refresh.
 */
@Component
public class ElectricCarService extends DefaultProductService<Product> {

    private MyIntegrationService myIntegrationService;

    public ElectricCarService(ProductRepository<Trackable> repository,
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
        ElectricCarProjection created =
                (ElectricCarProjection) super.create(businessInstance, context);
        myIntegrationService.register(created);
        return created;
    }

}

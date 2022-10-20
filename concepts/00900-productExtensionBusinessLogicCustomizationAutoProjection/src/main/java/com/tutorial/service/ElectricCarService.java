package com.tutorial.service;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import com.broadleafcommerce.catalog.domain.CategoryProduct;
import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.domain.product.Variant;
import com.broadleafcommerce.catalog.repository.product.ProductRepository;
import com.broadleafcommerce.catalog.service.CategoryProductService;
import com.broadleafcommerce.catalog.service.product.DefaultProductService;
import com.broadleafcommerce.catalog.service.product.VariantService;
import com.broadleafcommerce.common.extension.TypeFactory;
import com.broadleafcommerce.common.extension.cache.CacheStateManager;
import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.data.tracking.core.Trackable;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.core.filtering.fetch.FilterParser;
import com.broadleafcommerce.data.tracking.core.service.RsqlCrudEntityHelper;
import com.tutorial.domain.ElectricCar;
import com.tutorial.repository.ElectricCarRepositoryConcreteContribution;

import java.util.List;
import cz.jirutka.rsql.parser.ast.Node;

public class ElectricCarService extends DefaultProductService<Product> {

    public ElectricCarService(ProductRepository<Trackable> repository,
            RsqlCrudEntityHelper helper,
            VariantService<Variant> variantService,
            CategoryProductService<CategoryProduct> categoryProductService,
            CacheStateManager cacheStateManager,
            FilterParser<Node> parser,
            TypeFactory typeFactory) {
        super(repository, helper, variantService, categoryProductService, cacheStateManager, parser,
                typeFactory);
    }

    public List<Projection<ElectricCar>> readUsingModel(@NonNull String model,
            @Nullable ContextInfo contextInfo) {
        return this.getHelper().getMapper()
                .process(((ElectricCarRepositoryConcreteContribution) getRepository())
                        .findUsingModel(model, contextInfo), contextInfo);
    }

}

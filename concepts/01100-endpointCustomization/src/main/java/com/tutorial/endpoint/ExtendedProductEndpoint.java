package com.tutorial.endpoint;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.broadleafcommerce.bulk.domain.BulkUpdate;
import com.broadleafcommerce.bulk.service.BulkUpdateManager;
import com.broadleafcommerce.bulk.service.BulkUpdateService;
import com.broadleafcommerce.catalog.clone.product.CloneProductRequest;
import com.broadleafcommerce.catalog.clone.product.service.CloneProductService;
import com.broadleafcommerce.catalog.domain.CategoryProduct;
import com.broadleafcommerce.catalog.domain.PromotionalProduct;
import com.broadleafcommerce.catalog.domain.category.Category;
import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.domain.product.Variant;
import com.broadleafcommerce.catalog.service.CategoryProductService;
import com.broadleafcommerce.catalog.service.CategoryService;
import com.broadleafcommerce.catalog.service.product.OptionGenerationService;
import com.broadleafcommerce.catalog.service.product.ProductService;
import com.broadleafcommerce.catalog.service.product.SkuGenerationService;
import com.broadleafcommerce.catalog.service.product.consolidation.ProductConsolidationService;
import com.broadleafcommerce.catalog.service.product.hydration.ProductHydrationService;
import com.broadleafcommerce.catalog.service.product.relation.PromotionalProductService;
import com.broadleafcommerce.catalog.web.endpoint.ProductEndpoint;
import com.broadleafcommerce.common.extension.TypeFactory;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.core.context.ContextInfoCustomizer;
import com.broadleafcommerce.data.tracking.core.context.ContextOperation;
import com.broadleafcommerce.data.tracking.core.filtering.fetch.rsql.web.RsqlFilterHandlerMethodArgumentResolver;
import com.broadleafcommerce.data.tracking.core.policy.Policy;
import com.broadleafcommerce.data.tracking.core.type.OperationType;
import com.broadleafcommerce.translation.domain.Translation;
import com.broadleafcommerce.translation.service.TranslationEntityService;
import javax.servlet.http.HttpServletRequest;

/**
 * Override an endpoint from {@link ProductEndpoint}. It is important that all the
 * {@link FrameworkMapping} (and friends) are replaced with {@link RequestMapping} annotations.
 * {@link FrameworkRestController} should also be replaced with {@link RestController}. Finally, any
 * {@link Policy} annotations should be repeated here.
 */
@RestController
@RequestMapping({"/products"})
public class ExtendedProductEndpoint extends ProductEndpoint {

    public ExtendedProductEndpoint(ProductService<Product> productSvc,
            CloneProductService<Product, CloneProductRequest> cloneProductSvc,
            SkuGenerationService<Variant> skuGenerationSvc,
            PromotionalProductService<PromotionalProduct> promotionalProductSvc,
            CategoryService<Category> categoryService,
            TranslationEntityService<Translation> translationEntityService,
            CategoryProductService<CategoryProduct> categoryProductService,
            BulkUpdateManager bulkUpdateManager,
            BulkUpdateService<BulkUpdate> bulkUpdateService,
            TypeFactory catalogFactory,
            ProductConsolidationService productConsolidationService,
            ProductHydrationService productHydrationService,
            OptionGenerationService<Product> optionGenerationService,
            RsqlFilterHandlerMethodArgumentResolver rsqlResolver) {
        super(productSvc, cloneProductSvc, skuGenerationSvc, promotionalProductSvc, categoryService,
                translationEntityService, categoryProductService,
                bulkUpdateManager, bulkUpdateService, catalogFactory, productConsolidationService,
                productHydrationService, optionGenerationService,
                rsqlResolver);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"})
    @Policy(permissionRoots = {"PRODUCT"})
    @Override
    public Product createProduct(HttpServletRequest request,
            @ContextOperation(OperationType.CREATE) ContextInfo context,
            @RequestBody Product req) {
        /**
         * Note - {@link ContextInfoCustomizer} is another powerful way to manipulate ContextInfo
         * across multiple request types.
         */
        context.getAdditionalProperties().put("MyValue", "Test");
        return super.createProduct(request, context, req);
    }
}

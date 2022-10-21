package com.tutorial.endpoint;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkGetMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkPostMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkPutMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.core.context.ContextOperation;
import com.broadleafcommerce.data.tracking.core.filtering.fetch.rsql.web.RsqlFilterHandlerMethodArgumentResolver;
import com.broadleafcommerce.data.tracking.core.policy.Policy;
import com.broadleafcommerce.data.tracking.core.type.OperationType;
import com.broadleafcommerce.translation.domain.Translation;
import com.broadleafcommerce.translation.service.TranslationEntityService;
import com.tutorial.domain.ElectricCar;
import com.tutorial.service.ElectricCarService;

import javax.servlet.http.HttpServletRequest;
import cz.jirutka.rsql.parser.ast.Node;

/**
 * Override an endpoint from {@link ProductEndpoint}. It is important that all the
 * {@link FrameworkMapping} (and friends) are replaced with {@link RequestMapping} annotations.
 * {@link FrameworkRestController} should also be replaced with {@link RestController}. Finally, any
 * {@link Policy} annotations should be repeated here.
 */
@RestController
@RequestMapping({"/products"})
public class ElectricCarEndpoint extends ProductEndpoint {

    public ElectricCarEndpoint(ProductService<Product> productSvc,
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

    /**
     * Using a different path to avoid having to override all super mappings at `/`. Note, in this
     * case, searching by the `model` property could have also been achieved using RSQL syntax and
     * {@link ProductEndpoint#readAllProducts(ContextInfo, String, boolean, Node, Pageable)} without
     * requiring an endpoint, service, or repository customization.
     */
    @RequestMapping(path = "/modeled", method = RequestMethod.GET)
    @Policy(permissionRoots = {"PRODUCT"})
    public Page<Projection<ElectricCar>> readByModel(HttpServletRequest request,
            @ContextOperation(OperationType.READ) ContextInfo context,
            @RequestParam(name = "model") String model) {
        return new PageImpl<>(
                ((ElectricCarService) getProductSvc()).readUsingModel(model, context));
    }

}

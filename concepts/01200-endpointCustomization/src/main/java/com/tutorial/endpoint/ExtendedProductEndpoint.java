package com.tutorial.endpoint;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.service.product.ProductService;
import com.broadleafcommerce.catalog.service.product.hydration.ProductHydrationService;
import com.broadleafcommerce.catalog.web.endpoint.ProductEndpoint;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.core.context.ContextOperation;
import com.broadleafcommerce.data.tracking.core.policy.Policy;
import com.broadleafcommerce.data.tracking.core.type.OperationType;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

/**
 * Override an endpoint from {@link ProductEndpoint}. It is important that all the
 * {@link FrameworkMapping} (and friends) are replaced with {@link RequestMapping} annotations.
 * {@link FrameworkRestController} should also be replaced with {@link RestController}. Finally, any
 * {@link Policy} annotations should be repeated here.
 */
@RestController
@RequestMapping({"/products"})
@RequiredArgsConstructor
public class ExtendedProductEndpoint {

    private final ProductService<Product> productSvc;
    private final ProductHydrationService productHydrationService;

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"})
    @Policy(permissionRoots = {"PRODUCT"})
    public Product createProduct(HttpServletRequest request,
            @ContextOperation(OperationType.CREATE) ContextInfo context,
            @RequestBody Product req) {
        /**
         * Note - {@link ContextInfoCustomizer} is another powerful way to manipulate ContextInfo
         * across multiple request types.
         */
        context.getAdditionalProperties().put("MyValue", "Test");
        Product created = productSvc.create(req, context);
        return productHydrationService.hydrate(created, context);
    }
}

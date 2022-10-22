package com.tutorial.endpoint;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.broadleafcommerce.catalog.service.product.hydration.ProductHydrationService;
import com.broadleafcommerce.catalog.web.endpoint.ProductEndpoint;
import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.core.context.ContextOperation;
import com.broadleafcommerce.data.tracking.core.policy.Policy;
import com.tutorial.domain.ElectricCar;
import com.tutorial.service.ElectricCarService;

import java.util.Objects;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;

/**
 * Override an endpoint from {@link ProductEndpoint}. It is important that all the
 * {@link FrameworkMapping} (and friends) are replaced with {@link RequestMapping} annotations.
 * {@link FrameworkRestController} should also be replaced with {@link RestController}. Finally, any
 * {@link Policy} annotations should be repeated here.
 * <p>
 * </p>
 * It is generally preferable to declare a new {@code @RestController} annotated class for your
 * endpoint that does not derive from the Broadleaf controller. This usually serves to simplify the
 * injection requirements for your needs. Your path mapping will still be preferred over the
 * original Broadleaf framework mappings.
 */
@RestController
@RequestMapping({"/products"})
@RequiredArgsConstructor
public class ElectricCarEndpoint {

    private final ElectricCarService productSvc;
    private final ProductHydrationService productHydrationService;

    /**
     * Note, in this case, searching by the `model` property could have also been achieved using
     * RSQL syntax and
     * {@link ProductEndpoint#readAllProducts(ContextInfo, String, boolean, Node, Pageable)} without
     * requiring an endpoint, service, or repository customization. See
     * {@code com.tutorial.ProductExtensionOnlyIT#testRSQLForExtendedProperty()}.
     */
    @GetMapping
    @Policy(permissionRoots = "PRODUCT")
    public Page<Projection<ElectricCar>> readAllProducts(@ContextOperation ContextInfo context,
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "hydratePrimaryAssets",
                    defaultValue = "true") boolean hydratePrimaryAssets,
            Node filters,
            @PageableDefault(size = 50) Pageable page) {
        Page<Projection<ElectricCar>> results;
        if (StringUtils.isNotEmpty(model)) {
            results = new PageImpl<>(productSvc.readUsingModel(model, context));
        } else {
            results = Projection.cast(productSvc.readAllByName(query, filters, page, context));
        }
        if (hydratePrimaryAssets) {
            return Projection.cast(productHydrationService
                    .hydratePrimaryAssets(Objects.requireNonNull(Projection.cast(results)),
                            context));
        } else {
            return results;
        }
    }

}

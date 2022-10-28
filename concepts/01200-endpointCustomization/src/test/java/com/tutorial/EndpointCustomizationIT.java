package com.tutorial;

import static com.broadleafcommerce.data.tracking.test.BaseSandboxIntegrationTest.X_CONTEXT_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.service.product.ProductService;
import com.broadleafcommerce.catalog.web.endpoint.ProductEndpoint;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;

import java.time.Instant;

/**
 * Confirm the extension of {@link ProductEndpoint} is registered with Spring and is effective. This
 * example focuses on basic customization of endpoint logic using out-of-the-box business,
 * repository, and domain tiers.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class EndpointCustomizationIT extends AbstractMockMvcIT {

    @SpyBean
    protected ProductService<Product> productService;

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM JpaProduct").executeUpdate();
    }

    @Test
    void testEndpointCustomization() throws Exception {
        getMockMvc().perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJsonExcludeNull(projection()))
                        .header(X_CONTEXT_REQUEST,
                                toJsonExcludeNull(testContextRequest(false, true)))
                        .with(getMockMvcUtil().withAuthorities(Sets.newSet("CREATE_PRODUCT"))))
                .andExpect(status().is2xxSuccessful());
        verify(productService, times(1)).create(any(),
                argThat(info -> info.getAdditionalProperties().containsKey("MyValue")));
    }

    private Product projection() {
        Product projection = new Product();
        projection.setName("test");
        projection.setSku("test");
        projection.setActiveStartDate(Instant.now());
        projection.setDefaultPrice(Money.of(12, "USD"));
        return projection;
    }

}

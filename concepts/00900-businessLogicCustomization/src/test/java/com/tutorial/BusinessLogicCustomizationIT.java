package com.tutorial;

import static com.broadleafcommerce.data.tracking.test.BaseSandboxIntegrationTest.X_CONTEXT_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.service.product.ProductService;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.service.MyIntegrationService;

import java.time.Instant;

/**
 * Confirm the extension of {@link ProductService} is registered with Spring and is effective. This
 * example focuses on basic customization of service business logic using out-of-the-box domain and
 * repository.
 */
@TestCatalogRouted
class BusinessLogicCustomizationIT extends AbstractMockMvcIT {

    @Autowired
    private MyIntegrationService integrationService;

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM JpaProduct").executeUpdate();
    }

    @Test
    void testBusinessLogicCustomization() throws Exception {
        getMockMvc().perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJsonExcludeNull(projection()))
                        .header(X_CONTEXT_REQUEST,
                                toJsonExcludeNull(testContextRequest(false, true)))
                        .with(getMockMvcUtil().withAuthorities(Sets.newSet("CREATE_PRODUCT"))))
                .andExpect(status().is2xxSuccessful());

        assertThat(integrationService.getRegistrationCount()).isEqualTo(1);
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

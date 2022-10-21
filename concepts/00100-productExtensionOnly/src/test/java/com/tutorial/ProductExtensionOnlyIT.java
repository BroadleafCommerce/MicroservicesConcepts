package com.tutorial;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.http.MediaType;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.ElectricCar;
import java.time.Instant;

import static com.broadleafcommerce.data.tracking.test.BaseSandboxIntegrationTest.X_CONTEXT_REQUEST;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Confirm the extended type is targeted by {@link JpaProductRepository}, and that the auto
 * generated projection is used in/out with the API call.
 */
@TestCatalogRouted
class ProductExtensionOnlyIT extends AbstractMockMvcIT {

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM ElectricCar").executeUpdate();
    }

    @Test
    void testProductExtensionOnly() throws Exception {
        getMockMvc().perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJsonExcludeNull(projection()))
                        .header(X_CONTEXT_REQUEST,
                                toJsonExcludeNull(testContextRequest(false, true)))
                        .with(getMockMvcUtil().withAuthorities(Sets.newSet("CREATE_PRODUCT"))))
                .andExpect(status().is2xxSuccessful());

        getMockMvc().perform(
                get("/products")
                        .header(X_CONTEXT_REQUEST,
                                toJsonExcludeNull(testContextRequest(false, true)))
                        .with(getMockMvcUtil().withAuthorities(Sets.newSet("READ_PRODUCT"))))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].model").value("test"));
    }

    private Projection<ElectricCar> projection() {
        Projection<ElectricCar> projection = Projection.get(ElectricCar.class);
        ElectricCar car = projection.expose();
        car.setName("test");
        car.setSku("test");
        car.setActiveStartDate(Instant.now());
        car.setModel("test");
        car.setDefaultPrice(Money.of(12, "USD"));
        return projection;
    }

}

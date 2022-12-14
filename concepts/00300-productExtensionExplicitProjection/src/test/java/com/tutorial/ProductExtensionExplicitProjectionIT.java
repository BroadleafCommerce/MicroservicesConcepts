package com.tutorial;

import static com.broadleafcommerce.data.tracking.test.BaseSandboxIntegrationTest.X_CONTEXT_REQUEST;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.http.MediaType;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.MyAutoCoProduct;
import com.tutorial.domain.MyAutoCoProductProjection;
import com.tutorial.metadata.ProductExtensionMetadata;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

/**
 * Confirm the complex extended type is targeted by {@link JpaProductRepository}, and that the auto
 * generated projection is used in/out with the API call. Also confirm the new aggregation field in
 * the explicit projection is populated correctly in the response.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class ProductExtensionExplicitProjectionIT extends AbstractMockMvcIT {

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM MyAutoCoProduct").executeUpdate();
    }

    @Test
    void testProductExtensionExplicitProjection() throws Exception {
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
                .andExpect(jsonPath("$.content[0].model").value("test"))
                .andExpect(jsonPath("$.content[0].corporateId").value("test"))
                .andExpect(jsonPath("$.content[0].efficiencyByTempFahrenheit",
                        hasKey(ProductExtensionMetadata.TemperatureOptionEnum.LOW.label())))
                .andExpect(jsonPath("$.content[0].allMaterials", hasSize(1)))
                .andExpect(jsonPath("$.content[0].allMaterials[0].name").value("Vegan Cover"))
                .andExpect(jsonPath("$.content[0].tags", hasSize(1)))
                .andExpect(jsonPath("$.content[0].tags[0]").value("test"));
    }

    private MyAutoCoProductProjection projection() {
        MyAutoCoProductProjection car = new MyAutoCoProductProjection();
        car.setTags(Collections.singletonList("test"));
        car.setName("test");
        car.setSku("test");
        car.setActiveStartDate(Instant.now());
        car.setModel("test");
        car.setDefaultPrice(Money.of(12, "USD"));
        MyAutoCoProduct.Efficiency efficiency = new MyAutoCoProduct.Efficiency();
        efficiency.setChargeTimeMinutes(8L * 60L);
        efficiency.setRangeMiles(new BigDecimal(300));
        car.setEfficiencyByTempFahrenheit(Collections.singletonMap(
                ProductExtensionMetadata.TemperatureOptionEnum.LOW.label(), efficiency));
        MyAutoCoProduct.Feature feature = new MyAutoCoProduct.Feature();
        feature.setName("Bucket Seat");
        feature.setDescription("Bucket Seat");
        MyAutoCoProduct.Material material = new MyAutoCoProduct.Material();
        material.setName("Vegan Cover");
        material.setDescription("Durable vegan material simulates suede");
        material.setSupplier("Dinamica");
        feature.getMaterials().add(material);
        car.setFeatures(Collections.singletonList(feature));
        car.setCorporateId("test");
        return car;
    }

}

package com.tutorial;

import static com.broadleafcommerce.data.tracking.test.BaseSandboxIntegrationTest.X_CONTEXT_REQUEST;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;

import com.broadleafcommerce.catalog.web.endpoint.ProductEndpoint;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.MyAutoCoProduct;
import com.tutorial.domain.MyAutoCoProductProjection;
import com.tutorial.metadata.ProductExtensionMetadata;
import com.tutorial.service.MyAutoCoProductService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

/**
 * Confirm the extension of {@link ProductEndpoint} is registered with Spring and is effective.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class EndpointCustomizationExplicitProjectionIT extends AbstractMockMvcIT {

    @SpyBean
    protected MyAutoCoProductService carService;

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM JpaProduct").executeUpdate();
    }

    @Test
    void testEndpointCustomizationExplicitProjection() throws Exception {
        getMockMvc().perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJsonExcludeNull(projection()))
                        .header(X_CONTEXT_REQUEST,
                                toJsonExcludeNull(testContextRequest(false, true)))
                        .with(getMockMvcUtil().withAuthorities(Sets.newSet("CREATE_PRODUCT"))))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.model").value("test"))
                .andExpect(jsonPath("$.efficiencyByTempFahrenheit",
                        hasKey(ProductExtensionMetadata.TemperatureOptionEnum.LOW.label())))
                .andExpect(jsonPath("$.allMaterials", hasSize(1)))
                .andExpect(jsonPath("$.allMaterials[0].name").value("Vegan Cover"))
                .andExpect(jsonPath("$.tags", hasSize(1)))
                .andExpect(jsonPath("$.tags[0]").value("test"));

        verify(carService, times(1)).create(any(),
                argThat(info -> info.getAdditionalProperties().containsKey("MyValue")));
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
        return car;
    }

}

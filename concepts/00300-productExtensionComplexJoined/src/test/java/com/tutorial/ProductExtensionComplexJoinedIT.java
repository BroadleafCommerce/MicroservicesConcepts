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
import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.ElectricCar;
import com.tutorial.domain.Upgrade;
import com.tutorial.metadata.ProductExtensionMetadata;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

/**
 * Confirm the complex extended type is targeted by {@link JpaProductRepository}, and that the auto
 * generated projection is used in/out with the API call. Also confirm the {@code @OneToMany}
 * collection is mapped and persisted correctly.
 */
@TestCatalogRouted
class ProductExtensionComplexJoinedIT extends AbstractMockMvcIT {

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM Upgrade").executeUpdate();
        getEntityManager().createQuery("DELETE FROM ElectricCar").executeUpdate();
    }

    @Test
    void testProductExtensionComplexJson() throws Exception {
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
                .andExpect(jsonPath("$.content[0].efficiencyByTempFahrenheit",
                        hasKey(ProductExtensionMetadata.TemperatureOptionEnum.LOW.label())))
                .andExpect(jsonPath("$.content[0].upgrades", hasSize(1)))
                .andExpect(jsonPath("$.content[0].upgrades[0].name").value("upgrade"));
    }

    private Projection<ElectricCar> projection() {
        Projection<ElectricCar> projection = Projection.get(ElectricCar.class);
        ElectricCar car = projection.expose();
        car.setName("test");
        car.setSku("test");
        car.setActiveStartDate(Instant.now());
        car.setModel("test");
        car.setDefaultPrice(Money.of(12, "USD"));
        ElectricCar.Efficiency efficiency = new ElectricCar.Efficiency();
        efficiency.setChargeTimeMinutes(8L * 60L);
        efficiency.setRangeMiles(new BigDecimal(300));
        car.setEfficiencyByTempFahrenheit(Collections.singletonMap(
                ProductExtensionMetadata.TemperatureOptionEnum.LOW.label(), efficiency));
        Upgrade upgrade = new Upgrade();
        upgrade.setName("upgrade");
        upgrade.setDescription("upgrade description");
        upgrade.setManufacturerId("upgrade manufacturer id");
        car.setUpgrades(Collections.singletonList(upgrade));
        return projection;
    }

}

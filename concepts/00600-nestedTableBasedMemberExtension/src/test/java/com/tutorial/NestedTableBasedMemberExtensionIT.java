package com.tutorial;

import static com.broadleafcommerce.data.tracking.test.BaseSandboxIntegrationTest.X_CONTEXT_REQUEST;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.http.MediaType;

import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.ExtendedUpgrade;
import com.tutorial.domain.MyAutoCoProductProjection;

import java.time.Instant;
import java.util.Collections;

/**
 * Confirm the extended type for Upgrade (table based collection) is recognized and persisted during
 * service input/output. Backing domain extension uses auto projection.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class NestedTableBasedMemberExtensionIT extends AbstractMockMvcIT {

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM ExtendedUpgrade").executeUpdate();
        getEntityManager().createQuery("DELETE FROM MyAutoCoProduct").executeUpdate();
    }

    @Test
    void testNestedTableBasedMemberExtension() throws Exception {
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
                .andExpect(jsonPath("$.content[0].upgrades[0].corporateId").value("test"));
    }

    private MyAutoCoProductProjection projection() {
        MyAutoCoProductProjection car = new MyAutoCoProductProjection();
        car.setTags(Collections.singletonList("test"));
        car.setName("test");
        car.setSku("test");
        car.setActiveStartDate(Instant.now());
        car.setModel("test");
        car.setDefaultPrice(Money.of(12, "USD"));
        ExtendedUpgrade upgrade = new ExtendedUpgrade();
        upgrade.setName("upgrade");
        upgrade.setDescription("upgrade description");
        upgrade.setManufacturerId("upgrade manufacturer id");
        upgrade.setCorporateId("test");
        car.setUpgrades(Collections.singletonList(upgrade));
        return car;
    }

}

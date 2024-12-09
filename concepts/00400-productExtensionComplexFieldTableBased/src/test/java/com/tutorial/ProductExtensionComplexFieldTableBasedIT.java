package com.tutorial;

import static com.broadleafcommerce.data.tracking.test.BaseSandboxIntegrationTest.X_CONTEXT_REQUEST;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.broadleafcommerce.data.tracking.jpa.tenant.domain.catalog.JpaCatalog;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.Characteristics;
import com.tutorial.domain.MyAutoCoProductProjection;
import com.tutorial.domain.Upgrade;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

/**
 * Confirm the complex extended type is targeted by {@link JpaProductRepository}, and that the auto
 * generated projection is used in/out with the API call. Also confirm the {@code @OneToMany}
 * collection is mapped and persisted correctly.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class ProductExtensionComplexFieldTableBasedIT extends AbstractMockMvcIT {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM Upgrade").executeUpdate();
        getEntityManager().createQuery("DELETE FROM Characteristics").executeUpdate();
        getEntityManager().createQuery("DELETE FROM MyAutoCoProduct").executeUpdate();
    }

    @Test
    void testProductExtensionComplexFieldTableBased() throws Exception {
        getTemplate().executeWithoutResult(status -> {
            JpaCatalog catalog = new JpaCatalog();
            catalog.setName("test");
            catalog.setContextId("1");
            catalog.setLevel(1L);
            getEntityManager().merge(catalog);
        });

        getMockMvc().perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJsonExcludeNull(projection()))
                        .header(X_CONTEXT_REQUEST,
                                toJsonExcludeNull(testContextRequest(false, true)))
                        .with(getMockMvcUtil().withAuthorities(Sets.newSet("CREATE_PRODUCT"))))
                .andExpect(status().is2xxSuccessful());

        String json = getMockMvc().perform(
                get("/products")
                        .header(X_CONTEXT_REQUEST,
                                toJsonExcludeNull(testContextRequest(false, true)))
                        .with(getMockMvcUtil().withAuthorities(Sets.newSet("READ_PRODUCT"))))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].model").value("test"))
                .andExpect(jsonPath("$.content[0].upgrades", hasSize(1)))
                .andExpect(jsonPath("$.content[0].upgrades[0].name").value("upgrade"))
                .andExpect(jsonPath("$.content[0].upgrades[0].description").value("upgrade description"))
                .andExpect(jsonPath("$.content[0].tags", hasSize(1)))
                .andExpect(jsonPath("$.content[0].tags[0]").value("test"))
                .andExpect(jsonPath("$.content[0].altCharacteristics.model").value("test"))
                .andExpect(jsonPath("$.content[0].altCharacteristics.weight").value("4000.0"))
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        String product = node.get("content").elements().next().toString();
        MyAutoCoProductProjection projection = objectMapper.readValue(product, MyAutoCoProductProjection.class);
        projection.getUpgrades().get(0).setDescription("updated description");

        getMockMvc().perform(
                        patch("/products/" + projection.getId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(toJsonExcludeNull(projection))
                                .header(X_CONTEXT_REQUEST,
                                        toJsonExcludeNull(testContextRequest(false, true)))
                                .with(getMockMvcUtil().withAuthorities(Sets.newSet("UPDATE_PRODUCT"))))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.upgrades[0].description").value("updated description"));
    }

    private MyAutoCoProductProjection projection() {
        MyAutoCoProductProjection car = new MyAutoCoProductProjection();
        car.setTags(Collections.singletonList("test"));
        car.setName("test");
        car.setSku("test");
        car.setActiveStartDate(Instant.now());
        car.setModel("test");
        car.setDefaultPrice(Money.of(12, "USD"));
        Upgrade upgrade = new Upgrade();
        upgrade.setName("upgrade");
        upgrade.setDescription("upgrade description");
        upgrade.setManufacturerId("upgrade manufacturer id");
        car.setUpgrades(Collections.singletonList(upgrade));
        Characteristics characteristics = new Characteristics();
        characteristics.setLength(BigDecimal.valueOf(14.4D));
        characteristics.setWidth(BigDecimal.valueOf(5.8D));
        characteristics.setHeight(BigDecimal.valueOf(5.8D));
        characteristics.setWeight(BigDecimal.valueOf(4000));
        car.setAltCharacteristics(characteristics);
        return car;
    }

}

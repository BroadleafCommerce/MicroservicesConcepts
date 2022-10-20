package com.tutorial;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.common.jpa.autoconfigure.AutoConfigureTestDb;
import com.broadleafcommerce.data.tracking.core.type.TrackingLevel;
import com.broadleafcommerce.data.tracking.jpa.filtering.domain.CatalogJpaTracking;
import com.broadleafcommerce.oauth2.resource.security.test.MockMvcOAuth2AuthenticationUtil;
import com.tutorial.domain.ElectricCar;
import com.tutorial.metadata.ProductExtensionMetadata;

import java.math.BigDecimal;
import java.util.Collections;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.azam.ulidj.ULID;

/**
 * Confirm the complex extended type is targeted by {@link JpaProductRepository}, and that the auto
 * generated projection is serialized out through the API call.
 */
@SpringBootTest
@AutoConfigureTestDb
@AutoConfigureMockMvc
@TestPropertySource(properties = "broadleaf.default.data.route=catalog")
class ProductExtensionComplexJsonIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected MockMvcOAuth2AuthenticationUtil mockMvcUtil;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TransactionTemplate template;

    @BeforeEach
    private void init() {
        ElectricCar car = new ElectricCar();
        car.setContextId(ULID.random());
        car.setModel("test");
        ElectricCar.Efficiency efficiency = new ElectricCar.Efficiency();
        efficiency.setChargeTimeMinutes(8L * 60L);
        efficiency.setRangeMiles(new BigDecimal(300));
        car.setEfficiencyByTempFahrenheit(Collections.singletonMap(
                ProductExtensionMetadata.TemperatureOptionEnum.LOW.label(), efficiency));
        CatalogJpaTracking tracking = new CatalogJpaTracking();
        tracking.setLevel(TrackingLevel.PRODUCTION.getLevel());
        car.setTracking(tracking);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                em.persist(car);
            }
        });
    }

    @AfterEach
    private void tearDown() {
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                em.createQuery("DELETE FROM ElectricCar").executeUpdate();
            }
        });
    }

    @Test
    void testDomainExtension() throws Exception {
        mockMvc.perform(
                get("/products")
                        .with(mockMvcUtil.withAuthorities(Sets.newSet("READ_PRODUCT"))))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].model").value("test"))
                .andExpect(jsonPath("$.content[0].efficiencyByTempFahrenheit",
                        hasKey(ProductExtensionMetadata.TemperatureOptionEnum.LOW.label())));
    }

}

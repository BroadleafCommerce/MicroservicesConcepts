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
import com.tutorial.domain.ChargingStationProjection;
import com.tutorial.domain.PricingProjection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Confirm the mapping to/from explicit projection is effective. We should be able to exercise the
 * new wattageByConnector map, as well as see the MonetaryAmount correctly displayed for pricing.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class NewDomainExplicitProjectionIT extends AbstractMockMvcIT {

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM Charger").executeUpdate();
        getEntityManager().createQuery("DELETE FROM ChargingStation").executeUpdate();
    }

    @Test
    void testNewDomainExplicitProjection() throws Exception {
        getMockMvc().perform(
                post("/chargingStations") // review EnableJpaTrackableFlow.rootPath for automatic
                                          // naming behavior
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJsonExcludeNull(projection()))
                        .header(X_CONTEXT_REQUEST,
                                toJsonExcludeNull(testContextRequest(false, false)))
                        .with(getMockMvcUtil()
                                .withAuthorities(
                                        Sets.newSet(
                                                "CREATE_CHARGINGSTATION")))) // review
                                                                             // EnableJpaTrackableFlow.permissionRoots
                                                                             // for automatic
                                                                             // naming behavior
                .andExpect(status().is2xxSuccessful());

        getMockMvc().perform(
                get("/chargingStations") // review EnableJpaTrackableFlow.rootPath for automatic
                                         // naming behavior
                        .header(X_CONTEXT_REQUEST,
                                toJsonExcludeNull(testContextRequest(false, false)))
                        .with(getMockMvcUtil()
                                .withAuthorities(
                                        Sets.newSet("READ_CHARGINGSTATION")))) // review
                                                                               // EnableJpaTrackableFlow.permissionRoots
                                                                               // for automatic
                                                                               // naming behavior
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(
                        jsonPath("$.content[0].wattageByConnectorType['CHAdeMO'][0]")
                                .value("150"))
                .andExpect(
                        jsonPath("$.content[0].pricing[0].maxPower")
                                .value("90"))
                .andExpect(
                        jsonPath("$.content[0].pricing[1].pricePerMinute.amount")
                                .value("0.32"))
                .andExpect(
                        jsonPath("$.content[0].pricing[1].pricePerMinute.currency")
                                .value("USD"));
    }

    private ChargingStationProjection projection() {
        ChargingStationProjection projection = new ChargingStationProjection();
        projection.setState("TX");
        projection.setCity("Dallas");
        projection.setZipcode("75248");
        projection.setAddress1("123 Test Road");
        {
            projection.getWattageByConnectorType().put("CHAdeMO",
                    new ArrayList<>(Collections.singletonList(150)));
            projection.getWattageByConnectorType().put("CCS",
                    new ArrayList<>(Arrays.asList(50, 350)));
        }
        {
            PricingProjection pricing = new PricingProjection();
            pricing.setMaxPower(90);
            pricing.setPricePerMinute(Money.of(new BigDecimal("0.16"), "USD"));
            projection.getPricing().add(pricing);
        }
        {
            PricingProjection pricing = new PricingProjection();
            pricing.setMaxPower(350);
            pricing.setPricePerMinute(Money.of(new BigDecimal("0.32"), "USD"));
            projection.getPricing().add(pricing);
        }
        return projection;
    }
}

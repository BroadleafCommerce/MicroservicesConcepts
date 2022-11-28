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

import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.Charger;
import com.tutorial.domain.ChargingStation;
import com.tutorial.domain.Pricing;

import java.math.BigDecimal;

/**
 * Confirm the introduction of the new domain is effective and available via API call.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class NewDomainComplexFieldIT extends AbstractMockMvcIT {

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM Charger").executeUpdate();
        getEntityManager().createQuery("DELETE FROM ChargingStation").executeUpdate();
    }

    @Test
    void testNewDomainComplexField() throws Exception {
        getMockMvc().perform(
                post("/charging-stations") // review EnableJpaTrackableFlow.rootPath for automatic
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
                get("/charging-stations") // review EnableJpaTrackableFlow.rootPath for automatic
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
                .andExpect(jsonPath("$.content[0].chargers[0].watts").value("150"))
                .andExpect(jsonPath("$.content[0].chargers[1].connectorType").value("CCS"))
                .andExpect(jsonPath("$.content[0].pricing[0].maxPower").value("90"))
                .andExpect(jsonPath("$.content[0].pricing[1].pricePerMinute.amount").value("0.32"))
                .andExpect(
                        jsonPath("$.content[0].pricing[1].pricePerMinute.currency").value("USD"));
    }

    private Projection<ChargingStation> projection() {
        Projection<ChargingStation> projection = Projection.get(ChargingStation.class);
        ChargingStation asChargingStation = projection.expose();
        asChargingStation.setState("TX");
        asChargingStation.setCity("Dallas");
        asChargingStation.setZipcode("75248");
        asChargingStation.setAddress1("123 Test Road");
        {
            Charger charger = new Charger();
            charger.setWatts(150);
            charger.setConnectorType("CHAdeMO");
            asChargingStation.getChargers().add(charger);
        }
        {
            Charger charger = new Charger();
            charger.setWatts(50);
            charger.setConnectorType("CCS");
            asChargingStation.getChargers().add(charger);
        }
        {
            Charger charger = new Charger();
            charger.setWatts(350);
            charger.setConnectorType("CCS");
            asChargingStation.getChargers().add(charger);
        }
        {
            Pricing pricing = new Pricing();
            pricing.setMaxPower(90);
            pricing.setPricePerMinute(Money.of(new BigDecimal("0.16"), "USD"));
            asChargingStation.getPricing().add(pricing);
        }
        {
            Pricing pricing = new Pricing();
            pricing.setMaxPower(350);
            pricing.setPricePerMinute(Money.of(new BigDecimal("0.32"), "USD"));
            asChargingStation.getPricing().add(pricing);
        }
        return projection;
    }
}

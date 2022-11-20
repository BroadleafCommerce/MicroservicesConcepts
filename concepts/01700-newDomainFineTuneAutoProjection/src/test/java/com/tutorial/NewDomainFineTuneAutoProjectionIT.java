package com.tutorial;

import static com.broadleafcommerce.data.tracking.test.BaseSandboxIntegrationTest.X_CONTEXT_REQUEST;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.http.MediaType;

import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.data.tracking.core.mapping.ExplicitProjectionFieldConfiguration;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.Charger;
import com.tutorial.domain.ChargingStation;
import com.tutorial.domain.Pricing;

import java.math.BigDecimal;

/**
 * Confirm the fine tuning of the projection via {@link ExplicitProjectionFieldConfiguration} is
 * handled appropriately. In this case, we should see:
 * <ol>
 * <li>Removal of a field via {@link ExplicitProjectionFieldConfiguration#ignore()}</li>
 * <li>Ignoring a field in a request if it is
 * {@link ExplicitProjectionFieldConfiguration#responseOnly()}</li>
 * <li>Custom deserialization/serialization (e.g. to/from MonetaryAmount for BigDecimal price field)
 * when using {@link ExplicitProjectionFieldConfiguration#usingDeserializer()} and
 * {@link ExplicitProjectionFieldConfiguration#usingSerializer()}</li>
 * </ol>
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class NewDomainFineTuneAutoProjectionIT extends AbstractMockMvcIT {

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM Charger").executeUpdate();
        getEntityManager().createQuery("DELETE FROM ChargingStation").executeUpdate();
    }

    @Test
    void testNewDomainFineTuneAutoProjection() throws Exception {
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
                patch("/charging-stations/one")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJsonExcludeNull(mutate()))
                        .header(X_CONTEXT_REQUEST,
                                toJsonExcludeNull(testContextRequest(false, false)))
                        .with(getMockMvcUtil()
                                .withAuthorities(
                                        Sets.newSet(
                                                "UPDATE_CHARGINGSTATION"))))
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
                .andExpect(jsonPath("$.content[0].city")
                        .value("Dallas")) // city was not previously mutated because of
                                          // responseOnly
                .andExpect(jsonPath("$.content[0].address2").doesNotExist())
                .andExpect(jsonPath("$.content[0].chargers[0].watts").value("150"))
                .andExpect(jsonPath("$.content[0].chargers[1].connectorType").value("CCS"))
                .andExpect(jsonPath("$.content[0].pricing[0].maxPower").value("90"))
                .andExpect(jsonPath("$.content[0].pricing[1].pricePerMinute.amount").value("0.32"))
                .andExpect(
                        jsonPath("$.content[0].pricing[1].pricePerMinute.currency").value("USD"));
    }

    private Projection<ChargingStation> mutate() {
        Projection<ChargingStation> projection = Projection.get(ChargingStation.class);
        ChargingStation asChargingStation = projection.expose();
        asChargingStation.setCity("Denver");
        return projection;
    }

    private Projection<ChargingStation> projection() {
        Projection<ChargingStation> projection = Projection.get(ChargingStation.class);
        projection.setId("one");
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

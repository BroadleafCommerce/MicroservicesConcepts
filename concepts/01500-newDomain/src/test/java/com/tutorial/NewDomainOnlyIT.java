package com.tutorial;

import static com.broadleafcommerce.data.tracking.test.BaseSandboxIntegrationTest.X_CONTEXT_REQUEST;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.http.MediaType;

import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.ChargingStation;

/**
 * Confirm the introduction of the new domain is effective and available via API call.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class NewDomainOnlyIT extends AbstractMockMvcIT {

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM ChargingStation").executeUpdate();
    }

    @Test
    void testNewDomainOnly() throws Exception {
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
                .andExpect(jsonPath("$.content[0].state").value("TX"))
                .andExpect(jsonPath("$.content[0].city").value("Dallas"))
                .andExpect(jsonPath("$.content[0].address1").value("123 Test Road"))
                .andExpect(jsonPath("$.content[0].zipcode").value("75248"));
    }

    private Projection<ChargingStation> projection() {
        Projection<ChargingStation> projection = Projection.get(ChargingStation.class);
        ChargingStation asChargingStation = projection.expose();
        asChargingStation.setState("TX");
        asChargingStation.setCity("Dallas");
        asChargingStation.setZipcode("75248");
        asChargingStation.setAddress1("123 Test Road");
        return projection;
    }
}

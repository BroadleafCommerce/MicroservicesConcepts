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

import com.broadleafcommerce.catalog.domain.product.option.AttributeChoice;
import com.broadleafcommerce.catalog.domain.product.option.type.DefaultAttributeChoiceType;
import com.broadleafcommerce.catalog.domain.product.option.type.DefaultProductOptionType;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.ElectricCarProductOption;
import com.tutorial.domain.ExtendedFeature;
import com.tutorial.projection.ElectricCarProjection;

import java.time.Instant;
import java.util.Collections;

/**
 * Confirm the extended type for JpaProductOption (embedded collection) is recognized and persisted
 * during service input/output. Backing domain extension uses auto projection.
 */
@TestCatalogRouted
class EmbeddedCollectionMemberExtensionIT extends AbstractMockMvcIT {

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM ElectricCar").executeUpdate();
    }

    @Test
    void testOptionEmbeddedCollectionMemberExtension() throws Exception {
        getMockMvc().perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJsonExcludeNull(projection(true)))
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
                .andExpect(jsonPath("$.content[0].options[0].isDealerOption").value("true"));
    }

    @Test
    void testFeatureEmbeddedCollectionMemberExtension() throws Exception {
        getMockMvc().perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(toJsonExcludeNull(projection(false)))
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
                .andExpect(jsonPath("$.content[0].features[0].corporateId").value("test"));
    }

    private ElectricCarProjection projection(boolean testOption) {
        ElectricCarProjection projection = new ElectricCarProjection();
        projection.setTags(Collections.singletonList("test"));
        projection.setName("test");
        projection.setSku("test");
        projection.setActiveStartDate(Instant.now());
        projection.setDefaultPrice(Money.of(12, "USD"));
        projection.setModel("test");
        if (testOption) {
            ElectricCarProductOption option = new ElectricCarProductOption();
            option.setLabel("monogram");
            option.setType(DefaultProductOptionType.CART_ITEM_ATTRIBUTE.name());
            option.setAttributeChoice(
                    new AttributeChoice("monogram", DefaultAttributeChoiceType.TEXT.name()));
            option.setIsDealerOption(true);
            projection.getOptions().add(option);
        } else {
            ExtendedFeature feature = new ExtendedFeature();
            feature.setName("test");
            feature.setCorporateId("test");
            projection.setFeatures(Collections.singletonList(feature));
        }
        return projection;
    }

}

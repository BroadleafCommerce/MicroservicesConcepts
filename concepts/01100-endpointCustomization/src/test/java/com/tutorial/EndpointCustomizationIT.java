package com.tutorial;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.service.product.ProductService;
import com.broadleafcommerce.catalog.web.endpoint.ProductEndpoint;
import com.broadleafcommerce.microservices.AbstractMockMvcIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;

/**
 * Confirm the extension of {@link ProductEndpoint} is registered with Spring and is effective. This
 * example focuses on basic customization of endpoint logic using out-of-the-box business,
 * repository, and domain tiers.
 */
@TestCatalogRouted
class EndpointCustomizationIT extends AbstractMockMvcIT {

    @SpyBean
    protected ProductService<Product> productService;

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM JpaProduct").executeUpdate();
    }

    @Test
    void testEndpointCustomization() throws Exception {
        // TODO Finish this test
//        mockMvc.perform(
//                        post("/products")
//                                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                                .content(toJsonExcludeNull(navMenuItemToCreate))
//                                .with(mockMvcUtil.withAuthorities(Sets.newSet("CREATE_PRODUCT"))))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(jsonPath("$.content", hasSize(1)))
//                .andExpect(jsonPath("$.content[0].model").value("test"));
    }

}

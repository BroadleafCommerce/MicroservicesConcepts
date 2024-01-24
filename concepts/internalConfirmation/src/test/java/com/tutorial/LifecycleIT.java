package com.tutorial;

import static com.broadleafcommerce.data.tracking.test.BaseSandboxIntegrationTest.X_CONTEXT_REQUEST;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.common.jpa.schema.SchemaCompatibiltyUtility;
import com.broadleafcommerce.data.tracking.core.context.ContextInfoCustomizer;
import com.broadleafcommerce.data.tracking.core.context.ContextRequest;
import com.broadleafcommerce.data.tracking.core.tenant.domain.Application;
import com.broadleafcommerce.data.tracking.core.tenant.domain.ApplicationCatalogRef;
import com.broadleafcommerce.data.tracking.core.tenant.domain.Catalog;
import com.broadleafcommerce.data.tracking.core.tenant.domain.InheritanceLine;
import com.broadleafcommerce.data.tracking.core.tenant.domain.InheritanceLines;
import com.broadleafcommerce.data.tracking.core.tenant.domain.InheritanceMember;
import com.broadleafcommerce.data.tracking.core.type.CatalogStatus;
import com.broadleafcommerce.oauth2.resource.security.test.MockMvcOAuth2AuthenticationUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.domain.MyAutoCoProduct;

import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Primarily an internal stability test to confirm startup and basic operation with postgres,
 * generated liquibase schema, and modelmapper cache. This should help with confirming the stability
 * of most of the aspects in CI. The test execution via maven will first create the
 * modelmapper cache, then will startup the app with liquibase support for the extension against
 * Postgres, inject the modelmapper cache, and exercise the extension via the API.
 */
public class LifecycleIT {

    @Nested
    public class Startup extends StartupTest {}

    @ContextConfiguration(initializers = StartupTest.Initializer.class)
    @SpringBootTest(
            properties = {
                    "net.bytebuddy.dump=",
                    "broadleaf.modelmapper.cache.create.enabled=false",
                    "broadleaf.changesummary.notification.active=false",
                    "broadleaf.sandbox.preview.decoder.encoded-public-key=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAixGIyu/0rZCdNGJLdRWXV3FYx/X/va24BQqDZn2JHeDx7KrDNWFNs0SjaBJrXoDF7x205COqpVCpon8nawzhJ2WVePpodnFA0lQOIUdU7NlkLYz7rm36BQnkso2UG1aQbUKupaOot/5IN8Ax81GIqaU0LIoDiKOcJ3ylbitMkKOu/lIc+mxhD1U8CcwmXq+MZSAnTt0M/kUna6s9XmwkJgcS5lVIHX0CIdAyHDGF0ybz/zhcLsvSMZ71jJ5qakY62Tey2UMncVRinjppTZD01ApPvXmz3atp1ONFCqijua8+tc3n5m815CSZjDNm0jjtCdN7y6cFYQod3THSC1nmyQIDAQAB",
                    "broadleaf.catalog.liquibase.liquibase-schema=public",
                    "broadleaf.catalog.liquibase.change-log=classpath:/db/changelog/catalog.flex.postgresql.changelog-master.yaml",
                    "spring.liquibase.enabled=true"
            })
    @Slf4j
    public static class StartupTest extends AbstractMockMvcIT {

        public static PostgreSQLContainer container =
                new PostgreSQLContainer("postgres:11.2");

        public static class Initializer
                implements ApplicationContextInitializer<ConfigurableApplicationContext> {

            public void initialize(
                    @NonNull ConfigurableApplicationContext configurableApplicationContext) {
                TestPropertyValues.of(getDataRoutingSchemaProperties())
                        .applyTo(configurableApplicationContext.getEnvironment());

            }

            private List<String> getDataRoutingSchemaProperties() {
                List<String> props = new ArrayList<>();
                props.add("broadleaf.composite.datasource.url="
                        + System.getProperty("compat_jdbcurl"));
                props.add("broadleaf.composite.datasource.username="
                        + System.getProperty("compat_username"));
                props.add("broadleaf.composite.datasource.password="
                        + System.getProperty("compat_password"));
                log.info(Arrays.toString(props.toArray()));
                return props;
            }

        }

        @BeforeAll
        public static void startup() {
            container.start();
            SchemaCompatibiltyUtility.setup(container.getJdbcUrl(),
                    container.getUsername(), container.getPassword(), container.getDatabaseName());
        }


        @Autowired
        private ApplicationContext appctx;

        @Test
        public void applicationContextLoads() {
            assertNotNull(appctx);
        }

        @Test
        void testProductExtensionOnly() throws Exception {
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
                    .andExpect(jsonPath("$.content[0].tags", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].tags[0]").value("test"));
        }

        private Projection<MyAutoCoProduct> projection() {
            Projection<MyAutoCoProduct> projection = Projection.get(MyAutoCoProduct.class);
            Product asProduct = (Product) projection;
            asProduct.setTags(Collections.singletonList("test"));
            asProduct.setName("test");
            asProduct.setSku("test");
            asProduct.setActiveStartDate(Instant.now());
            asProduct.setDefaultPrice(Money.of(12, "USD"));
            MyAutoCoProduct car = projection.expose();
            car.setModel("test");
            return projection;
        }
    }


    @AutoConfigureMockMvc
    public static abstract class AbstractMockMvcIT extends AbstractStandardIT {

        @Autowired
        @Getter(AccessLevel.PROTECTED)
        private MockMvc mockMvc;

        @Autowired
        @Getter(AccessLevel.PROTECTED)
        private MockMvcOAuth2AuthenticationUtil mockMvcUtil;

    }


    @Import(AbstractStandardIT.ContextSetup.class)
    public static abstract class AbstractStandardIT {

        @TestConfiguration
        static class ContextSetup {

            /**
             * Avoid loading real catalogs in the DB
             */
            @Bean
            public ContextInfoCustomizer testContextCustomizer() {
                return (contextInfo, routeKey, endpoint, webRequest) -> {
                    Application application = contextInfo.getContextRequest().getApplication();
                    String applicationId = contextInfo.getContextRequest().getApplicationId();
                    String tenantId = contextInfo.getContextRequest().getTenantId();
                    if (application == null && applicationId != null) {
                        application = new Application(applicationId);
                        application.setTenantId(tenantId);
                        contextInfo.getContextRequest().setApplication(application);
                    }
                    Catalog catalog = contextInfo.getContextRequest().getCatalog();
                    String catalogId = contextInfo.getContextRequest().getCatalogId();
                    if (catalog == null && catalogId != null) {
                        catalog = new Catalog();
                        catalog.setId(catalogId);
                        catalog.setLocale(Locale.ENGLISH);
                        catalog.setLevel(1L);
                        contextInfo.getContextRequest().setCatalog(catalog);
                        if (application != null) {
                            catalog.setOwningApplication(applicationId);
                            application.setInheritanceLines(
                                    new InheritanceLines(Collections.singletonList(
                                            new InheritanceLine(Collections.singletonList(
                                                    new InheritanceMember("-1", null, 1))))));
                            ApplicationCatalogRef applicationCatalogRef =
                                    new ApplicationCatalogRef();
                            applicationCatalogRef.setId(catalogId);
                            applicationCatalogRef.setCatalogStatus(CatalogStatus.ONLINE.name());
                            application.getIsolatedCatalogs().add(applicationCatalogRef);
                        }
                    }
                };
            }

        }

        @Autowired
        @Getter(AccessLevel.PROTECTED)
        private ObjectMapper objectMapper;

        protected String toJsonExcludeNull(Object o) {
            try {
                return objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                        .writeValueAsString(o);
            } catch (JsonProcessingException e) {
                throw new UncheckedIOException(e);
            }
        }

        protected ContextRequest testContextRequest(boolean withApplication, boolean withCatalog) {
            ContextRequest response = new ContextRequest()
                    .withSandBoxId("1")
                    .withSandBoxName("test")
                    .withTenantId("tenant");
            if (withApplication) {
                response.setApplicationId("1");
            }
            if (withCatalog) {
                response.setCatalogId("1");
            }
            return response;
        }
    }

}

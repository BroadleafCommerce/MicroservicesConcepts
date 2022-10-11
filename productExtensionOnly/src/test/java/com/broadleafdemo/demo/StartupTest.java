package com.broadleafdemo.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import com.broadleafcommerce.common.jpa.schema.SchemaCompatibiltyUtility;
import com.broadleafcommerce.data.tracking.core.mapping.cache.ModelMapperCacheGenerationConfiguration;
import com.broadleafcommerce.data.tracking.core.mapping.cache.ModelMapperSerializationHelper;
import com.broadleafcommerce.oauth2.resource.security.test.MockMvcOAuth2AuthenticationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.extern.apachecommons.CommonsLog;

@AutoConfigureMockMvc
@ContextConfiguration(initializers = StartupTest.Initializer.class)
@CommonsLog
@SpringBootTest(properties = {
        "broadleaf.solr.startup-validation=false",
        "broadleaf.modelmapper.cache.create.enabled=true",
        "broadleaf.bulk.create-sandbox-request.notification.active=false",
        "broadleaf.bulk.delete-sandbox-request.notification.active=false"
})
@Import(ModelMapperCacheGenerationConfiguration.class)
public class StartupTest {

    public static PostgreSQLContainer container =
            new PostgreSQLContainer("postgres:11.2");

    @BeforeAll
    public static void startup() {
        container.start();
        SchemaCompatibiltyUtility.setup(container.getJdbcUrl(),
                container.getUsername(), container.getPassword(), container.getDatabaseName());
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
        @Bean
        public SolrClient solrClient() {
            List<String> zkHosts = Collections.singletonList("localhost:2181");
            Optional<String> zkChroot = Optional.of("/solr");
            return new CloudSolrClient.Builder(zkHosts, zkChroot).build();
        }
    }

    @Autowired
    ApplicationContext appctx;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MockMvcOAuth2AuthenticationUtil mockMvcUtil;

    @Autowired
    ModelMapperSerializationHelper helper;

    @Test
    public void applicationContextLoads() {
        assertNotNull(appctx);
    }

    @Test
    public void buildModelMapperCache() {
        helper.generateCacheResources();
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(
                @NonNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(getDataRoutingSchemaProperties())
                    .applyTo(configurableApplicationContext.getEnvironment());

        }

        private List<String> getDataRoutingSchemaProperties() {
            List<String> props = new ArrayList<>();
            props.add("broadleaf.composite.datasource.url=" + System.getProperty("compat_jdbcurl"));
            props.add("broadleaf.composite.datasource.username="
                    + System.getProperty("compat_username"));
            props.add("broadleaf.composite.datasource.password="
                    + System.getProperty("compat_password"));
            log.info(Arrays.toString(props.toArray()));
            return props;
        }

    }

}

package com.broadleafcommerce.microservices.schema;

import org.springframework.test.context.TestPropertySource;

import com.broadleafcommerce.common.jpa.schema.SchemaCompatibiltyUtility;

@TestPropertySource(properties = {
        "spring.liquibase.enabled=false",
        "spring.cache.type=none",
        "logging.level.root=WARN",
        "schema.utils.ignore.spotless=true",
        "schema.utils.ignore.docs=true",
        "schema.utils.inherit.scan=true",
        "spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults=false"
})
public class PostgresUtility extends SchemaCompatibiltyUtility.PostgresUtilityProvider {}

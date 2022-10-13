package com.broadleafcommerce.microservices.schema;

import org.springframework.test.context.TestPropertySource;

import com.broadleafcommerce.common.jpa.schema.SchemaCompatibiltyUtility;

@TestPropertySource(properties = {
        "service.key=catalog",
        "client.prefix=tutorial",
        "spring.liquibase.enabled=false",
        "spring.cache.type=none",
        "logging.level.root=WARN",
        "schema.utils.ignore.spotless=true",
        "schema.utils.ignore.docs=true",
        "schema.utils.inherit.scan=true",
        "broadleaf.project.relative.build.depth=5",
        "broadleaf.project.root=pom.xml",
        "spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults=false"
})
public class PostgresUtility extends SchemaCompatibiltyUtility.PostgresUtilityProvider {}

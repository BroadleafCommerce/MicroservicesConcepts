package com.tutorial;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

import com.broadleafcommerce.common.extension.data.DataRoutes;
import com.broadleafcommerce.common.jpa.autoconfigure.CommonJpaAutoConfiguration;
import com.broadleafcommerce.data.tracking.jpa.filtering.auto.EnableJpaTrackableFlow;
import com.tutorial.domain.ChargingStation;

/**
 * Setup components for the customization case.
 */
@Configuration
@AutoConfigureBefore(CommonJpaAutoConfiguration.class)
@EnableJpaTrackableFlow(
        entityClass = ChargingStation.class,
        routeKey = DataRoutes.CATALOG) // Auto establishes all plumbing for this domain
public class NewDomainComplexField {}

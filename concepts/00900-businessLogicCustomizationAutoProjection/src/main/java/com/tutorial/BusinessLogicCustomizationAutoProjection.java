package com.tutorial;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.catalog.domain.CategoryProduct;
import com.broadleafcommerce.catalog.domain.product.Variant;
import com.broadleafcommerce.catalog.repository.product.ProductRepository;
import com.broadleafcommerce.catalog.service.CategoryProductService;
import com.broadleafcommerce.catalog.service.autoconfigure.CatalogServiceAutoConfiguration;
import com.broadleafcommerce.catalog.service.product.VariantService;
import com.broadleafcommerce.common.extension.TypeFactory;
import com.broadleafcommerce.common.extension.cache.CacheStateManager;
import com.broadleafcommerce.data.tracking.core.Trackable;
import com.broadleafcommerce.data.tracking.core.filtering.fetch.FilterParser;
import com.broadleafcommerce.data.tracking.core.service.RsqlCrudEntityHelper;
import com.tutorial.service.ElectricCarService;

import cz.jirutka.rsql.parser.ast.Node;

/**
 * Setup components for the extension case.
 */
@Configuration
@ComponentScan(basePackageClasses = ElectricCarService.class)
@AutoConfigureBefore(CatalogServiceAutoConfiguration.class)
public class BusinessLogicCustomizationAutoProjection {}

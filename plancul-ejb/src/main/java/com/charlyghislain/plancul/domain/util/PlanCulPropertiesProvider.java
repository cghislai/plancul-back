package com.charlyghislain.plancul.domain.util;

import com.charlyghislain.plancul.domain.i18n.Language;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

@Singleton
@Startup
public class PlanCulPropertiesProvider {

    private static final String DEFAULT_PROPERTIES_BUNDLE_PATH = "com/charlyghislain/plancul/configuration/parameters-defaults";

    private Map<String, String> systemProperties;
    private ResourceBundle defaultPropertiesBundle;

    @PostConstruct
    public void init() {
        defaultPropertiesBundle = ResourceBundle.getBundle(DEFAULT_PROPERTIES_BUNDLE_PATH);
        systemProperties = new HashMap<>();

        this.fillSystemProperties();
    }

    public String getValue(PlanCulProperties planCulProperties) {
        String propertyKey = planCulProperties.getKey();
        String defaultValue = defaultPropertiesBundle.getString(propertyKey);
        return systemProperties.getOrDefault(propertyKey, defaultValue);
    }

    public String getValue(PlanCulProperties planCulProperties, Language language) {
        String propertyKey = planCulProperties.getKey();
        ResourceBundle bundle = ResourceBundle.getBundle(DEFAULT_PROPERTIES_BUNDLE_PATH, language.getLocale());
        String defaultValue = bundle.getString(propertyKey);
        return systemProperties.getOrDefault(planCulProperties.getKey(), defaultValue);
    }

    private void fillSystemProperties() {
        Arrays.stream(PlanCulProperties.values())
                .forEach(this::initializeParameter);
    }

    private void initializeParameter(PlanCulProperties planCulProperties) {
        String key = planCulProperties.getKey();
        String systemPropertyValue = System.getProperty(key);

        // Ensure we have a default value defined
        defaultPropertiesBundle.getString(key);

        Optional.ofNullable(systemPropertyValue)
                .ifPresent(value -> systemProperties.put(key, value));
    }
}

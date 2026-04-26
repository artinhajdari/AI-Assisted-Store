package com.demo.Store.service;

import java.util.Locale;

public interface MessageSourceService {

    /**
     * Resolves a localized message using the current locale context.
     *
     * @param key message key to resolve
     * @return resolved localized message
     */
    String getMessage(String key);

    /**
     * Resolves a localized message for an explicit locale.
     *
     * @param key message key to resolve
     * @param locale locale used for message lookup
     * @return resolved localized message
     */
    String getMessage(String key, Locale locale);

    /**
     * Resolves a localized message with placeholder arguments for an explicit locale.
     *
     * @param key message key to resolve
     * @param args placeholder arguments injected into the resolved message
     * @param locale locale used for message lookup
     * @return resolved localized message
     */
    String getMessage(String key, Object[] args, Locale locale);

}

package com.demo.Store.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageSourceServiceImplTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageSourceServiceImpl service;

    @AfterEach
    void cleanUp() {
        LocaleContextHolder.resetLocaleContext();
    }

    private static final String KEY = "key";
    private static final String VALUE = "value";

    @Test
    void getMessageUsesLocaleContextHolder() {
        LocaleContextHolder.setLocale(Locale.GERMANY);
        when(messageSource.getMessage(KEY, null, Locale.GERMANY)).thenReturn(VALUE);

        String result = service.getMessage(KEY);

        assertEquals(VALUE, result);
        verify(messageSource).getMessage(KEY, null, Locale.GERMANY);
    }

    @Test
    void getMessageWithLocaleUsesProvidedLocale() {
        when(messageSource.getMessage(KEY, null, Locale.US)).thenReturn(VALUE);

        String result = service.getMessage(KEY, Locale.US);

        assertEquals(VALUE, result);
        verify(messageSource).getMessage(KEY, null, Locale.US);
    }

    @Test
    void getMessageWithArgsUsesProvidedArgsAndLocale() {
        Object[] args = new Object[]{"x"};
        when(messageSource.getMessage(KEY, args, Locale.UK)).thenReturn(VALUE);

        String result = service.getMessage(KEY, args, Locale.UK);

        assertEquals(VALUE, result);
        verify(messageSource).getMessage(KEY, args, Locale.UK);
    }
}
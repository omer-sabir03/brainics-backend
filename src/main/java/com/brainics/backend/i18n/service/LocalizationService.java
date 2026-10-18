package com.brainics.backend.i18n.service;

import com.brainics.backend.i18n.model.Localization;
import com.brainics.backend.i18n.repository.LocalizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalizationService {

    private final LocalizationRepository localizationRepository;
    private static final String DEFAULT_LANGUAGE = "en";

    @Cacheable(value = "localization_all", key = "#language", unless = "#result == null")
    public Map<String, String> getAllTranslations(String language) {
        if (language == null || language.isBlank()) {
            language = DEFAULT_LANGUAGE;
        }

        List<Localization> translations = localizationRepository.findByLanguageCodeAndIsActiveTrue(language);

        Map<String, String> result = new HashMap<>();
        for (Localization translation : translations) {
            result.put(translation.getKey(), translation.getValue());
        }

        log.debug("Loaded {} translations for language: {}", result.size(), language);
        return result;
    }

    @Cacheable(value = "localization_category", key = "#language + '_' + #category", unless = "#result == null")
    public Map<String, String> getTranslationsByCategory(String language, String category) {
        if (language == null || language.isBlank()) {
            language = DEFAULT_LANGUAGE;
        }

        List<Localization> translations = localizationRepository
            .findByLanguageCodeAndCategoryAndIsActiveTrue(language, category);

        Map<String, String> result = new HashMap<>();
        for (Localization translation : translations) {
            result.put(translation.getKey(), translation.getValue());
        }

        log.debug("Loaded {} translations for language: {} category: {}", result.size(), language, category);
        return result;
    }

    @Cacheable(value = "localization_key", key = "#key + '_' + #language", unless = "#result == null")
    public String getTranslation(String key, String language) {
        if (language == null || language.isBlank()) {
            language = DEFAULT_LANGUAGE;
        }

        return localizationRepository.findByKeyAndLanguageCode(key, language)
            .map(Localization::getValue)
            .orElse(key);
    }

    public String getTranslationOrDefault(String key, String language, String defaultValue) {
        if (language == null || language.isBlank()) {
            language = DEFAULT_LANGUAGE;
        }

        return localizationRepository.findByKeyAndLanguageCode(key, language)
            .map(Localization::getValue)
            .orElse(defaultValue);
    }

    public String normalizeLanguageCode(String language) {
        if (language == null || language.isBlank()) {
            return DEFAULT_LANGUAGE;
        }

        return language.toLowerCase().substring(0, Math.min(2, language.length()));
    }

    public String detectLanguage(String acceptLanguageHeader, String queryParam) {
        if (queryParam != null && !queryParam.isBlank()) {
            return normalizeLanguageCode(queryParam);
        }

        if (acceptLanguageHeader != null && !acceptLanguageHeader.isBlank()) {
            return normalizeLanguageCode(acceptLanguageHeader.substring(0, Math.min(2, acceptLanguageHeader.length())));
        }

        return DEFAULT_LANGUAGE;
    }
}

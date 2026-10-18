package com.brainics.backend.i18n.controller;

import com.brainics.backend.common.ApiResponse;
import com.brainics.backend.i18n.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/i18n")
@RequiredArgsConstructor
public class LocalizationController {

    private final LocalizationService localizationService;

    @GetMapping("/keys")
    public ResponseEntity<ApiResponse<Map<String, String>>> getTranslations(
        @RequestParam(value = "lang", required = false) String language,
        @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage,
        @RequestParam(value = "category", required = false) String category) {

        String detectedLanguage = localizationService.detectLanguage(acceptLanguage, language);

        Map<String, String> translations;
        if (category != null && !category.isBlank()) {
            translations = localizationService.getTranslationsByCategory(detectedLanguage, category);
        } else {
            translations = localizationService.getAllTranslations(detectedLanguage);
        }

        return ResponseEntity.ok(ApiResponse.success(translations));
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getTranslation(
        @PathVariable String key,
        @RequestParam(value = "lang", required = false) String language,
        @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        String detectedLanguage = localizationService.detectLanguage(acceptLanguage, language);
        String translation = localizationService.getTranslation(key, detectedLanguage);

        return ResponseEntity.ok(ApiResponse.success(Map.of(key, translation)));
    }
}

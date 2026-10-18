package com.brainics.backend.i18n.repository;

import com.brainics.backend.i18n.model.Localization;
import org.springframework.data.jpa.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalizationRepository extends CrudRepository<Localization, Long> {
    Optional<Localization> findByKeyAndLanguageCode(String key, String languageCode);
    List<Localization> findByLanguageCodeAndIsActiveTrue(String languageCode);
    List<Localization> findByLanguageCodeAndCategoryAndIsActiveTrue(String languageCode, String category);
    List<Localization> findByLanguageCode(String languageCode);
}

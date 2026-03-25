// src/main/java/com/arabicpos/repository/SettingsRepository.java
package com.aboali.repository;

import com.aboali.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Optional<Settings> findByKey(String key);
    List<Settings> findByCategory(String category);
}

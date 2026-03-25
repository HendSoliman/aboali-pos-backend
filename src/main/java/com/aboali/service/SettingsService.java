// src/main/java/com/aboali/service/SettingsService.java
package com.aboali.service;

import com.aboali.exception.ResourceNotFoundException;
import com.aboali.model.Settings;
import com.aboali.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsService {
    
    private final SettingsRepository repo;
    
    // ── Get all settings as a flat key→value map ─────────────────
    @Transactional(readOnly = true)
    public Map<String, String> getAllAsMap() {
        return repo.findAll()
                   .stream()
                   .collect(Collectors.toMap(
                           Settings::getKey,
                           s -> s.getValue() != null ? s.getValue() : ""
                   ));
    }
    
    // ── Get all settings as a list ───────────────────────────────
    @Transactional(readOnly = true)
    public List<Settings> getAll() {
        return repo.findAll();
    }
    
    // ── Get by category ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<Settings> getByCategory(String category) {
        return repo.findByCategory(category.toUpperCase());
    }
    
    // ── Get single setting by key ────────────────────────────────
    @Transactional(readOnly = true)
    public Settings getByKey(String key) {
        return repo.findByKey(key)
                   .orElseThrow(() ->
                           new ResourceNotFoundException("الإعداد غير موجود: " + key));
    }
    
    // ── Get value string directly (with fallback) ────────────────
    @Transactional(readOnly = true)
    public String getValue(String key, String defaultValue) {
        return repo.findByKey(key)
                   .map(Settings::getValue)
                   .orElse(defaultValue);
    }
    
    // ── Upsert single key ────────────────────────────────────────
    @Transactional
    public Settings upsert(String key, String value, String category) {
        Settings setting = repo.findByKey(key)
                               .orElse(Settings.builder()
                                               .key(key)
                                               .category(category != null
                                                       ? category.toUpperCase()
                                                       : "GENERAL")
                                               .build());
        
        setting.setValue(value);
        log.info("Settings upsert → key='{}' value='{}'", key, value);
        return repo.save(setting);
    }
    
    // ── Bulk save: { "store_name": "متجر", "tax_rate": "15" } ────
    @Transactional
    public Map<String, String> bulkSave(Map<String, String> settings) {
        settings.forEach((key, value) -> {
            // Determine category from key prefix conventions
            String category = resolveCategoryFromKey(key);
            upsert(key, value, category);
        });
        
        log.info("Bulk settings saved — {} keys updated", settings.size());
        return getAllAsMap();
    }
    
    // ── Delete by key ────────────────────────────────────────────
    @Transactional
    public void delete(String key) {
        Settings setting = repo.findByKey(key)
                               .orElseThrow(() ->
                                       new ResourceNotFoundException("الإعداد غير موجود: " + key));
        repo.delete(setting);
        log.info("Setting deleted → key='{}'", key);
    }
    
    // ── Helpers ──────────────────────────────────────────────────
    
    /**
     * Auto-categorizes settings by key prefix:
     *   store_*    → STORE
     *   tax_*      → BILLING
     *   currency_* → BILLING
     *   receipt_*  → RECEIPT
     *   everything else → GENERAL
     */
    private String resolveCategoryFromKey(String key) {
        if (key == null) return "GENERAL";
        if (key.startsWith("store_"))    return "STORE";
        if (key.startsWith("tax_"))      return "BILLING";
        if (key.startsWith("currency"))  return "BILLING";
        if (key.startsWith("receipt_"))  return "RECEIPT";
        return "GENERAL";
    }
}

// src/main/java/com/aboali/controller/SettingsController.java
package com.aboali.controller;

import com.aboali.dto.ApiResponse;
import com.aboali.model.Settings;
import com.aboali.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingsController {
    
    private final SettingsService service;
    
    // ── GET all settings ─────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAllAsMap()));
    }
    
    // ── GET single setting by key ────────────────────────────────
    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse<Settings>> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByKey(key)));
    }
    
    // ── GET settings by category ─────────────────────────────────
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Settings>>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByCategory(category)));
    }
    
    // ── POST upsert a single key ──────────────────────────────────
    @PostMapping("/{key}")
    public ResponseEntity<ApiResponse<Settings>> upsert(
            @PathVariable String key,
            @RequestBody Map<String, String> body) {
        String value    = body.get("value");
        String category = body.getOrDefault("category", "GENERAL");
        return ResponseEntity.ok(
                ApiResponse.ok("تم الحفظ", service.upsert(key, value, category))
        );
    }
    
    // ── POST bulk save (map of key → value) ───────────────────────
    // Request body: { "store_name": "متجر عربي", "tax_rate": "15", ... }
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<Map<String, String>>> bulkSave(
            @RequestBody Map<String, String> settings) {
        return ResponseEntity.ok(
                ApiResponse.ok("تم حفظ الإعدادات بنجاح", service.bulkSave(settings))
        );
    }
    
    // ── DELETE a setting ─────────────────────────────────────────
    @DeleteMapping("/{key}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String key) {
        service.delete(key);
        return ResponseEntity.ok(ApiResponse.ok("تم الحذف", null));
    }
}

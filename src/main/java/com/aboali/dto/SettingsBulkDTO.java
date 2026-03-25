package com.aboali.dto;

import lombok.Data;

@Data
public class SettingsBulkDTO {
    private String key;
    private String value;   // always String — no numbers or booleans
}

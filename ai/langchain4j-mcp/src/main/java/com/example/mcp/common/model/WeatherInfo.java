package com.example.mcp.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherInfo {
    private String city;
    private double temperature;
    private String condition;
    private double humidity;
    private double windSpeed;
}

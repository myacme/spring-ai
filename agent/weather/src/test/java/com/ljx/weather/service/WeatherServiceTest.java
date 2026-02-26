package com.ljx.weather.service;

import com.ljx.weather.model.WeatherInfo;
import com.ljx.weather.model.WeatherQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WeatherServiceTest {
    
    @Autowired
    private WeatherService weatherService;
    
    @Test
    public void testGetCurrentWeather() {
        WeatherInfo weather = weatherService.getCurrentWeather("北京");
        assertNotNull(weather);
        assertEquals("北京", weather.getCity());
        assertEquals(LocalDate.now(), weather.getDate());
        assertNotNull(weather.getTemperature());
        assertNotNull(weather.getCondition());
    }
    
    @Test
    public void testGetWeatherByDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        WeatherInfo weather = weatherService.getWeatherByDate("上海", futureDate);
        assertNotNull(weather);
        assertEquals("上海", weather.getCity());
        assertEquals(futureDate, weather.getDate());
    }
    
    @Test
    public void testGetWeatherForecast() {
        List<WeatherInfo> forecast = weatherService.getWeatherForecast("广州", 3);
        assertNotNull(forecast);
        assertEquals(3, forecast.size());
        assertEquals("广州", forecast.get(0).getCity());
    }
    
    @Test
    public void testQueryWeatherCurrent() {
        WeatherQuery query = new WeatherQuery();
        query.setCity("深圳");
        query.setQueryType(WeatherQuery.QueryType.CURRENT);
        
        List<WeatherInfo> result = weatherService.queryWeather(query);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("深圳", result.get(0).getCity());
    }
    
    @Test
    public void testQueryWeatherForecast() {
        WeatherQuery query = new WeatherQuery();
        query.setCity("杭州");
        query.setQueryType(WeatherQuery.QueryType.FORECAST);
        query.setDate("未来三天");
        
        List<WeatherInfo> result = weatherService.queryWeather(query);
        assertNotNull(result);
        assertTrue(result.size() >= 2 && result.size() <= 7);
    }
    
    @Test
    public void testGetSupportedCities() {
        String[] cities = weatherService.getSupportedCities();
        assertNotNull(cities);
        assertTrue(cities.length > 0);
        // 验证包含主要城市
        boolean hasBeijing = false;
        for (String city : cities) {
            if ("北京".equals(city)) {
                hasBeijing = true;
                break;
            }
        }
        assertTrue(hasBeijing);
    }
    
    @Test
    public void testUnsupportedCity() {
        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.getCurrentWeather("纽约");
        });
    }
    
    @Test
    public void testInvalidForecastDays() {
        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.getWeatherForecast("北京", 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.getWeatherForecast("北京", 8);
        });
    }
}
package com.ljx.weather.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DateParserServiceTest {
    
    @Autowired
    private DateParserService dateParserService;
    
    @Test
    public void testParseDateToday() {
        DateParserService.ParsedDate result = dateParserService.parseDate("今天");
        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getDate());
        assertEquals("今天", result.getDescription());
    }
    
    @Test
    public void testParseDateTomorrow() {
        DateParserService.ParsedDate result = dateParserService.parseDate("明天");
        assertNotNull(result);
        assertEquals(LocalDate.now().plusDays(1), result.getDate());
        assertEquals("明天", result.getDescription());
    }
    
    @Test
    public void testParseDateDefault() {
        DateParserService.ParsedDate result = dateParserService.parseDate("");
        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getDate());
        assertEquals("今天", result.getDescription());
        
        result = dateParserService.parseDate(null);
        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getDate());
    }
    
    @Test
    public void testIsFutureDate() {
        assertTrue(dateParserService.isFutureDate("明天"));
        assertTrue(dateParserService.isFutureDate("后天"));
        assertFalse(dateParserService.isFutureDate("昨天"));
        assertFalse(dateParserService.isFutureDate("前天"));
        assertFalse(dateParserService.isFutureDate("今天"));
    }
    
    @Test
    public void testIsForecastQuery() {
        assertTrue(dateParserService.isForecastQuery("明天天气"));
        assertTrue(dateParserService.isForecastQuery("未来三天"));
        assertTrue(dateParserService.isForecastQuery("天气预报"));
        assertFalse(dateParserService.isForecastQuery("今天天气"));
        assertFalse(dateParserService.isForecastQuery("昨天天气"));
    }
    
    @Test
    public void testGetForecastDays() {
        assertEquals(2, dateParserService.getForecastDays("明天"));
        assertEquals(3, dateParserService.getForecastDays("后天"));
        assertEquals(3, dateParserService.getForecastDays("未来三天"));
        assertEquals(7, dateParserService.getForecastDays("未来一周"));
        assertEquals(3, dateParserService.getForecastDays("")); // 默认值
    }
}
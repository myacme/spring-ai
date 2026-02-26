package com.ljx.weather.service;

import com.ljx.weather.model.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LocationExtractorServiceTest {
    
    @Autowired
    private LocationExtractorService locationExtractorService;
    
    @Test
    public void testExtractCity() {
        assertEquals("北京", locationExtractorService.extractCity("今天北京天气"));
        assertEquals("上海", locationExtractorService.extractCity("上海的温度"));
        assertEquals("广州", locationExtractorService.extractCity("广州明天天气"));
        assertEquals("深圳", locationExtractorService.extractCity("深圳会下雨吗"));
    }
    
    @Test
    public void testExtractCityWithAlias() {
        assertEquals("北京", locationExtractorService.extractCity("首都天气怎么样"));
        assertEquals("上海", locationExtractorService.extractCity("魔都今天天气"));
        assertEquals("广州", locationExtractorService.extractCity("羊城温度"));
    }
    
    @Test
    public void testExtractCityDefault() {
        assertEquals("北京", locationExtractorService.extractCity(""));
        assertEquals("北京", locationExtractorService.extractCity(null));
        assertEquals("北京", locationExtractorService.extractCity("天气怎么样"));
    }
    
    @Test
    public void testIsSupportedCity() {
        assertTrue(locationExtractorService.isSupportedCity("北京"));
        assertTrue(locationExtractorService.isSupportedCity("上海"));
        assertFalse(locationExtractorService.isSupportedCity("纽约"));
        assertFalse(locationExtractorService.isSupportedCity(""));
        assertFalse(locationExtractorService.isSupportedCity(null));
    }
    
    @Test
    public void testGetLocationInfo() {
        Location beijing = locationExtractorService.getLocationInfo("北京");
        assertNotNull(beijing);
        assertEquals("北京", beijing.getCity());
        assertEquals("北京市", beijing.getProvince());
        assertEquals("中国", beijing.getCountry());
        assertEquals(116.4074, beijing.getLongitude(), 0.0001);
        assertEquals(39.9042, beijing.getLatitude(), 0.0001);
        
        Location shanghai = locationExtractorService.getLocationInfo("上海");
        assertNotNull(shanghai);
        assertEquals("上海", shanghai.getCity());
        assertEquals("上海市", shanghai.getProvince());
        
        // 测试不支持的城市
        Location unsupported = locationExtractorService.getLocationInfo("纽约");
        assertNull(unsupported);
    }
    
    @Test
    public void testGetLocationInfoNull() {
        Location result = locationExtractorService.getLocationInfo(null);
        assertNull(result);
        
        result = locationExtractorService.getLocationInfo("");
        assertNull(result);
    }
}
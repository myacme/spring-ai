package com.ljx.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljx.weather.model.WeatherQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
public class WeatherControllerTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @Test
    public void testHealthCheck() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        MvcResult result = mockMvc.perform(get("/weather/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("\"status\":\"UP\""));
        assertTrue(content.contains("\"service\":\"Weather Agent\""));
    }
    
    @Test
    public void testGetApiInfo() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        MvcResult result = mockMvc.perform(get("/weather/api-info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("\"service\":\"天气查询Agent\""));
        assertTrue(content.contains("\"version\":\"1.0.0\""));
    }
    
    @Test
    public void testGetSupportedCities() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        MvcResult result = mockMvc.perform(get("/weather/cities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("北京"));
        assertTrue(content.contains("上海"));
    }
    
    @Test
    public void testQueryWeatherSuccess() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "今天北京天气怎么样？");
        
        MvcResult result = mockMvc.perform(post("/weather/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertFalse(content.contains("\"status\":\"ERROR\""));
    }
    
    @Test
    public void testQueryWeatherEmptyQuery() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        Map<String, String> request = new HashMap<>();
        request.put("query", "");
        
        MvcResult result = mockMvc.perform(post("/weather/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("查询语句不能为空"));
    }
    
    @Test
    public void testStructuredQuerySuccess() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        WeatherQuery query = new WeatherQuery();
        query.setQuery("天气查询");
        query.setCity("北京");
        query.setQueryType(WeatherQuery.QueryType.CURRENT);
        
        MvcResult result = mockMvc.perform(post("/weather/structured")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("\"status\":\"SUCCESS\""));
    }
    
    @Test
    public void testStructuredQueryInvalidCity() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        WeatherQuery query = new WeatherQuery();
        query.setQuery("天气查询");
        query.setCity("纽约");
        query.setQueryType(WeatherQuery.QueryType.CURRENT);
        
        MvcResult result = mockMvc.perform(post("/weather/structured")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(status().isBadRequest())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("不支持的城市"));
    }
}
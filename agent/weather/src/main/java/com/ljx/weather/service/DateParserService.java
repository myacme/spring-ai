package com.ljx.weather.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * 日期解析服务
 */
@Service
public class DateParserService {
    
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("MM-dd"),
        DateTimeFormatter.ofPattern("MM/dd")
    };
    
    // 日期关键词映射
    private static final List<DateKeyword> DATE_KEYWORDS = Arrays.asList(
        new DateKeyword("今天", 0),
        new DateKeyword("今日", 0),
        new DateKeyword("现在", 0),
        new DateKeyword("当前", 0),
        new DateKeyword("明天", 1),
        new DateKeyword("明日", 1),
        new DateKeyword("后天", 2),
        new DateKeyword("大后天", 3),
        new DateKeyword("昨天", -1),
        new DateKeyword("前天", -2)
    );
    
    /**
     * 解析日期字符串
     */
    public ParsedDate parseDate(String dateText) {
        if (dateText == null || dateText.trim().isEmpty()) {
            return new ParsedDate(LocalDate.now(), "今天");
        }
        
        // 关键词匹配
        for (DateKeyword keyword : DATE_KEYWORDS) {
            if (dateText.contains(keyword.keyword)) {
                LocalDate date = LocalDate.now().plusDays(keyword.offset);
                return new ParsedDate(date, keyword.keyword);
            }
        }
        
        // 格式化日期解析
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(dateText, formatter);
                return new ParsedDate(date, dateText);
            } catch (DateTimeParseException e) {
                // 继续尝试其他格式
            }
        }
        
        // 相对日期解析
        ParsedDate relativeDate = parseRelativeDate(dateText);
        if (relativeDate != null) {
            return relativeDate;
        }
        
        // 默认返回今天
        return new ParsedDate(LocalDate.now(), "今天");
    }
    
    /**
     * 解析相对日期
     */
    private ParsedDate parseRelativeDate(String text) {
        // 解析"3天后"、"5天前"等格式
        if (text.matches(".*\\d+天[前后].*")) {
            try {
                int days = extractNumber(text);
                boolean isFuture = text.contains("后");
                int offset = isFuture ? days : -days;
                LocalDate date = LocalDate.now().plusDays(offset);
                return new ParsedDate(date, text);
            } catch (Exception e) {
                // 解析失败
            }
        }
        
        // 解析"下周"、"下个月"等
        if (text.contains("下周")) {
            LocalDate date = LocalDate.now().plusWeeks(1);
            return new ParsedDate(date, "下周");
        } else if (text.contains("下个月")) {
            LocalDate date = LocalDate.now().plusMonths(1);
            return new ParsedDate(date, "下个月");
        }
        
        return null;
    }
    
    /**
     * 从文本中提取数字
     */
    private int extractNumber(String text) {
        StringBuilder number = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c);
            }
        }
        return number.length() > 0 ? Integer.parseInt(number.toString()) : 1;
    }
    
    /**
     * 判断是否是未来日期
     */
    public boolean isFutureDate(String dateText) {
        ParsedDate parsedDate = parseDate(dateText);
        return parsedDate.getDate().isAfter(LocalDate.now());
    }
    
    /**
     * 判断是否是预报查询
     */
    public boolean isForecastQuery(String dateText) {
        if (dateText == null) return false;
        
        return dateText.contains("明天") || 
               dateText.contains("后天") || 
               dateText.contains("未来") || 
               dateText.contains("预报") || 
               dateText.contains("几天") ||
               dateText.matches(".*\\d+天后.*");
    }
    
    /**
     * 获取预报天数
     */
    public int getForecastDays(String dateText) {
        if (dateText == null) return 3;
        
        if (dateText.contains("一周") || dateText.contains("7天")) {
            return 7;
        } else if (dateText.contains("三天") || dateText.contains("3天")) {
            return 3;
        } else if (dateText.contains("五天") || dateText.contains("5天")) {
            return 5;
        } else if (dateText.contains("明天")) {
            return 2;
        } else if (dateText.contains("后天")) {
            return 3;
        } else {
            return 3; // 默认3天
        }
    }
    
    /**
     * 日期解析结果类
     */
    public static class ParsedDate {
        private final LocalDate date;
        private final String description;
        
        public ParsedDate(LocalDate date, String description) {
            this.date = date;
            this.description = description;
        }
        
        public LocalDate getDate() {
            return date;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 日期关键词映射类
     */
    private static class DateKeyword {
        private final String keyword;
        private final int offset;
        
        public DateKeyword(String keyword, int offset) {
            this.keyword = keyword;
            this.offset = offset;
        }
    }
}
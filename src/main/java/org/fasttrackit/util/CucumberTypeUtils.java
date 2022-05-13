package org.fasttrackit.util;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CucumberTypeUtils {

    public static String transformToDate(String s) {
        return transformToDate(s, 0);
    }

    public static String transformToDate(String s, long sleep) {
        if (!Strings.isNullOrEmpty(s)) {
            s = removeDoubleQuotes(s);
            Pattern pattern = Pattern.compile("(yesterday|today|tomorrow|nextWeek|nextMonth1DayAgo|nextMonth2DaysAgo|nextMonth3DaysAgo|nextMonthAnd1Day|nextMonthAnd2Days|nextMonth|next6Months1DayAgo|next6Months2DaysAgo|next6MonthsAnd1Day|next6MonthsAnd2Days|2DaysAgo|3DaysAgo|1WeekAgo|in2Days|in3Days|nextYear1DayAgo|nextYear)([EMdyHhsmaSTZ':,.\\-\\s\\/]+)?");
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                String day = matcher.group(1);
                try {
                    Thread.sleep(sleep); // This is for not generate a long email
                } catch (InterruptedException e) {
                    log.error("InterruptedException: {}", e.getMessage());
                }
                LocalDateTime now = LocalDateTime.now();
                if ("yesterday".equals(day)) {
                    now = now.minusDays(1L);
                } else if ("2DaysAgo".equals(day)) {
                    now = now.minusDays(2L);
                } else if ("3DaysAgo".equals(day)) {
                    now = now.minusDays(3L);
                } else if ("tomorrow".equals(day)) {
                    now = now.plusDays(1L);
                } else if ("in2Days".equals(day)) {
                    now = now.plusDays(2L);
                } else if ("in3Days".equals(day)) {
                    now = now.plusDays(3L);
                } else if ("nextWeek".equals(day)) {
                    now = now.plusDays(7L);
                } else if ("1WeekAgo".equals(day)) {
                    now = now.minusDays(7L);
                } else if ("nextMonth".equals(day)) {
                    now = now.plusMonths(1L);
                } else if ("nextMonth1DayAgo".equals(day)) {
                    now = now.plusMonths(1L).minusDays(1);
                } else if ("nextMonth2DaysAgo".equals(day)) {
                    now = now.plusMonths(1L).minusDays(2);
                } else if ("nextMonth3DaysAgo".equals(day)) {
                    now = now.plusMonths(1L).minusDays(3);
                } else if ("nextMonthAnd1Day".equals(day)) {
                    now = now.plusMonths(1L).plusDays(1);
                } else if ("nextMonthAnd2Days".equals(day)) {
                    now = now.plusMonths(1L).plusDays(2);
                } else if ("next6Months1DayAgo".equals(day)) {
                    now = now.plusMonths(6L).minusDays(1);
                } else if ("next6Months2DaysAgo".equals(day)) {
                    now = now.plusMonths(6L).minusDays(2);
                } else if ("next6MonthsAnd1Day".equals(day)) {
                    now = now.plusMonths(6L).plusDays(1);
                } else if ("next6MonthsAnd2Days".equals(day)) {
                    now = now.plusMonths(6L).plusDays(2);
                } else if ("nextYear".equals(day)) {
                    now = now.plusYears(1L);
                } else if ("nextYear1DayAgo".equals(day)) {
                    now = now.plusYears(1L).minusDays(1);
                }
                String format = matcher.group(2);
                String date;
                if (Strings.isNullOrEmpty(format)) {
                    date = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd").withLocale(Locale.ENGLISH));
                } else {
                    date = now.format(DateTimeFormatter.ofPattern(format.trim()).withLocale(Locale.ENGLISH));
                }
                s = s.replace(matcher.group(0).trim(), date);
            }
        }
        return s;
    }

    public static String generateLongString(String s) {
        s = removeDoubleQuotes(s);
        if (s.contains("s_")) {
            Pattern pattern = Pattern.compile("s_(\\d+)");
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                String count = matcher.group(1);
                String result = StringUtils.leftPad("", Integer.parseInt(count), "a");
                s = s.replace(matcher.group(0), result);
            }
        }
        return s;
    }

    public static String tokenizeEmailAddress(String email) {
        if (email.startsWith("tokenized_email:") && email.contains("@")) {
            email = email.replace("tokenized_email:", "");
            email = email.replace("@", "+" + System.currentTimeMillis() + "@");
        }
        return email;
    }

    public static String replaceSpecialChar(String s) {
        s = removeDoubleQuotes(s);
        if (s.startsWith("tokenized_email:")) {
            s = tokenizeEmailAddress(s);
        }
        return s.replace("_quote_", "\"")
                .replace("_blank", " ")
                .replace("pipe_", "|")
                .replace("_newLine_", "\n");
    }

    public static String removeDoubleQuotes(String s) {
        return s.replaceAll("^\"|\"$", "");
    }

    public static void main(String[] args) {
        String s = CucumberTypeUtils.transformToDate("today MMMddHHmmssSSS");
        log.debug("{}", s);
    }
}
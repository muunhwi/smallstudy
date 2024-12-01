package com.smallstudy.utils;

import java.time.format.DateTimeFormatter;

public class StaticFormatter {

    public static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy. MM. dd. a h:mm:ss");
    public static DateTimeFormatter TIME_YMD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

}

package com.malsati.xrest.utilities.text;

public class StringExtensions {
    public static boolean IsBlankJson(String json) {
        return json != null && json.trim().equalsIgnoreCase("{}");
    }

    public static boolean IsNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
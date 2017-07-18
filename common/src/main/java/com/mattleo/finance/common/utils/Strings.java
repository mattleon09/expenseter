package com.mattleo.finance.common.utils;

public final class Strings {
    private Strings() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}

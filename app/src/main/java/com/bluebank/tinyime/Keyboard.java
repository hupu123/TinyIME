package com.bluebank.tinyime;

public class Keyboard {
    private static final String[][] KEYBOARD_LOWER = new String[][]{
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"},
            {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"},
            {"a", "s", "d", "f", "g", "h", "j", "k", "l"},
            {"z", "x", "c", "v", "b", "n", "m", "cap", "del"}
    };

    private static final String[][] KEYBOARD_UPER = new String[][]{
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"},
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"Z", "X", "C", "V", "B", "N", "M", "CAP", "DEL"}
    };

    public static String getKeyValue(int row, int position, boolean isUper) {
        if (isUper) {
            return KEYBOARD_UPER[row][position];
        } else {
            return KEYBOARD_LOWER[row][position];
        }
    }

    public static String[][] getKeyboardValues(boolean isUper) {
        if (isUper) {
            return KEYBOARD_UPER;
        } else {
            return KEYBOARD_LOWER;
        }
    }
}

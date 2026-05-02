package com.insuretechpro.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * InputUtil keeps Scanner code in one place.
 * Menu classes use this class to read clean input from the user.
 */
public class InputUtil {
    private static final Scanner SCANNER = new Scanner(System.in);

    private InputUtil() {
        // Private constructor prevents object creation for this utility class.
    }

    public static String readString(String message) {
        // Reads one line of text from the console.
        System.out.print(message);
        return SCANNER.nextLine().trim();
    }

    public static long readLong(String message) {
        while (true) {
            try {
                // Converts text input into long value for database IDs.
                return Long.parseLong(readString(message));
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    public static int readInt(String message) {
        while (true) {
            try {
                // Converts text input into int value.
                return Integer.parseInt(readString(message));
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    public static double readDouble(String message) {
        while (true) {
            try {
                // Converts text input into decimal value.
                return Double.parseDouble(readString(message));
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }

    public static LocalDate readDate(String message) {
        while (true) {
            try {
                // Date must be entered in yyyy-MM-dd format, for example 2026-05-02.
                return LocalDate.parse(readString(message));
            } catch (DateTimeParseException exception) {
                System.out.println("Please enter date in yyyy-MM-dd format.");
            }
        }
    }
}

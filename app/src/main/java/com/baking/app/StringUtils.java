package com.baking.app;

/**
 * {@link "https://www.dotnetperls.com/uppercase-first-letter-java"}
 */
public class StringUtils {

    public static String upperCaseFirst(String value) {

        // Convert String to char array.
        char[] array = value.toCharArray();
        // Modify first element in array.
        array[0] = Character.toUpperCase(array[0]);
        // Return string.
        return new String(array);
    }

}

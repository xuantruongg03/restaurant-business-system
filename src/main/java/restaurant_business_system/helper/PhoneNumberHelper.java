package restaurant_business_system.helper;

public class PhoneNumberHelper {
    /**
     * Check if the string is a valid phone number.
     * Assume a valid phone number contains only digits and is between 10 and 15 characters long.
     *
     * @param s The string to check.
     * @return true if the string is a valid phone number, otherwise false.
     */
    public static boolean isValidPhoneNumber(String s) {
        // check length string
        if (s == null || s.length() < 10 || s.length() > 15) {
            return false;
        }

        // check all characters in the string are digits
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }

        // if all characters are digits and the length is between 10 and 15, return true
        return true;
    }
}


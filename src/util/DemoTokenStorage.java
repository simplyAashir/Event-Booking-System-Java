package util;

public class DemoTokenStorage {
    private static String lastResetToken;
    private static String userEmail;
    
    public static void setLastResetToken(String token) {
        lastResetToken = token;
    }
    
    public static String getLastResetToken() {
        return lastResetToken;
    }
    
    public static void setUserEmail(String email) {
        userEmail = email;
    }
    
    public static String getUserEmail() {
        return userEmail;
    }
}
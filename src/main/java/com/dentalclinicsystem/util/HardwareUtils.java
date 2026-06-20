package com.dentalclinicsystem.util;

import java.security.MessageDigest;

public class HardwareUtils {
    //------------------------------Harware id (HWID )-----------------------------------//
    public static String getHWID() {

        try {
            String data = System.getProperty("user.name") 
                    + System.getProperty("os.name")
                    + System.getenv("COMPUTERNAME");

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash =  md.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : hash) {
                sb.append(String.format("%02x",b));
            }

            return sb.toString();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
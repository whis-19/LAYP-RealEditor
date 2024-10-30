package DAL;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashCalculator {
	  public static String calculateHash(String text) throws Exception {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
	        byte[] hashBytes = md.digest(textBytes);
	        String hexHash = bytesToHex(hashBytes);
	        return hexHash;
	    }

	    private static String bytesToHex(byte[] bytes) {
	        StringBuilder hexString = new StringBuilder();
	        for (byte b : bytes) {
	            int unsignedByte = b & 0xFF;
	            String hex = Integer.toHexString(unsignedByte).toUpperCase();
	            if (hex.length() == 1) {
	                hexString.append('0');
	            }
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    }

}
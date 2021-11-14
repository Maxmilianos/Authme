package the.max.authme.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	
	public static String getHash(String message, String algorithm) {
        try {
            if (message == null)
                message = "";
            StringBuffer hexString = new StringBuffer();
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(message.getBytes());

            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0"
                            + Integer.toHexString((0xFF & hash[i])));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMD5(String message) {
        return getHash(message, "MD5");
    }

    public static String getSHA256(String message) {
        return getHash(message, "SHA-256");
    }
}

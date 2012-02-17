package com.bladefs.client.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    private static final ThreadLocal<MessageDigest> DIGESTER_CONTEXT = new ThreadLocal<MessageDigest>() {
        @Override
		protected synchronized MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    };
    
    public static byte[] getDigest(String s) {
        byte[] digest = md5(s);
        return digest;
    }
    
    public static byte[] md5(String string) {
        return md5(encode(string));
    }
    
    public static byte[] md5(byte[] data) {
        return md5(data, 0, data.length);
    }
    
    public static byte[] md5(byte[] data, int start, int len) {
        MessageDigest digester = DIGESTER_CONTEXT.get();
        digester.update(data, start, len);
        return digester.digest();
    }
    
    public static byte [] encode(String s) {
        int strlen = s.length();
        int utflen = 0;
        char c;
        for (int i = 0; i < strlen; i++) {
            c = s.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }
        byte [] buf = new byte[utflen];
        encode(s, buf, 0);
        return buf;
    }
    
    public static int encode(String s, byte [] bytes, int offset) {
        int strlen = s.length();

        int i=0;
        char c;
        int pos = offset;
        for (i=0; i<strlen; i++) {
            c = s.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) break;
            bytes[pos++] = (byte) c;
        }

        for (; i<strlen; i++) {
            c = s.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytes[pos++] = (byte)c;
            } else if (c > 0x07FF) {
                bytes[pos++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytes[pos++] = (byte) (0x80 | ((c >>  6) & 0x3F));
                bytes[pos++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            } else {
                bytes[pos++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
                bytes[pos++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            }
        }
        return pos - offset;
    }

}

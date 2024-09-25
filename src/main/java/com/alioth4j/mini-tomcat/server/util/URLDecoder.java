package server.util;

import java.io.UnsupportedEncodingException;

public final class URLDecoder {

    public static String urlDecode(String str) {
        return urlDecode(str, null);
    }

    public static String urlDecode(String str, String enc) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        byte[] bytes = new byte[len];
        bytes = str.getBytes();
        return urlDecode(bytes, enc);
    }

    public static String urlDecode(byte[] bytes) {
        return urlDecode(bytes, null);
    }

    public static String urlDecode(byte[] bytes, String enc) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        int ix = 0;
        int ox = 0;
        while (ix < len) {
            byte b = bytes[ix++];
            if (b == '+') {
                b = (byte) ' ';
            } else if (b == '%') {
                b = (byte) (convertHexDigit(bytes[ix++]) << 4 + convertHexDigit(bytes[ix++]));
            }
            bytes[ox++] = b;
        }
        if (enc != null) {
            try {
                return new String(bytes, 0, ox, enc);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return new String(bytes, 0, ox);
    }

    private static byte convertHexDigit(byte b) {
        if (b >= '0' && b <= '9') {
            return (byte)(b - '0');
        } else if (b >= 'a' && b <= 'f') {
            return (byte)(b - 'a' + 10);
        } else if (b >= 'A' && b <= 'F') {
            return (byte)(b - 'A' + 10);
        }
        return 0;
    }

}

package net.wtako.PlayerInventoryLogger.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressUtils {

    public static byte[] compress(String str) {
        if (str == null || str.length() == 0) {
            return str.getBytes();
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static String decompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        String outStr = "";
        try {
            final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
            final BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
            String line;
            while ((line = bf.readLine()) != null) {
                outStr += line;
            }
            bf.close();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return outStr;
    }
}

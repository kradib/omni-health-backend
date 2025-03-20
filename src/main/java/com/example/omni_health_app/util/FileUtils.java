package com.example.omni_health_app.util;

public final class FileUtils {

    public static String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

}

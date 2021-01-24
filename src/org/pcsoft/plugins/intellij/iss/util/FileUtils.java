package org.pcsoft.plugins.intellij.iss.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {
    public static void writeByteArrayToFile(File file, byte[] bytes) throws IOException {
        try(final OutputStream out = new FileOutputStream(file)) {
            out.write(bytes);
        }
    }
}

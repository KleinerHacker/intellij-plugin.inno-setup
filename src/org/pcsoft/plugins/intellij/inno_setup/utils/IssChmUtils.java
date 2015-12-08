package org.pcsoft.plugins.intellij.inno_setup.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.lang.SystemUtils;
import org.pcsoft.plugins.intellij.inno_setup.ui.IssChmHelpBrowser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Christoph on 08.12.2015.
 */
public final class IssChmUtils {

    public static File decompileChm(File chmFile) throws IOException, InterruptedException {
        final long fileHash = FileUtil.fileHashCode(chmFile) + (chmFile.lastModified() * chmFile.length());

        final File tmpDir = new File(SystemUtils.JAVA_IO_TMPDIR, fileHash + ".tmp");
        if (tmpDir.exists())
            return tmpDir;

        tmpDir.mkdirs();

        final File copyFile = new File(tmpDir, chmFile.getName() + ".copy");
        FileUtil.copy(chmFile, copyFile);

        final Process process = new ProcessBuilder()
                .directory(tmpDir)
                .command("hh.exe", "-decompile", ".", copyFile.getName())
                .start();
        if (!process.waitFor(2500, TimeUnit.MILLISECONDS)) {
            Logger.getInstance(IssChmHelpBrowser.class).warn("Unable to decompile help");
            return null;
        }
        if (process.exitValue() != 0) {
            Logger.getInstance(IssChmHelpBrowser.class).warn("Help Decompile returns with exit code " + process.exitValue());
            return null;
        }

        return tmpDir;
    }

    private IssChmUtils() {
    }
}

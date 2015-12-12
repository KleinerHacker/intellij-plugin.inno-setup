package org.pcsoft.plugins.intellij.inno_setup.utils;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import org.pcsoft.plugins.intellij.inno_setup.configuration.app.IssCompilerSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pfeifchr on 09.12.2015.
 */
public final class IssLanguageUtils {
    private static final IssCompilerSettings SETTINGS = ServiceManager.getService(IssCompilerSettings.class);

    public static List<File> findLanguageFiles() {
        if (SETTINGS.getInstallationPlace() == null)
            return new ArrayList<>();

        final File installationPath = new File(SETTINGS.getInstallationPlace());
        if (!installationPath.exists()) {
            Logger.getInstance(IssLanguageUtils.class).error("Unable to find installation path: " + installationPath.getAbsolutePath());
            return new ArrayList<>();
        }

        final File languagePath = new File(installationPath, SETTINGS.getLanguagePlace());
        if (!languagePath.exists()) {
            Logger.getInstance(IssLanguageUtils.class).error("Unable to find languages path: " + languagePath.getAbsolutePath());
            return new ArrayList<>();
        }

        final List<File> fileList = new ArrayList<>();
        for (final File file : languagePath.listFiles(pathname -> pathname.getAbsolutePath().toLowerCase().endsWith(".isl"))) {
            fileList.add(file);
        }

        return fileList;
    }

    private IssLanguageUtils() {
    }
}

package org.pcsoft.plugins.intellij.inno_setup.configuration.ui;

import com.intellij.codeInspection.ui.ListWrappingTableModel;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import org.pcsoft.plugins.intellij.inno_setup.configuration.IssCompilerSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Christoph on 23.11.2015.
 */
final class IssLanguageTableModel extends ListWrappingTableModel {
    private static final IssCompilerSettings SETTINGS = ServiceManager.getService(IssCompilerSettings.class);
    private static final Pattern PATTERN = Pattern.compile("LanguageName\\s*=\\s*(.+)");
    public static final List<List<String>> LISTSRMPTY_DATA_LIST = Arrays.asList(new ArrayList<>(), new ArrayList<>());

    public IssLanguageTableModel() {
        super(buildData(), buildColumns());
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    private static String[] buildColumns() {
        return new String[]{"Filename", "Language-Name"};
    }

    private static List<List<String>> buildData() {
        final File installationPath = new File(SETTINGS.getInstallationPlace());
        if (!installationPath.exists()) {
            System.err.println("Unable to find installation path: " + installationPath.getAbsolutePath());
            return LISTSRMPTY_DATA_LIST;
        }

        final File languagePath = new File(installationPath, SETTINGS.getLanguagePlace());
        if (!languagePath.exists()) {
            System.err.println("Unable to find languages path: " + languagePath.getAbsolutePath());
            return LISTSRMPTY_DATA_LIST;
        }

        final List<String> fileNameList = new ArrayList<>(), lanNameList = new ArrayList<>();
        for (final File file : languagePath.listFiles(pathname -> pathname.getAbsolutePath().toLowerCase().endsWith(".isl"))) {
            String lanName = "???";
            try (final InputStream in = new FileInputStream(file)) {
                try (final Scanner scanner = new Scanner(in)) {
                    String line = null;
                    while ((line = scanner.next()) != null) {
                        final Matcher matcher = PATTERN.matcher(line);
                        if (matcher.matches()) {
                            lanName = matcher.group(1);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                Logger.getInstance(IssLanguageTableModel.class).error("IO", e);
                return LISTSRMPTY_DATA_LIST;
            }

            fileNameList.add(file.getName());
            lanNameList.add(lanName);
        }

        return Arrays.asList(fileNameList, lanNameList);
    }
}

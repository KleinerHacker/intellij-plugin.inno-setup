package org.pcsoft.plugins.intellij.inno_setup.configuration.ui;

import com.intellij.codeInspection.ui.ListWrappingTableModel;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import org.pcsoft.plugins.intellij.inno_setup.configuration.IssCompilerSettings;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
    private static final Pattern PATTERN_LAN_NAME = Pattern.compile("LanguageName\\s*=\\s*(.+)"), PATTERN_LAN_ID = Pattern.compile("LanguageID\\s*=\\s*(.+)"),
            PATTERN_LAN_CODEPAGE = Pattern.compile("LanguageCodePage\\s*=\\s*(.+)"),
            PATTERN_UNICODE = Pattern.compile("<([0-9A-Z]{4})>");
    public static final List<List<String>> LISTSRMPTY_DATA_LIST = Arrays.asList(new ArrayList<>(), new ArrayList<>());

    public IssLanguageTableModel() {
        super(buildData(), buildColumns());
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    private static String[] buildColumns() {
        return new String[]{"Filename", "Language Name", "ID", "Code Page"};
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

        final List<String> fileNameList = new ArrayList<>(), lanNameList = new ArrayList<>(), lanIdList = new ArrayList<>(), lanCodePageList = new ArrayList<>();
        for (final File file : languagePath.listFiles(pathname -> pathname.getAbsolutePath().toLowerCase().endsWith(".isl"))) {
            String lanName = null, lanId = null, lanCodePage = null;
            try (final InputStream in = new FileInputStream(file)) {
                try (final InputStreamReader reader = new InputStreamReader(in, "ISO-8859-1")) {
                    try (final Scanner scanner = new Scanner(reader)) {
                        String line = null;
                        while ((line = scanner.next()) != null && (lanName == null || lanId == null || lanCodePage == null)) {
                            Matcher matcher = PATTERN_LAN_NAME.matcher(line);
                            if (matcher.matches()) {
                                lanName = matcher.group(1);
                                lanName = convertFromUnicode(lanName);
                                continue;
                            }
                            matcher = PATTERN_LAN_ID.matcher(line);
                            if (matcher.matches()) {
                                lanId = matcher.group(1);
                                continue;
                            }
                            matcher = PATTERN_LAN_CODEPAGE.matcher(line);
                            if (matcher.matches()) {
                                lanCodePage = matcher.group(1);
                                continue;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Logger.getInstance(IssLanguageTableModel.class).error("IO", e);
                return LISTSRMPTY_DATA_LIST;
            }

            fileNameList.add(file.getName());
            lanNameList.add(lanName == null ? "UNKNOWN" : lanName);
            lanIdList.add(lanId == null ? "UNKNOWN" : lanId);
            lanCodePageList.add(lanCodePage == null ? "UNKNOWN" : lanCodePage);
        }

        return Arrays.asList(fileNameList, lanNameList, lanIdList, lanCodePageList);
    }

    private static String convertFromUnicode(String text) throws UnsupportedEncodingException {
        final Matcher unicodeMatcher = PATTERN_UNICODE.matcher(text);
        while (unicodeMatcher.find()) {
            text = text.replace("<" + unicodeMatcher.group(1) + ">", new String(new HexBinaryAdapter().unmarshal(unicodeMatcher.group(1)), "Unicode"));
        }
        return text;
    }
}

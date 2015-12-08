package org.pcsoft.plugins.intellij.inno_setup.configuration.run;

import com.intellij.openapi.diagnostic.Logger;
import org.pcsoft.plugins.intellij.inno_setup.IssConstants;
import org.pcsoft.plugins.intellij.inno_setup.configuration.app.IssCompilerSettings;
import org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui.IssSymbolValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public final class IssCompileRunUtils {
    public static final class Conversion {
        public static String convertSymbolValues(final IssSymbolValue... values) {
            if (values == null || values.length <= 0)
                return null;

            final StringBuilder sb = new StringBuilder();
            for (final IssSymbolValue value : values) {
                sb.append(value.getName()).append("=").append(value.getValue()).append("|");
            }

            return sb.toString();
        }

        public static IssSymbolValue[] convertSymbolValues(final String values) {
            if (values == null || values.trim().isEmpty())
                return null;
            final String[] symbolValueParts = values.split("\\|");

            final List<IssSymbolValue> list = new ArrayList<>();
            for (final String symbolValuePart : symbolValueParts) {
                final String[] valueParts = symbolValuePart.split("=");
                if (valueParts.length != 2) {
                    Logger.getInstance(IssCompileRunUtils.class).warn("Unable to read value: " + symbolValuePart);
                    continue;
                }
                list.add(new IssSymbolValue(valueParts[0], valueParts[1]));
            }

            return list.toArray(new IssSymbolValue[list.size()]);
        }

        private Conversion() {
        }
    }

    public static String buildCommandLine(final IssCompileRunConfiguration configuration, final IssCompilerSettings settings) {
        final StringBuilder sb = new StringBuilder();
        //***** Base call
        sb.append("\"").append(settings.getInstallationPlace()).append("/").append(IssConstants.EXE_COMPILER).append("\" ");

        //***** Options
        //*** Output
        if (configuration.getOutputDir() != null && !configuration.getOutputDir().trim().isEmpty()) {
            sb.append("\"/O").append(configuration.getOutputDir()).append("\" ");
        }
        //*** Base Filename
        if (configuration.getOutputBaseFilename() != null && !configuration.getOutputBaseFilename().trim().isEmpty()) {
            sb.append("\"/F").append(configuration.getOutputBaseFilename()).append("\" ");
        }
        //*** Logging
        switch (configuration.getOutputLogging()) {
            case None:
                sb.append("/Q ");
                break;
            case Progress:
                sb.append("/Qp ");
                break;
            case Verbose:
                //Do nothing, is default
                break;
            default:
                throw new RuntimeException();
        }
        //*** Symbols
        if (configuration.getSymbolValues() != null && configuration.getSymbolValues().length > 0) {
            for (final IssSymbolValue symbolValue : configuration.getSymbolValues()) {
                sb.append("\"/D").append(symbolValue.getName());
                if (symbolValue.getValue() != null && !symbolValue.getValue().trim().isEmpty()) {
                    sb.append("=").append(symbolValue.getValue());
                }
                sb.append("\" ");
            }
        }

        //***** Filename
        sb.append("\"").append(configuration.getScriptFile()).append("\"");

        return sb.toString();
    }

    private IssCompileRunUtils() {
    }
}

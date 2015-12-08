package org.pcsoft.plugins.intellij.inno_setup.configuration.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.configuration.app.IssCompilerSettings;
import org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui.IssSymbolValue;

import java.io.File;
import java.io.IOException;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssCompileRunConfiguration extends LocatableConfigurationBase {
    private static final String KEY_SCRIPT_FILE = "ScriptFile";
    private static final String KEY_OUTPUT_DIR = "OutputDir";
    private static final String KEY_OUTPUT_BASE_FILENAME = "OutputBaseFilename";
    private static final String KEY_OUTPUT_LOGGING = "OutputLogging";
    private static final String KEY_SYMBOL_VALUES = "SymbolValues";

    private final IssCompilerSettings compilerSettings = ServiceManager.getService(IssCompilerSettings.class);
    private String scriptFile, outputDir, outputBaseFilename;
    private IssCompileRunOutputLogging outputLogging;
    private IssSymbolValue[] symbolValues;

    public IssCompileRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new IssCompileRunSettingsEditor();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (scriptFile == null || scriptFile.isEmpty())
            throw new RuntimeConfigurationException("No script file is set!");
        if (!new File(scriptFile).exists())
            throw new RuntimeConfigurationException("Given script file not exists!");
        if (outputDir != null && !outputDir.trim().isEmpty() && !new File(outputDir).exists())
            throw new RuntimeConfigurationException("Given output directory not exists!");

        final File testFile = new File(outputDir, outputBaseFilename + ".exe");
        try {
            if (outputDir != null && !outputDir.trim().isEmpty() && outputBaseFilename != null && !outputBaseFilename.trim().isEmpty() &&
                    !testFile.createNewFile())
                throw new RuntimeConfigurationException("Unable to create the output file!");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeConfigurationException(e.getMessage(), "Unable to create the output file");
        } finally {
            testFile.delete();
        }

        compilerSettings.validateForCompileRun();
    }

    @Override
    public String suggestedName() {
        return "Inno Setup Compilation";
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new IssCompileRun(this);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        scriptFile = JDOMExternalizerUtil.readField(element, KEY_SCRIPT_FILE);
        outputDir = JDOMExternalizerUtil.readField(element, KEY_OUTPUT_DIR);
        outputBaseFilename = JDOMExternalizerUtil.readField(element, KEY_OUTPUT_BASE_FILENAME);
        try {
            final String outputLoggingString = JDOMExternalizerUtil.readField(element, KEY_OUTPUT_LOGGING);
            outputLogging = outputLoggingString == null ? null : IssCompileRunOutputLogging.valueOf(outputLoggingString);
        } catch (IllegalArgumentException e) {
            outputLogging = null;
        }
        symbolValues = IssCompileRunUtils.Conversion.convertSymbolValues(JDOMExternalizerUtil.readField(element, KEY_SYMBOL_VALUES));
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, KEY_SCRIPT_FILE, scriptFile);
        JDOMExternalizerUtil.writeField(element, KEY_OUTPUT_DIR, outputDir);
        JDOMExternalizerUtil.writeField(element, KEY_OUTPUT_BASE_FILENAME, outputBaseFilename);
        JDOMExternalizerUtil.writeField(element, KEY_OUTPUT_LOGGING, outputLogging == null ? null : outputLogging.name());
        JDOMExternalizerUtil.writeField(element, KEY_SYMBOL_VALUES, IssCompileRunUtils.Conversion.convertSymbolValues(symbolValues));
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getOutputBaseFilename() {
        return outputBaseFilename;
    }

    public void setOutputBaseFilename(String outputBaseFilename) {
        this.outputBaseFilename = outputBaseFilename;
    }

    public IssCompileRunOutputLogging getOutputLogging() {
        return outputLogging == null ? IssCompileRunOutputLogging.getDefault() : outputLogging;
    }

    public void setOutputLogging(IssCompileRunOutputLogging outputLogging) {
        this.outputLogging = outputLogging;
    }

    public IssSymbolValue[] getSymbolValues() {
        return symbolValues;
    }

    public void setSymbolValues(IssSymbolValue[] symbolValues) {
        this.symbolValues = symbolValues;
    }
}

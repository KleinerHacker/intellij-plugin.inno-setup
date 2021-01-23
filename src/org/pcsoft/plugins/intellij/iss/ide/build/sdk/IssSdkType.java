package org.pcsoft.plugins.intellij.iss.ide.build.sdk;

import com.intellij.openapi.projectRoots.*;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.IssIcons;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;

public class IssSdkType extends SdkType {
    public static final String NAME = "Inno Setup Kit";

    public IssSdkType() {
        super(NAME);
    }

    @Override
    public @Nullable
    String suggestHomePath() {
        return System.getenv("IS_HOME");
    }

    @Override
    public boolean isValidSdkHome(String s) {
        final File file = new File(s);
        if (!file.exists() || !file.isDirectory())
            return false;

        final File[] files = file.listFiles();
        if (files == null)
            return false;

        return Arrays.stream(files).anyMatch(x -> x.getName().equalsIgnoreCase("iscc.exe"));
    }

    @Override
    public @NotNull
    String suggestSdkName(@Nullable String sdkName, String sdkHome) {
        return new File(sdkHome).getName();
    }

    @Override
    public @Nullable
    AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public @NotNull
    @Nls(capitalization = Nls.Capitalization.Title)
    String getPresentableName() {
        return "Inno Setup Kit";
    }

    @Override
    public Icon getIcon() {
        return IssIcons.SDK;
    }

    @Override
    public @NotNull Icon getIconForAddAction() {
        return IssIcons.SDK;
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData sdkAdditionalData, @NotNull Element element) {

    }

    @Override
    public boolean setupSdkPaths(@NotNull Sdk sdk, @NotNull SdkModel sdkModel) {
        SdkModificator modificator = sdk.getSdkModificator();
        modificator.setVersionString(getVersionString(sdk));
        modificator.commitChanges(); // save
        return true;
    }

    @Override
    public @Nullable
    String getVersionString(String sdkHome) {
        final String[] parts = StringUtils.split(new File(sdkHome).getName(), ' ');
        return parts[parts.length - 1];
    }

    /*@Override
    public boolean supportsCustomCreateUI() {
        return true;
    }

    @Override
    public void showCustomCreateUI(@NotNull SdkModel sdkModel, @NotNull JComponent parentComponent, @Nullable Sdk selectedSdk, @NotNull Consumer<? super Sdk> sdkCreatedCallback) {
        if (selectedSdk == null)
            return;

        final VerticalBox pnlMain = new VerticalBox();
        {
            final TextFieldWithBrowseButton pathField = new TextFieldWithBrowseButton(new JTextField(selectedSdk.getHomePath()));
            pathField.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(false, true, false, false, false, false)));

            final LabeledComponent<TextFieldWithBrowseButton> path = new LabeledComponent<>();
            path.setComponent(pathField);

            pnlMain.add(path);
        }

        final JPanel pnlRoot = new JPanel(new BorderLayout());
        pnlRoot.add(pnlMain, BorderLayout.NORTH);

        parentComponent.add(pnlRoot);
    }*/


}

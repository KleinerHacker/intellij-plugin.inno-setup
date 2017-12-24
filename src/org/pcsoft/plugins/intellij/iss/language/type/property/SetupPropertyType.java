package org.pcsoft.plugins.intellij.iss.language.type.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.ValueType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.*;
import org.pcsoft.plugins.intellij.iss.language.type.value.*;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum SetupPropertyType implements PropertyType {
    //Compiler related
    ASLRCompatible("ASLRCompatible", ValueType.Boolean, true, BooleanValueType.class),
    Compression("Compression", ValueType.SingleValue, "lzma2/max", CompressionValueType.class),
    CompressionThreads("CompressionThreads", new ValueType[]{ValueType.SingleValue, ValueType.Number}, "auto", CompressionThreadsValueType.class),
    DEPCompatible("DEPCompatible", ValueType.Boolean, true, BooleanValueType.class),
    DiskClusterSize("DiskCluster", ValueType.Number, 512),
    DiskSliceSize("DiskSliceSize", new ValueType[]{ValueType.SingleValue, ValueType.Number}, "max", DiskSliceSizeValueType.class),
    DiskSpanning("DiskSpanning", ValueType.Boolean, false, BooleanValueType.class),
    Encryption("Encryption", ValueType.Boolean, false, BooleanValueType.class),
    InternalCompressLevel("InternalCompressLevel", ValueType.SingleValue, "normal", CompressionLevelValueType.class),
    LZMAAlgorithm("LZMAAlgorithm", ValueType.Number, null),
    LZMABlockSize("LZMABlockSize", ValueType.Number, null),
    LZMADictionarySize("LZMADictionarySize", ValueType.Number, null),
    LZMAMatchFinder("LZMAMatchFinder", ValueType.Number, null, LZMAMatchFinderValueType.class),
    LZMANumBlockThreads("LZMANumBlockThreads", ValueType.Number, 1),
    LZMANumFastBytes("LZMANumFastBytes", ValueType.Number, null),
    LZMAUseSeparateProcess("LZMAUseSeparateProcess", ValueType.Boolean, false, BooleanValueType.class),
    MergeDuplicateFiles("MergeDuplicateFiles", ValueType.Boolean, true, BooleanValueType.class),
    Output("Output", ValueType.Boolean, true, BooleanValueType.class),
    OutputBaseFilename("OutputBaseFilename", ValueType.String, "mysetup"),
    OutputDir("OutputDir", ValueType.String, "Output"),
    OutputManifestFile("OutputManifestFile", ValueType.String, null),
    ReserveBytes("ReserveBytes", ValueType.Number, 0),
    SignedUninstaller("SignedUninstaller", ValueType.Boolean, null, BooleanValueType.class),
    SignedUninstallerDir("SignedUninstallerDir", ValueType.String, null),
    SignTool("SignTool", ValueType.String, null),
    SignToolRetryCount("SignToolRetryCount", ValueType.Number, 2),
    SlicesPerDisk("SlicesPerDisk", ValueType.Number, 1),
    SolidCompression("SolidCompression", ValueType.Boolean, false, BooleanValueType.class),
    SourceDir("SourceDir", ValueType.String, null),
    TerminalServicesAware("TerminalServicesAware", ValueType.Boolean, true, BooleanValueType.class),
    UseSetupLdr("UseSetupLdr", ValueType.Boolean, true, BooleanValueType.class),
    VersionInfoCompany("VersionInfoCompany", ValueType.String, null),
    VersionInfoCopyright("VersionInfoCopyright", ValueType.String, null),
    VersionInfoDescription("VersionInfoDescription", ValueType.String, null),
    VersionInfoProductName("VersionInfoProductName", ValueType.String, null),
    VersionInfoProductTextVersion("VersionInfoProductTextVersion", ValueType.String, null),
    VersionInfoProductVersion("VersionInfoProductVersion", ValueType.Version, null),
    VersionInfoTextVersion("VersionInfoTextVersion", ValueType.String, null),
    VersionInfoVersion("VersionInfoVersion", ValueType.Version, "0.0.0.0"),
    //Intsaller related
    AllowCancelDuringInstall("AllowCancelDuringInstall", ValueType.Boolean, true, BooleanValueType.class),
    AllowNetworkDrive("AllowNetworkDrive", ValueType.Boolean, true, BooleanValueType.class),
    AllowNoIcon("AllowNoIcon", ValueType.Boolean, false, BooleanValueType.class),
    AllowRootDirectory("AllowRootDirectory", ValueType.Boolean, false, BooleanValueType.class),
    AllowUNCPath("AllowUNCPath", ValueType.Boolean, true, BooleanValueType.class),
    AlwaysRestart("AlwaysRestart", ValueType.Boolean, false, BooleanValueType.class),
    AlwaysShowComponentsList("AlwaysShowComponentsList", ValueType.Boolean, true, BooleanValueType.class),
    AlwaysShowDirOnReadyPage("AlwaysShowDirOnReadyPage", ValueType.Boolean, false, BooleanValueType.class),
    AlwaysShowGroupOnReadyPage("AlwaysShowGroupOnReadyPage", ValueType.Boolean, false, BooleanValueType.class),
    AlwaysUsePersonalGroup("AlwaysUsePersonalGroup", ValueType.Boolean, false, BooleanValueType.class),
    AppendDefaultDirName("AppendDefaultDirName", ValueType.Boolean, true, BooleanValueType.class),
    AppendDefaultGroupName("AppendDefaultGroupName", ValueType.Boolean, true, BooleanValueType.class),
    AppComments("AppComments", ValueType.String, null),
    AppContact("AppContact", ValueType.String, null),
    AppId("AppId", ValueType.String, null),
    AppModifyPath("AppModifyPath", ValueType.String, null),
    AppMutex("AppMutex", ValueType.String, null),
    @IsRequired @IsKeyProperty
    AppName("AppName", ValueType.String, null),
    AppPublisher("AppPublisher", ValueType.String, null),
    AppPublisherURL("AppPublisherURL", ValueType.String, null),
    AppReadmeFile("AppReadmeFile", ValueType.String, null),
    AppSupportPhone("AppSupportPhone", ValueType.String, null),
    AppSupportURL("AppSupportURL", ValueType.String, null),
    AppUpdatesURL("AppUpdatesURL", ValueType.String, null),
    AppVerName("AppVerName", ValueType.String, null),
    @IsRequired @IsInfoProperty
    AppVersion("AppVersion", ValueType.Version, null),
    ArchitecturesAllowed("ArchitecturesAllowed", ValueType.MultiValue, null, ArchitecturesValueType.class),
    ArchitecturesInstallIn64BitMode("ArchitecturesInstallIn64BitMode", ValueType.MultiValue, Architectures64ValueType.class),
    ChangesAssociations("ChangesAssociations", ValueType.Boolean, false, BooleanValueType.class),
    ChangesEnvironment("ChangesEnvironment", ValueType.Boolean, false, BooleanValueType.class),
    CloseApplications("CloseApplications", ValueType.BooleanEx, true, BooleanWithForceValueType.class),
    CloseApplicationsFilter("CloseApplicationsFilter", ValueType.String, "*.exe,*.dll,*.chm"),
    CreateAppDir("CreateAppDir", ValueType.Boolean, true, BooleanValueType.class),
    CreateUninstallRegKey("CreateUninstallRegKey", ValueType.Boolean, true, BooleanValueType.class),
    DefaultDialogFontName("DefaultDialogFontName", ValueType.String, "Tahoma"),
    DefaultDirName("DefaultDirName", ValueType.String, null),
    DefaultGroupName("DefaultGroupName", ValueType.String, null),
    DefaultUserInfoName("DefaultUserInfoName", ValueType.String, null),
    DefaultUserInfoOrg("DefaultUserInfoOrg", ValueType.String, null),
    DefaultUserInfoSerial("DefaultUserInfoSerial", ValueType.String, null),
    DirExistsWarning("DirExistsWarning", ValueType.BooleanEx, null, BooleanWithAutoValueType.class),
    DisableDirPage("DisableDirPage", ValueType.BooleanEx, null, BooleanWithAutoValueType.class),
    DisableFinishedPage("DisableFinishedPage", ValueType.Boolean, false, BooleanValueType.class),
    DisableProgramGroupPage("DisableProgramGroupPage", ValueType.BooleanEx, null, BooleanWithAutoValueType.class),
    DisableReadyMemo("DisableReadyMemo", ValueType.Boolean, false, BooleanValueType.class),
    DisableReadyPage("DisableReadyPage", ValueType.Boolean, false, BooleanValueType.class),
    DisableStartupPrompt("DisableStartupPrompt", ValueType.Boolean, true, BooleanValueType.class),
    DisableWelcomePage("DisableWelcomePage", ValueType.Boolean, true, BooleanValueType.class),
    EnableDirDoesntExistWarning("EnableDirDoesntExistWarning", ValueType.Boolean, false, BooleanValueType.class),
    ExtraDiskSpaceRequired("ExtraDiskSpaceRequired", ValueType.Number, 0),
    InfoAfterFile("InfoAfterFile", ValueType.String, null),
    InfoBeforeFile("InfoBeforeFile", ValueType.String, null),
    LanguageDetectionMethod("LanguageDetectionMethod", ValueType.SingleValue, LanguageDetectionValueType.class),
    LicenseFile("LicenseFile", ValueType.String, null),
    MinVersion("MinVersion", ValueType.Version, null),
    OnlyBelowVersion("OnlyBelowVersion", ValueType.Version, null),
    Password("Password", ValueType.String, null),
    PrivilegesRequired("PrivilegesRequired", ValueType.SingleValue, PrivilegesValueType.class),
    RestartApplications("RestartApplications", ValueType.Boolean, true, BooleanValueType.class),
    RestartIfNeededByRun("RestartIfNeededByRun", ValueType.Boolean, true, BooleanValueType.class),
    SetupLogging("SetupLogging", ValueType.Boolean, false, BooleanValueType.class),
    SetupMutex("SetupMutex", ValueType.String, null),
    ShowLanguageDialog("ShowLanguageDialog", ValueType.BooleanEx, true, BooleanWithAutoValueType.class),
    ShowUndisplayableLanguages("ShowUndisplayableLanguages", ValueType.Boolean, false, BooleanValueType.class),
    TimeStampRounding("TimeStampRounding", ValueType.Number, 2),
    TimeStampsInUTC("TimeStampsInUTC", ValueType.Boolean, false, BooleanValueType.class),
    TouchDate("TouchDate", ValueType.SingleValue, "current", TouchDateValueType.class),
    TouchTime("TouchTime", ValueType.SingleValue, "current", TouchTimeValueType.class),
    Uninstallable("Uninstallable", ValueType.Boolean, true, BooleanValueType.class),
    UninstallDisplayIcon("UninstallDisplayIcon", ValueType.String, null),
    UninstallDisplayName("UninstallDisplayName", ValueType.String, null),
    UninstallDisplaySize("UninstallDisplaySize", ValueType.Number, null),
    UninstallFilesDir("UninstallFilesDir", ValueType.String, "{app}"),
    UninstallLogMode("UninstallLogMode", ValueType.SingleValue, LogModeValueType.class),
    UninstallRestartComputer("UninstallRestartComputer", ValueType.Boolean, false, BooleanValueType.class),
    UpdateUninstallLogAppName("UpdateUninstallLogAppName", ValueType.Boolean, true, BooleanValueType.class),
    UsePreviousAppDir("UsePreviousAppDir", ValueType.Boolean, true, BooleanValueType.class),
    UsePreviousGroup("UsePreviousGroup", ValueType.Boolean, true, BooleanValueType.class),
    UsePreviousLanguage("UsePreviousLanguage", ValueType.Boolean, true, BooleanValueType.class),
    UsePreviousSetupType("UsePreviousSetupType", ValueType.Boolean, true, BooleanValueType.class),
    UsePreviousTasks("UsePreviousTasks", ValueType.Boolean, true, BooleanValueType.class),
    UsePreviousUserInfo("UsePreviousUserInfo", ValueType.Boolean, true, BooleanValueType.class),
    UserInfoPage("UserInfoPage", ValueType.Boolean, false, BooleanValueType.class),
    //Cosmetic
    AppCopyright("AppCopyright", ValueType.String, null),
    BackColor("BackColor", new ValueType[]{ValueType.SingleValue, ValueType.Number}, "clBlue", ColorValueType.class),
    BackColor2("BackColor2", new ValueType[]{ValueType.SingleValue, ValueType.Number}, "clBlack", ColorValueType.class),
    BackColorDirection("BackColorDirection", ValueType.SingleValue, "topToBottom", ColorDirectionValueType.class),
    BackSolid("BackSolid", ValueType.Boolean, false, BooleanValueType.class),
    FlatComponentsList("FlatComponentsList", ValueType.String, null),
    SetupIconFile("SetupIconFile", ValueType.Boolean, false, BooleanValueType.class),
    ShowComponentSizes("ShowComponentSizes", ValueType.Boolean, true, BooleanValueType.class),
    ShowTasksTreeLines("ShowTasksTreeLines", ValueType.Boolean, false, BooleanValueType.class),
    WindowShowCaption("WindowShowCaption", ValueType.Boolean, true, BooleanValueType.class),
    WindowStartMaximized("WindowStartMaximized", ValueType.Boolean, true, BooleanValueType.class),
    WindowResizable("WindowResizable", ValueType.Boolean, true, BooleanValueType.class),
    WindowVisible("WindowVisible", ValueType.Boolean, true, BooleanValueType.class),
    WizardImageAlphaFormat("WizardImageAlphaFormat", ValueType.SingleValue, "none", AlphaFormatValueType.class),
    WizardImageFile("WizardImageFile", ValueType.String, "compiler:WIZMODERNIMAGE.BMP"),
    WizardImageStretch("WizardImageStretch", ValueType.Boolean, true, BooleanValueType.class),
    WizardSmallImageFile("WizardSmallImageFile", ValueType.String, "compiler:WIZMODERNSMALLIMAGE.BMP"),
    //Obsolete
    @IsDeprecated
    AlwaysCreateUninstallIcon("AlwaysCreateUninstallIcon", ValueType.String, null),
    @IsDeprecated
    DisableAppendDir("DisableAppendDir", ValueType.String, null),
    @IsDeprecated
    DontMergeDuplicateFiles("DontMergeDuplicateFiles", ValueType.String, null),
    @IsDeprecated
    MessagesFile("MessagesFile", ValueType.String, null),
    @IsDeprecated
    UninstallIconFile("UninstallIconFile", ValueType.String, null),
    @IsDeprecated
    UninstallIconName("UninstallIconName", ValueType.String, null),
    @IsDeprecated
    UninstallStyle("UninstallStyle", ValueType.String, null),
    @IsDeprecated
    WizardImageBackColor("WizardImageBackColor", ValueType.String, null),
    @IsDeprecated
    WizardSmallImageBackColor("WizardSmallImageBackColor", ValueType.String, null),
    @IsDeprecated
    WizardStyle("WizardStyle", ValueType.String, null),;

    private final String name;
    private final ValueType[] valueTypes;
    private final Object defaultValue;
    private final Class<? extends SpecialValueType> propertySpecialValueTypeClass;
    private final boolean required, deprecated;
    private final boolean isKey, isInfo;
    private final boolean isReferenceKey;
    private final SectionType referenceTargetSectionType;

    private SetupPropertyType(String name, ValueType valueType, Object defaultValue) {
        this(name, new ValueType[]{valueType}, defaultValue, null);
    }

    private SetupPropertyType(String name, ValueType[] valueTypes, Object defaultValue) {
        this(name, valueTypes, defaultValue, null);
    }

    private SetupPropertyType(String name, ValueType valueType, Object defaultValue, Class<? extends SpecialValueType> propertySpecialValueTypeClass) {
        this(name, new ValueType[]{valueType}, defaultValue, propertySpecialValueTypeClass);
    }

    private SetupPropertyType(String name, ValueType[] valueTypes, Object defaultValue, Class<? extends SpecialValueType> propertySpecialValueTypeClass) {
        this.name = name;
        this.valueTypes = valueTypes;
        this.defaultValue = defaultValue;
        this.propertySpecialValueTypeClass = propertySpecialValueTypeClass;

        try {
            required = getClass().getField(name()).getAnnotation(IsRequired.class) != null;
            deprecated = getClass().getField(name()).getAnnotation(IsDeprecated.class) != null;

            isKey = getClass().getField(name()).getAnnotation(IsKeyProperty.class) != null;
            isInfo = getClass().getField(name()).getAnnotation(IsInfoProperty.class) != null;

            isReferenceKey = getClass().getField(name()).getAnnotation(IsReferenceKey.class) != null;
            final ReferenceTo referenceToAnnotation = getClass().getField(name()).getAnnotation(ReferenceTo.class);
            referenceTargetSectionType = referenceToAnnotation == null ? null : referenceToAnnotation.value();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public ValueType[] getValueTypes() {
        return valueTypes;
    }

    @Nullable
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    @Override
    public Class<? extends SpecialValueType> getSpecialValueTypeClass() {
        return propertySpecialValueTypeClass;
    }

    @Override
    public boolean isKey() {
        return isKey;
    }

    @Override
    public boolean isInfo() {
        return isInfo;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public boolean isReferenceKey() {
        return isReferenceKey;
    }

    @Override
    public SectionType getReferenceTargetSectionType() {
        return referenceTargetSectionType;
    }
}

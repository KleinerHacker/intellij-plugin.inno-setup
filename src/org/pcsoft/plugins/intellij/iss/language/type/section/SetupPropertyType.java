package org.pcsoft.plugins.intellij.iss.language.type.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.PropertyValueType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.*;
import org.pcsoft.plugins.intellij.iss.language.type.value.*;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum SetupPropertyType implements PropertyType {
    //Compiler related
    ASLRCompatible("ASLRCompatible", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    Compression("Compression", PropertyValueType.SingleValue, "lzma2/max", PropertyCompressionValueType.class),
    CompressionThreads("CompressionThreads", new PropertyValueType[]{PropertyValueType.SingleValue, PropertyValueType.Number}, "auto", PropertyCompressionThreadsValueType.class),
    DEPCompatible("DEPCompatible", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    DiskClusterSize("DiskCluster", PropertyValueType.Number, 512),
    DiskSliceSize("DiskSliceSize", new PropertyValueType[]{PropertyValueType.SingleValue, PropertyValueType.Number}, "max", PropertyDiskSliceSizeValueType.class),
    DiskSpanning("DiskSpanning", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    Encryption("Encryption", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    InternalCompressLevel("InternalCompressLevel", PropertyValueType.SingleValue, "normal", PropertyCompressionLevelValueType.class),
    LZMAAlgorithm("LZMAAlgorithm", PropertyValueType.Number, null),
    LZMABlockSize("LZMABlockSize", PropertyValueType.Number, null),
    LZMADictionarySize("LZMADictionarySize", PropertyValueType.Number, null),
    LZMAMatchFinder("LZMAMatchFinder", PropertyValueType.Number, null, PropertyLZMAMatchFinderValueType.class),
    LZMANumBlockThreads("LZMANumBlockThreads", PropertyValueType.Number, 1),
    LZMANumFastBytes("LZMANumFastBytes", PropertyValueType.Number, null),
    LZMAUseSeparateProcess("LZMAUseSeparateProcess", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    MergeDuplicateFiles("MergeDuplicateFiles", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    Output("Output", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    OutputBaseFilename("OutputBaseFilename", PropertyValueType.String, "mysetup"),
    OutputDir("OutputDir", PropertyValueType.String, "Output"),
    OutputManifestFile("OutputManifestFile", PropertyValueType.String, null),
    ReserveBytes("ReserveBytes", PropertyValueType.Number, 0),
    SignedUninstaller("SignedUninstaller", PropertyValueType.Boolean, null, PropertyBooleanValueType.class),
    SignedUninstallerDir("SignedUninstallerDir", PropertyValueType.String, null),
    SignTool("SignTool", PropertyValueType.String, null),
    SignToolRetryCount("SignToolRetryCount", PropertyValueType.Number, 2),
    SlicesPerDisk("SlicesPerDisk", PropertyValueType.Number, 1),
    SolidCompression("SolidCompression", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    SourceDir("SourceDir", PropertyValueType.String, null),
    TerminalServicesAware("TerminalServicesAware", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    UseSetupLdr("UseSetupLdr", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    VersionInfoCompany("VersionInfoCompany", PropertyValueType.String, null),
    VersionInfoCopyright("VersionInfoCopyright", PropertyValueType.String, null),
    VersionInfoDescription("VersionInfoDescription", PropertyValueType.String, null),
    VersionInfoProductName("VersionInfoProductName", PropertyValueType.String, null),
    VersionInfoProductTextVersion("VersionInfoProductTextVersion", PropertyValueType.String, null),
    VersionInfoProductVersion("VersionInfoProductVersion", PropertyValueType.Version, null),
    VersionInfoTextVersion("VersionInfoTextVersion", PropertyValueType.String, null),
    VersionInfoVersion("VersionInfoVersion", PropertyValueType.Version, "0.0.0.0"),
    //Intsaller related
    AllowCancelDuringInstall("AllowCancelDuringInstall", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    AllowNetworkDrive("AllowNetworkDrive", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    AllowNoIcon("AllowNoIcon", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    AllowRootDirectory("AllowRootDirectory", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    AllowUNCPath("AllowUNCPath", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    AlwaysRestart("AlwaysRestart", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    AlwaysShowComponentsList("AlwaysShowComponentsList", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    AlwaysShowDirOnReadyPage("AlwaysShowDirOnReadyPage", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    AlwaysShowGroupOnReadyPage("AlwaysShowGroupOnReadyPage", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    AlwaysUsePersonalGroup("AlwaysUsePersonalGroup", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    AppendDefaultDirName("AppendDefaultDirName", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    AppendDefaultGroupName("AppendDefaultGroupName", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    AppComments("AppComments", PropertyValueType.String, null),
    AppContact("AppContact", PropertyValueType.String, null),
    AppId("AppId", PropertyValueType.String, null),
    AppModifyPath("AppModifyPath", PropertyValueType.String, null),
    AppMutex("AppMutex", PropertyValueType.String, null),
    @IsRequired @IsKeyProperty
    AppName("AppName", PropertyValueType.String, null),
    AppPublisher("AppPublisher", PropertyValueType.String, null),
    AppPublisherURL("AppPublisherURL", PropertyValueType.String, null),
    AppReadmeFile("AppReadmeFile", PropertyValueType.String, null),
    AppSupportPhone("AppSupportPhone", PropertyValueType.String, null),
    AppSupportURL("AppSupportURL", PropertyValueType.String, null),
    AppUpdatesURL("AppUpdatesURL", PropertyValueType.String, null),
    AppVerName("AppVerName", PropertyValueType.String, null),
    @IsRequired @IsInfoProperty
    AppVersion("AppVersion", PropertyValueType.Version, null),
    ArchitecturesAllowed("ArchitecturesAllowed", PropertyValueType.MultiValue, null, PropertyArchitecturesValueType.class),
    ArchitecturesInstallIn64BitMode("ArchitecturesInstallIn64BitMode", PropertyValueType.MultiValue, PropertyArchitectures64ValueType.class),
    ChangesAssociations("ChangesAssociations", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    ChangesEnvironment("ChangesEnvironment", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    CloseApplications("CloseApplications", PropertyValueType.Boolean, true, PropertyBooleanWithForceValueType.class),
    CloseApplicationsFilter("CloseApplicationsFilter", PropertyValueType.String, "*.exe,*.dll,*.chm"),
    CreateAppDir("CreateAppDir", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    CreateUninstallRegKey("CreateUninstallRegKey", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    DefaultDialogFontName("DefaultDialogFontName", PropertyValueType.String, "Tahoma"),
    DefaultDirName("DefaultDirName", PropertyValueType.String, null),
    DefaultGroupName("DefaultGroupName", PropertyValueType.String, null),
    DefaultUserInfoName("DefaultUserInfoName", PropertyValueType.String, null),
    DefaultUserInfoOrg("DefaultUserInfoOrg", PropertyValueType.String, null),
    DefaultUserInfoSerial("DefaultUserInfoSerial", PropertyValueType.String, null),
    DirExistsWarning("DirExistsWarning", PropertyValueType.Boolean, null, PropertyBooleanWithAutoValueType.class),
    DisableDirPage("DisableDirPage", PropertyValueType.Boolean, null, PropertyBooleanWithAutoValueType.class),
    DisableFinishedPage("DisableFinishedPage", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    DisableProgramGroupPage("DisableProgramGroupPage", PropertyValueType.Boolean, null, PropertyBooleanWithAutoValueType.class),
    DisableReadyMemo("DisableReadyMemo", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    DisableReadyPage("DisableReadyPage", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    DisableStartupPrompt("DisableStartupPrompt", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    DisableWelcomePage("DisableWelcomePage", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    EnableDirDoesntExistWarning("EnableDirDoesntExistWarning", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    ExtraDiskSpaceRequired("ExtraDiskSpaceRequired", PropertyValueType.Number, 0),
    InfoAfterFile("InfoAfterFile", PropertyValueType.String, null),
    InfoBeforeFile("InfoBeforeFile", PropertyValueType.String, null),
    LanguageDetectionMethod("LanguageDetectionMethod", PropertyValueType.SingleValue, PropertyLanguageDetectionValueType.class),
    LicenseFile("LicenseFile", PropertyValueType.String, null),
    MinVersion("MinVersion", PropertyValueType.Version, null),
    OnlyBelowVersion("OnlyBelowVersion", PropertyValueType.Version, null),
    Password("Password", PropertyValueType.String, null),
    PrivilegesRequired("PrivilegesRequired", PropertyValueType.SingleValue, PropertyPrivilegesValueType.class),
    RestartApplications("RestartApplications", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    RestartIfNeededByRun("RestartIfNeededByRun", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    SetupLogging("SetupLogging", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    SetupMutex("SetupMutex", PropertyValueType.String, null),
    ShowLanguageDialog("ShowLanguageDialog", PropertyValueType.Boolean, true, PropertyBooleanWithAutoValueType.class),
    ShowUndisplayableLanguages("ShowUndisplayableLanguages", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    TimeStampRounding("TimeStampRounding", PropertyValueType.Number, 2),
    TimeStampsInUTC("TimeStampsInUTC", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    TouchDate("TouchDate", PropertyValueType.SingleValue, "current", PropertyTouchDateValueType.class),
    TouchTime("TouchTime", PropertyValueType.SingleValue, "current", PropertyTouchTimeValueType.class),
    Uninstallable("Uninstallable", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    UninstallDisplayIcon("UninstallDisplayIcon", PropertyValueType.String, null),
    UninstallDisplayName("UninstallDisplayName", PropertyValueType.String, null),
    UninstallDisplaySize("UninstallDisplaySize", PropertyValueType.Number, null),
    UninstallFilesDir("UninstallFilesDir", PropertyValueType.String, "{app}"),
    UninstallLogMode("UninstallLogMode", PropertyValueType.SingleValue, PropertyLogModeValueType.class),
    UninstallRestartComputer("UninstallRestartComputer", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    UpdateUninstallLogAppName("UpdateUninstallLogAppName", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    UsePreviousAppDir("UsePreviousAppDir", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    UsePreviousGroup("UsePreviousGroup", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    UsePreviousLanguage("UsePreviousLanguage", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    UsePreviousSetupType("UsePreviousSetupType", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    UsePreviousTasks("UsePreviousTasks", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    UsePreviousUserInfo("UsePreviousUserInfo", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    UserInfoPage("UserInfoPage", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    //Cosmetic
    AppCopyright("AppCopyright", PropertyValueType.String, null),
    BackColor("BackColor", new PropertyValueType[]{PropertyValueType.SingleValue, PropertyValueType.Number}, "clBlue", PropertyColorValueType.class),
    BackColor2("BackColor2", new PropertyValueType[]{PropertyValueType.SingleValue, PropertyValueType.Number}, "clBlack", PropertyColorValueType.class),
    BackColorDirection("BackColorDirection", PropertyValueType.SingleValue, "topToBottom", PropertyColorDirectionValueType.class),
    BackSolid("BackSolid", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    FlatComponentsList("FlatComponentsList", PropertyValueType.String, null),
    SetupIconFile("SetupIconFile", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    ShowComponentSizes("ShowComponentSizes", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    ShowTasksTreeLines("ShowTasksTreeLines", PropertyValueType.Boolean, false, PropertyBooleanValueType.class),
    WindowShowCaption("WindowShowCaption", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    WindowStartMaximized("WindowStartMaximized", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    WindowResizable("WindowResizable", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    WindowVisible("WindowVisible", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    WizardImageAlphaFormat("WizardImageAlphaFormat", PropertyValueType.SingleValue, "none", PropertyAlphaFormatValueType.class),
    WizardImageFile("WizardImageFile", PropertyValueType.String, "compiler:WIZMODERNIMAGE.BMP"),
    WizardImageStretch("WizardImageStretch", PropertyValueType.Boolean, true, PropertyBooleanValueType.class),
    WizardSmallImageFile("WizardSmallImageFile", PropertyValueType.String, "compiler:WIZMODERNSMALLIMAGE.BMP"),
    //Obsolete
    @IsDeprecated
    AlwaysCreateUninstallIcon("AlwaysCreateUninstallIcon", PropertyValueType.String, null),
    @IsDeprecated
    DisableAppendDir("DisableAppendDir", PropertyValueType.String, null),
    @IsDeprecated
    DontMergeDuplicateFiles("DontMergeDuplicateFiles", PropertyValueType.String, null),
    @IsDeprecated
    MessagesFile("MessagesFile", PropertyValueType.String, null),
    @IsDeprecated
    UninstallIconFile("UninstallIconFile", PropertyValueType.String, null),
    @IsDeprecated
    UninstallIconName("UninstallIconName", PropertyValueType.String, null),
    @IsDeprecated
    UninstallStyle("UninstallStyle", PropertyValueType.String, null),
    @IsDeprecated
    WizardImageBackColor("WizardImageBackColor", PropertyValueType.String, null),
    @IsDeprecated
    WizardSmallImageBackColor("WizardSmallImageBackColor", PropertyValueType.String, null),
    @IsDeprecated
    WizardStyle("WizardStyle", PropertyValueType.String, null),;

    private final String name;
    private final PropertyValueType[] propertyValueTypes;
    private final Object defaultValue;
    private final Class<? extends PropertySpecialValueType> propertySpecialValueTypeClass;
    private final boolean required, deprecated;
    private final boolean isKey, isInfo;
    private final boolean isReferenceKey;
    private final SectionType referenceTargetSectionType;

    private SetupPropertyType(String name, PropertyValueType propertyValueType, Object defaultValue) {
        this(name, new PropertyValueType[]{propertyValueType}, defaultValue, null);
    }

    private SetupPropertyType(String name, PropertyValueType[] propertyValueTypes, Object defaultValue) {
        this(name, propertyValueTypes, defaultValue, null);
    }

    private SetupPropertyType(String name, PropertyValueType propertyValueType, Object defaultValue, Class<? extends PropertySpecialValueType> propertySpecialValueTypeClass) {
        this(name, new PropertyValueType[]{propertyValueType}, defaultValue, propertySpecialValueTypeClass);
    }

    private SetupPropertyType(String name, PropertyValueType[] propertyValueTypes, Object defaultValue, Class<? extends PropertySpecialValueType> propertySpecialValueTypeClass) {
        this.name = name;
        this.propertyValueTypes = propertyValueTypes;
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
    public PropertyValueType[] getPropertyValueTypes() {
        return propertyValueTypes;
    }

    @Nullable
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    @Override
    public Class<? extends PropertySpecialValueType> getPropertySpecialValueTypeClass() {
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

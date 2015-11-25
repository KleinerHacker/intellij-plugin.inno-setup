package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 22.12.2014.
 */
public enum IssSetupProperty implements IssPropertyIdentifier {
    //Compiler
    Compression("Compression", IssMarkerFactory.SetupSection.PROPERTY_COMPRESSION, IssMarkerFactory.SetupSection.PROPERTY_COMPRESSION_VALUE,
            IssSetupPropertyClassifier.Compiler, "setup.property.compression", IssValueType.DirectSingle),
    CompressionThreads("CompressionThreads", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    DiskClusterSize("DisClusterSize", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    DiskSliceSize("DiskSliceSize", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    DiskSpanning("DiskSpanning", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    Encryption("Encryption", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    InternalCompressLevel("InternalCompressLevel", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    LZMAAlgorithm("LZMAAlgorithm", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    LZMABlockSize("LZMABlockSize", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    LZMADictionarySize("LZMADictionarySize", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    LZMAMatchFinder("LZMAMatchFinder", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    LZMANumBlockThreads("LZMANumBlockThreads", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    LZMANumFastBytes("LZMANumFastBytes", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    LZMAUseSeparateProcess("LZMAUseSeparateProcess", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    MergeDuplicateFiles("MergeDuplicateFiles", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    Output("Output", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    OutputBaseFilename("OutputBaseFilename", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    OutputDir("OutputDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    OutputManifestFile("OutputManifestFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    ReserveBytes("ReserveBytes", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    SignedUninstaller("SignedUninstaller", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    SignedUninstallerDir("SignedUninstallerDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    SignTool("SignTool", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    SlicesPerDisk("SlicesPerDisk", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    SolidCompression("SolidCompression", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    SourceDir("SourceDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    TerminalServicesAware("TerminalServicesAware", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    UseSetupLdr("UseSetupLdr", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    VersionInfoCompany("VersionInfoCompany", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    VersionInfoCopyright("VersionInfoCopyright", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    VersionInfoDescription("VersionInfoDescription", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    VersionInfoProductName("VersionInfoProductName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    VersionInfoProductTextVersion("VersionInfoProductTextVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    VersionInfoProductVersion("VersionInfoProductVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    VersionInfoTextVersion("VersionInfoTextVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    VersionInfoVersion("VersionInfoVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Compiler, ""),
    // Installer
    AppName("AppName", IssMarkerFactory.SetupSection.PROPERTY_APPNAME, IssMarkerFactory.SetupSection.PROPERTY_APPNAME_VALUE,
            IssSetupPropertyClassifier.Installer, "setup.property.appname", true),
    AppVersion("AppVersion", IssMarkerFactory.SetupSection.PROPERTY_APPVERSION, IssMarkerFactory.SetupSection.PROPERTY_APPVERSION_VALUE,
            IssSetupPropertyClassifier.Installer, "setup.property.appversion", true),
    AllowCancelDuringInstall("AllowCancelDuringInstall", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AllowNetworkDrive("AllowNetworkDrive", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AllowNoIcons("AllowNoIcons", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AllowRootDirectory("AllowRootDirectory", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AllowUNCPath("AllowUNCPath", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AlwaysRestart("AlwaysRestart", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AlwaysShowComponentsList("AlwaysShowComponentsList", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AlwaysShowDirOnReadyPage("AlwaysShowDirOnReadyPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AlwaysShowGroupOnReadyPage("AlwaysShowGroupOnReadyPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AlwaysUsePersonalGroup("AlwaysUsePersonalGroup", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppendDefaultDirName("AppendDefaultDirName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppendDefaultGroupName("AppendDefaultGroupName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppComments("AppComments", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppContact("AppContact", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppId("AppId", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppModifyPath("AppModifyPath", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppMutex("AppMutex", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppPublisher("AppPublisher", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppPublisherURL("AppPublisherURL", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppReadmeFile("AppReadmeFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppSupportPhone("AppSupportPhone", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppSupportURL("AppSupportURL", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppUpdatesURL("AppUpdatesURL", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    AppVerName("AppVerName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    ArchitecturesAllowed("ArchitecturesAllowed", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    ArchitecturesInstallIn64BitMode("ArchitecturesInstallIn64BitMode", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    ChangesAssociations("ChangesAssociations", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    ChangesEnvironment("ChangesEnvironment", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    CloseApplications("CloseApplications", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    CloseApplicationsFilter("CloseApplicationsFilter", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    CreateAppDir("CreateAppDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    CreateUninstallRegKey("CreateUninstallRegKey", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DefaultDialogFontName("DefaultDialogFontName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DefaultDirName("DefaultDirName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DefaultGroupName("DefaultGroupName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DefaultUserInfoName("DefaultUserInfoName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DefaultUserInfoOrg("DefaultUserInfoOrg", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DefaultUserInfoSerial("DefaultUserInfoSerial", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DirExistsWarning("DirExistsWarning", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DisableDirPage("DisableDirPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DisableFinishedPage("DisableFinishedPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DisableProgramGroupPage("DisableProgramGroupPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DisableReadyMemo("DisableReadyMemo", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DisableReadyPage("DisableReadyPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DisableStartupPrompt("DisableStartupPrompt", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    DisableWelcomePage("DisableWelcomePage", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    EnableDirDoesntExistWarning("EnableDirDoesntExistWarning", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    ExtraDiskSpaceRequired("ExtraDiskSpaceRequired", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    InfoAfterFile("InfoAfterFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    InfoBeforeFile("InfoBeforeFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    LanguageDetectionMethod("LanguageDetectionMethod", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    LicenseFile("LicenseFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    MinVersion("MinVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    OnlyBelowVersion("OnlyBelowVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    Password("Password", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    PrivilegesRequired("PrivilegesRequired", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    RestartApplications("RestartApplications", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    RestartIfNeededByRun("RestartIfNeededByRun", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    SetupLogging("SetupLogging", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    ShowLanguageDialog("ShowLanguageDialog", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    ShowUndisplayableLanguages("ShowUndisplayableLanguages", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    TimeStampRounding("TimeStampRounding", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    TimeStampsInUTC("TimeStampsInUTC", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    TouchDate("TouchDate", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    TouchTime("TouchTime", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    Uninstallable("Uninstallable", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UninstallDisplayIcon("UninstallDisplayIcon", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UninstallDisplayName("UninstallDisplayName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UninstallDisplaySize("UninstallDisplaySize", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UninstallFilesDir("UninstallFilesDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UninstallLogMode("UninstallLogMode", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UninstallRestartComputer("UninstallRestartComputer", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UpdateUninstallLogAppName("UpdateUninstallLogAppName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UsePreviousAppDir("UsePreviousAppDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UsePreviousGroup("UsePreviousGroup", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UsePreviousLanguage("UsePreviousLanguage", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UsePreviousSetupType("UsePreviousSetupType", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UsePreviousTasks("UsePreviousTasks", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UsePreviousUserInfo("UsePreviousUserInfo", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    UserInfoPage("UserInfoPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Installer, ""),
    //Cosmetic
    AppCopyright("AppCopyright", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    BackColor("BackColor", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    BackColor2("BackColor2", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    BackColorDirection("BackColorDirection", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    BackSolid("BackSolid", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    FlatComponentsList("FlatComponentsList", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    SetupIconFile("SetupIconFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    ShowComponentSizes("ShowComponentSizes", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    ShowTasksTreeLines("ShowTasksTreeLines", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    WindowShowCaption("WindowShowCaption", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    WindowStartMaximized("WindowStartMaximized", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    WindowResizable("WindowResizable", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    WindowVisible("WindowVisible", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    WizardImageBackColor("WizardImageBackColor", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    WizardImageFile("WizardImageFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    WizardImageStretch("WizardImageStretch", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    WizardSmallImageFile("WizardSmallImageFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Cosmetic, ""),
    //Obsolete
    AlwaysCreateUninstallIcon("AlwaysCreateUninstallIcon", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Obsolete, ""),
    DisableAppendDir("DisableAppendDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Obsolete, ""),
    DontMergeDuplicateFiles("DontMergeDuplicateFiles", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Obsolete, ""),
    MessagesFile("MessagesFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Obsolete, ""),
    UninstallIconFile("UninstallIconFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Obsolete, ""),
    UninstallIconName("UninstallIconName", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Obsolete, ""),
    UninstallStyle("UninstallStyle", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Obsolete, ""),
    WizardSmallImageBackColor("WizardSmallImageBackColor", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Obsolete, ""),
    WizardStyle("WizardStyle", IssMarkerFactory.PROPERTY_UNKNOWN, null, IssSetupPropertyClassifier.Obsolete, "");

    @Override
    public String toString() {
        return id;
    }

    public static IssSetupProperty fromId(final String id) {
        return IssPropertyIdentifier.fromId(id, IssSetupProperty.class);
    }

    public static IElementType getPropertyMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getItemMarkerElementFromId(id, IssSetupProperty.class);
    }

    public static IElementType getPropertyValueMarkerElementFromId(final String id) {
        return IssPropertyIdentifier.getSingleValueMarkerElementFromId(id, IssSetupProperty.class);
    }

    private final String id, descriptionKey;
    private final IssSetupPropertyClassifier classifier;
    private final IElementType propertyMarkerElement, propertyValueMarkerElement;
    private final boolean required;
    private final IssValueType valueType;

    private IssSetupProperty(String id, IElementType propertyMarkerElement, IElementType propertyValueMarkerElement,
                             IssSetupPropertyClassifier classifier, String descriptionKey) {
        this(id, propertyMarkerElement, propertyValueMarkerElement, classifier, descriptionKey, IssValueType.DirectMultiple);
    }

    private IssSetupProperty(String id, IElementType propertyMarkerElement, IElementType propertyValueMarkerElement, 
                             IssSetupPropertyClassifier classifier, String descriptionKey, IssValueType valueType) {
        this(id, propertyMarkerElement, propertyValueMarkerElement, classifier, descriptionKey, valueType, false);
    }

    private IssSetupProperty(String id, IElementType propertyMarkerElement, IElementType propertyValueMarkerElement,
                             IssSetupPropertyClassifier classifier, String descriptionKey, boolean required) {
        this(id, propertyMarkerElement, propertyValueMarkerElement, classifier, descriptionKey, IssValueType.DirectMultiple, false);
    }

    private IssSetupProperty(String id, IElementType propertyMarkerElement, IElementType propertyValueMarkerElement,
                             IssSetupPropertyClassifier classifier, String descriptionKey, IssValueType valueType, boolean required) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.classifier = classifier;
        this.propertyMarkerElement = propertyMarkerElement;
        this.propertyValueMarkerElement = propertyValueMarkerElement;
        this.required = required;
        this.valueType = valueType;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }


    @NotNull
    @Override
    public IElementType getPropertyMarkerElement() {
        return propertyMarkerElement;
    }

    @Override
    public IElementType getPropertyValueMarkerElement() {
        return propertyValueMarkerElement;
    }

    @NotNull
    @Override
    public IssValueType getValueType() {
        return valueType;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isDeprecated() {
        return classifier == IssSetupPropertyClassifier.Obsolete;
    }



    @NotNull
    public IssSetupPropertyClassifier getClassifier() {
        return classifier;
    }
}

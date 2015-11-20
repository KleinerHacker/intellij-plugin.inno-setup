package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 22.12.2014.
 */
public enum IssSetupProperty implements IssStandardPropertyIdentifier {
    //Compiler
    Compression("Compression", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    CompressionThreads("CompressionThreads", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    DiskClusterSize("DisClusterSize", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    DiskSliceSize("DiskSliceSize", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    DiskSpanning("DiskSpanning", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    Encryption("Encryption", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    InternalCompressLevel("InternalCompressLevel", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    LZMAAlgorithm("LZMAAlgorithm", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    LZMABlockSize("LZMABlockSize", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    LZMADictionarySize("LZMADictionarySize", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    LZMAMatchFinder("LZMAMatchFinder", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    LZMANumBlockThreads("LZMANumBlockThreads", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    LZMANumFastBytes("LZMANumFastBytes", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    LZMAUseSeparateProcess("LZMAUseSeparateProcess", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    MergeDuplicateFiles("MergeDuplicateFiles", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    Output("Output", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    OutputBaseFilename("OutputBaseFilename", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    OutputDir("OutputDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    OutputManifestFile("OutputManifestFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    ReserveBytes("ReserveBytes", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    SignedUninstaller("SignedUninstaller", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    SignedUninstallerDir("SignedUninstallerDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    SignTool("SignTool", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    SlicesPerDisk("SlicesPerDisk", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    SolidCompression("SolidCompression", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    SourceDir("SourceDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    TerminalServicesAware("TerminalServicesAware", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    UseSetupLdr("UseSetupLdr", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    VersionInfoCompany("VersionInfoCompany", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    VersionInfoCopyright("VersionInfoCopyright", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    VersionInfoDescription("VersionInfoDescription", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    VersionInfoProductName("VersionInfoProductName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    VersionInfoProductTextVersion("VersionInfoProductTextVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    VersionInfoProductVersion("VersionInfoProductVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    VersionInfoTextVersion("VersionInfoTextVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    VersionInfoVersion("VersionInfoVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COMPILER),
    // Installer
    AppName("AppName", IssMarkerFactory.SetupSection.PROPERTY_APP_NAME, IssMarkerFactory.SetupSection.PROPERTY_APP_NAME_VALUE, Constants.TYPE_INSTALLER, true),
    AppVersion("AppVersion", IssMarkerFactory.SetupSection.PROPERTY_APP_VERSION, IssMarkerFactory.SetupSection.PROPERTY_APP_VERSION_VALUE, Constants.TYPE_INSTALLER, true),
    AllowCancelDuringInstall("AllowCancelDuringInstall", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AllowNetworkDrive("AllowNetworkDrive", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AllowNoIcons("AllowNoIcons", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AllowRootDirectory("AllowRootDirectory", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AllowUNCPath("AllowUNCPath", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AlwaysRestart("AlwaysRestart", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AlwaysShowComponentsList("AlwaysShowComponentsList", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AlwaysShowDirOnReadyPage("AlwaysShowDirOnReadyPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AlwaysShowGroupOnReadyPage("AlwaysShowGroupOnReadyPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AlwaysUsePersonalGroup("AlwaysUsePersonalGroup", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppendDefaultDirName("AppendDefaultDirName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppendDefaultGroupName("AppendDefaultGroupName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppComments("AppComments", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppContact("AppContact", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppId("AppId", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppModifyPath("AppModifyPath", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppMutex("AppMutex", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppPublisher("AppPublisher", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppPublisherURL("AppPublisherURL", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppReadmeFile("AppReadmeFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppSupportPhone("AppSupportPhone", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppSupportURL("AppSupportURL", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppUpdatesURL("AppUpdatesURL", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    AppVerName("AppVerName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    ArchitecturesAllowed("ArchitecturesAllowed", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    ArchitecturesInstallIn64BitMode("ArchitecturesInstallIn64BitMode", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    ChangesAssociations("ChangesAssociations", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    ChangesEnvironment("ChangesEnvironment", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    CloseApplications("CloseApplications", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    CloseApplicationsFilter("CloseApplicationsFilter", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    CreateAppDir("CreateAppDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    CreateUninstallRegKey("CreateUninstallRegKey", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DefaultDialogFontName("DefaultDialogFontName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DefaultDirName("DefaultDirName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DefaultGroupName("DefaultGroupName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DefaultUserInfoName("DefaultUserInfoName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DefaultUserInfoOrg("DefaultUserInfoOrg", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DefaultUserInfoSerial("DefaultUserInfoSerial", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DirExistsWarning("DirExistsWarning", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DisableDirPage("DisableDirPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DisableFinishedPage("DisableFinishedPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DisableProgramGroupPage("DisableProgramGroupPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DisableReadyMemo("DisableReadyMemo", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DisableReadyPage("DisableReadyPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DisableStartupPrompt("DisableStartupPrompt", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    DisableWelcomePage("DisableWelcomePage", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    EnableDirDoesntExistWarning("EnableDirDoesntExistWarning", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    ExtraDiskSpaceRequired("ExtraDiskSpaceRequired", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    InfoAfterFile("InfoAfterFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    InfoBeforeFile("InfoBeforeFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    LanguageDetectionMethod("LanguageDetectionMethod", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    LicenseFile("LicenseFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    MinVersion("MinVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    OnlyBelowVersion("OnlyBelowVersion", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    Password("Password", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    PrivilegesRequired("PrivilegesRequired", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    RestartApplications("RestartApplications", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    RestartIfNeededByRun("RestartIfNeededByRun", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    SetupLogging("SetupLogging", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    ShowLanguageDialog("ShowLanguageDialog", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    ShowUndisplayableLanguages("ShowUndisplayableLanguages", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    TimeStampRounding("TimeStampRounding", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    TimeStampsInUTC("TimeStampsInUTC", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    TouchDate("TouchDate", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    TouchTime("TouchTime", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    Uninstallable("Uninstallable", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UninstallDisplayIcon("UninstallDisplayIcon", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UninstallDisplayName("UninstallDisplayName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UninstallDisplaySize("UninstallDisplaySize", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UninstallFilesDir("UninstallFilesDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UninstallLogMode("UninstallLogMode", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UninstallRestartComputer("UninstallRestartComputer", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UpdateUninstallLogAppName("UpdateUninstallLogAppName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UsePreviousAppDir("UsePreviousAppDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UsePreviousGroup("UsePreviousGroup", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UsePreviousLanguage("UsePreviousLanguage", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UsePreviousSetupType("UsePreviousSetupType", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UsePreviousTasks("UsePreviousTasks", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UsePreviousUserInfo("UsePreviousUserInfo", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    UserInfoPage("UserInfoPage", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_INSTALLER),
    //Cosmetic
    AppCopyright("AppCopyright", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    BackColor("BackColor", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    BackColor2("BackColor2", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    BackColorDirection("BackColorDirection", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    BackSolid("BackSolid", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    FlatComponentsList("FlatComponentsList", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    SetupIconFile("SetupIconFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    ShowComponentSizes("ShowComponentSizes", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    ShowTasksTreeLines("ShowTasksTreeLines", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    WindowShowCaption("WindowShowCaption", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    WindowStartMaximized("WindowStartMaximized", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    WindowResizable("WindowResizable", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    WindowVisible("WindowVisible", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    WizardImageBackColor("WizardImageBackColor", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    WizardImageFile("WizardImageFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    WizardImageStretch("WizardImageStretch", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    WizardSmallImageFile("WizardSmallImageFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_COSMETIC),
    //Obsolete
    AlwaysCreateUninstallIcon("AlwaysCreateUninstallIcon", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_OBSOLETE),
    DisableAppendDir("DisableAppendDir", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_OBSOLETE),
    DontMergeDuplicateFiles("DontMergeDuplicateFiles", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_OBSOLETE),
    MessagesFile("MessagesFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_OBSOLETE),
    UninstallIconFile("UninstallIconFile", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_OBSOLETE),
    UninstallIconName("UninstallIconName", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_OBSOLETE),
    UninstallStyle("UninstallStyle", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_OBSOLETE),
    WizardSmallImageBackColor("WizardSmallImageBackColor", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_OBSOLETE),
    WizardStyle("WizardStyle", IssMarkerFactory.PROPERTY_UNKNOWN, null, Constants.TYPE_OBSOLETE);

    @Override
    public String toString() {
        return id;
    }

    private static final class Constants {
        private static final String TYPE_COMPILER = "Compiler-related";
        private static final String TYPE_INSTALLER = "Installer-related";
        private static final String TYPE_COSMETIC = "Cosmetic";
        private static final String TYPE_OBSOLETE = "Obsolete";
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

    private final String id, type;
    private final IElementType propertyMarkerElement, propertyValueMarkerElement;
    private final boolean required;

    private IssSetupProperty(String id, IElementType propertyMarkerElement, IElementType propertyValueMarkerElement, String type) {
        this(id, propertyMarkerElement, propertyValueMarkerElement, type, false);
    }

    private IssSetupProperty(String id, IElementType propertyMarkerElement, IElementType propertyValueMarkerElement, String type, boolean required) {
        this.id = id;
        this.type = type;
        this.propertyMarkerElement = propertyMarkerElement;
        this.propertyValueMarkerElement = propertyValueMarkerElement;
        this.required = required;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getDescriptionKey() {
        return null;
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

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isDeprecated() {
        return type.equals(Constants.TYPE_OBSOLETE);
    }

    public String getType() {
        return type;
    }


}

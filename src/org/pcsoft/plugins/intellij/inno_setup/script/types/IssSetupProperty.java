package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

/**
 * Created by Christoph on 22.12.2014.
 */
public enum IssSetupProperty implements IssSectionIdentifier {
    //Compiler
    Compression("Compression", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    CompressionThreads("CompressionThreads", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    DiskClusterSize("DisClusterSize", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    DiskSliceSize("DiskSliceSize", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    DiskSpanning("DiskSpanning", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    Encryption("Encryption", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    InternalCompressLevel("InternalCompressLevel", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    LZMAAlgorithm("LZMAAlgorithm", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    LZMABlockSize("LZMABlockSize", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    LZMADictionarySize("LZMADictionarySize", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    LZMAMatchFinder("LZMAMatchFinder", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    LZMANumBlockThreads("LZMANumBlockThreads", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    LZMANumFastBytes("LZMANumFastBytes", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    LZMAUseSeparateProcess("LZMAUseSeparateProcess", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    MergeDuplicateFiles("MergeDuplicateFiles", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    Output("Output", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    OutputBaseFilename("OutputBaseFilename", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    OutputDir("OutputDir", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    OutputManifestFile("OutputManifestFile", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    ReserveBytes("ReserveBytes", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    SignedUninstaller("SignedUninstaller", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    SignedUninstallerDir("SignedUninstallerDir", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    SignTool("SignTool", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    SlicesPerDisk("SlicesPerDisk", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    SolidCompression("SolidCompression", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    SourceDir("SourceDir", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    TerminalServicesAware("TerminalServicesAware", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    UseSetupLdr("UseSetupLdr", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    VersionInfoCompany("VersionInfoCompany", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    VersionInfoCopyright("VersionInfoCopyright", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    VersionInfoDescription("VersionInfoDescription", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    VersionInfoProductName("VersionInfoProductName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    VersionInfoProductTextVersion("VersionInfoProductTextVersion", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    VersionInfoProductVersion("VersionInfoProductVersion", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    VersionInfoTextVersion("VersionInfoTextVersion", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    VersionInfoVersion("VersionInfoVersion", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COMPILER),
    // Installer
    AppName("AppName", IssMarkerFactory.SetupSection.ITEM_APP_NAME, Constants.TYPE_INSTALLER),
    AppVersion("AppVersion", IssMarkerFactory.SetupSection.ITEM_APP_VERSION, Constants.TYPE_INSTALLER),
    AllowCancelDuringInstall("AllowCancelDuringInstall", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AllowNetworkDrive("AllowNetworkDrive", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AllowNoIcons("AllowNoIcons", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AllowRootDirectory("AllowRootDirectory", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AllowUNCPath("AllowUNCPath", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AlwaysRestart("AlwaysRestart", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AlwaysShowComponentsList("AlwaysShowComponentsList", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AlwaysShowDirOnReadyPage("AlwaysShowDirOnReadyPage", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AlwaysShowGroupOnReadyPage("AlwaysShowGroupOnReadyPage", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AlwaysUsePersonalGroup("AlwaysUsePersonalGroup", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppendDefaultDirName("AppendDefaultDirName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppendDefaultGroupName("AppendDefaultGroupName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppComments("AppComments", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppContact("AppContact", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppId("AppId", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppModifyPath("AppModifyPath", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppMutex("AppMutex", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppPublisher("AppPublisher", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppPublisherURL("AppPublisherURL", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppReadmeFile("AppReadmeFile", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppSupportPhone("AppSupportPhone", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppSupportURL("AppSupportURL", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppUpdatesURL("AppUpdatesURL", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    AppVerName("AppVerName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    ArchitecturesAllowed("ArchitecturesAllowed", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    ArchitecturesInstallIn64BitMode("ArchitecturesInstallIn64BitMode", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    ChangesAssociations("ChangesAssociations", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    ChangesEnvironment("ChangesEnvironment", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    CloseApplications("CloseApplications", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    CloseApplicationsFilter("CloseApplicationsFilter", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    CreateAppDir("CreateAppDir", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    CreateUninstallRegKey("CreateUninstallRegKey", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DefaultDialogFontName("DefaultDialogFontName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DefaultDirName("DefaultDirName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DefaultGroupName("DefaultGroupName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DefaultUserInfoName("DefaultUserInfoName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DefaultUserInfoOrg("DefaultUserInfoOrg", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DefaultUserInfoSerial("DefaultUserInfoSerial", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DirExistsWarning("DirExistsWarning", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DisableDirPage("DisableDirPage", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DisableFinishedPage("DisableFinishedPage", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DisableProgramGroupPage("DisableProgramGroupPage", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DisableReadyMemo("DisableReadyMemo", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DisableReadyPage("DisableReadyPage", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DisableStartupPrompt("DisableStartupPrompt", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    DisableWelcomePage("DisableWelcomePage", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    EnableDirDoesntExistWarning("EnableDirDoesntExistWarning", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    ExtraDiskSpaceRequired("ExtraDiskSpaceRequired", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    InfoAfterFile("InfoAfterFile", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    InfoBeforeFile("InfoBeforeFile", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    LanguageDetectionMethod("LanguageDetectionMethod", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    LicenseFile("LicenseFile", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    MinVersion("MinVersion", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    OnlyBelowVersion("OnlyBelowVersion", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    Password("Password", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    PrivilegesRequired("PrivilegesRequired", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    RestartApplications("RestartApplications", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    RestartIfNeededByRun("RestartIfNeededByRun", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    SetupLogging("SetupLogging", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    ShowLanguageDialog("ShowLanguageDialog", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    ShowUndisplayableLanguages("ShowUndisplayableLanguages", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    TimeStampRounding("TimeStampRounding", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    TimeStampsInUTC("TimeStampsInUTC", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    TouchDate("TouchDate", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    TouchTime("TouchTime", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    Uninstallable("Uninstallable", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UninstallDisplayIcon("UninstallDisplayIcon", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UninstallDisplayName("UninstallDisplayName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UninstallDisplaySize("UninstallDisplaySize", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UninstallFilesDir("UninstallFilesDir", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UninstallLogMode("UninstallLogMode", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UninstallRestartComputer("UninstallRestartComputer", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UpdateUninstallLogAppName("UpdateUninstallLogAppName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UsePreviousAppDir("UsePreviousAppDir", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UsePreviousGroup("UsePreviousGroup", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UsePreviousLanguage("UsePreviousLanguage", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UsePreviousSetupType("UsePreviousSetupType", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UsePreviousTasks("UsePreviousTasks", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UsePreviousUserInfo("UsePreviousUserInfo", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    UserInfoPage("UserInfoPage", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_INSTALLER),
    //Cosmetic
    AppCopyright("AppCopyright", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    BackColor("BackColor", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    BackColor2("BackColor2", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    BackColorDirection("BackColorDirection", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    BackSolid("BackSolid", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    FlatComponentsList("FlatComponentsList", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    SetupIconFile("SetupIconFile", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    ShowComponentSizes("ShowComponentSizes", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    ShowTasksTreeLines("ShowTasksTreeLines", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    WindowShowCaption("WindowShowCaption", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    WindowStartMaximized("WindowStartMaximized", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    WindowResizable("WindowResizable", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    WindowVisible("WindowVisible", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    WizardImageBackColor("WizardImageBackColor", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    WizardImageFile("WizardImageFile", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    WizardImageStretch("WizardImageStretch", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    WizardSmallImageFile("WizardSmallImageFile", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_COSMETIC),
    //Obsolete
    AlwaysCreateUninstallIcon("AlwaysCreateUninstallIcon", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_OBSOLETE),
    DisableAppendDir("DisableAppendDir", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_OBSOLETE),
    DontMergeDuplicateFiles("DontMergeDuplicateFiles", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_OBSOLETE),
    MessagesFile("MessagesFile", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_OBSOLETE),
    UninstallIconFile("UninstallIconFile", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_OBSOLETE),
    UninstallIconName("UninstallIconName", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_OBSOLETE),
    UninstallStyle("UninstallStyle", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_OBSOLETE),
    WizardSmallImageBackColor("WizardSmallImageBackColor", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_OBSOLETE),
    WizardStyle("WizardStyle", IssMarkerFactory.ITEM_DEFAULT, Constants.TYPE_OBSOLETE);

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
        return IssSectionIdentifier.fromId(id, IssSetupProperty.class);
    }

    public static IElementType getMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssSetupProperty.class);
    }

    private final String id, type;
    private final IElementType itemMarkerElement;

    private IssSetupProperty(String id, IElementType itemMarkerElement, String type) {
        this.id = id;
        this.type = type;
        this.itemMarkerElement = itemMarkerElement;
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
    public IElementType getItemMarkerElement() {
        return itemMarkerElement;
    }

    @Override
    public boolean isDeprecated() {
        return type.equals(Constants.TYPE_OBSOLETE);
    }

    public String getType() {
        return type;
    }


}

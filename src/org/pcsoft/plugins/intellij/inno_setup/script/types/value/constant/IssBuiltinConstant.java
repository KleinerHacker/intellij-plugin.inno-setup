package org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssBuiltinConstant implements IssPropertyValue {
    Application("app", "constant.builtin.app", IssBuiltinConstantType.Directory),
    Windows("win", "constant.builtin.win", IssBuiltinConstantType.Directory),
    System32("sys", "constant.builtin.sys", IssBuiltinConstantType.Directory),
    System64("sysWOW64", "constant.builtin.syswow64", IssBuiltinConstantType.Directory),
    Source("src", "constant.builtin.src", IssBuiltinConstantType.Directory),
    ProgramFiles("pf", "constant.builtin.pf", IssBuiltinConstantType.Directory),
    ProgramFiles32("pf32", "constant.builtin.pf32", IssBuiltinConstantType.Directory),
    ProgramFiles64("pf64", "constant.builtin.pf64", IssBuiltinConstantType.Directory),
    CommonFiles("cf", "constant.builtin.cf", IssBuiltinConstantType.Directory),
    CommonFiles32("cf32", "constant.builtin.cf32", IssBuiltinConstantType.Directory),
    CommonFiles64("cf64", "constant.builtin.cf64", IssBuiltinConstantType.Directory),
    Temp("tmp", "constant.builtin.tmp", IssBuiltinConstantType.Directory),
    Fonts("fonts", "constant.builtin.fonts", IssBuiltinConstantType.Directory),
    DAO("dao", "constant.builtin.dao", IssBuiltinConstantType.Directory),
    DotNET11("dotNET11", "constant.builtin.dontnet11", IssBuiltinConstantType.Directory),
    DotNET20("dotNET20", "constant.builtin.dontnet20", IssBuiltinConstantType.Directory),
    DotNET2032("dotNET2032", "constant.builtin.dontnet2032", IssBuiltinConstantType.Directory),
    DotNET2064("dotNET2064", "constant.builtin.dontnet2064", IssBuiltinConstantType.Directory),
    DotNET40("dotNET40", "constant.builtin.dontnet40", IssBuiltinConstantType.Directory),
    DotNET4032("dotNET4032", "constant.builtin.dontnet4032", IssBuiltinConstantType.Directory),
    DotNET4064("dotNET4064", "constant.builtin.dontnet4064", IssBuiltinConstantType.Directory),
    /****************************************************************************************/
    Group("group", "constant.builtin.group", IssBuiltinConstantType.ShellFolder),
    LocalAppData("localAppData", "constant.builtin.localappdata", IssBuiltinConstantType.ShellFolder),
    SendTo("sendTo", "constant.builtin.sendto", IssBuiltinConstantType.ShellFolder),
    UserAppData("userAppData", "constant.builtin.userappdata", IssBuiltinConstantType.ShellFolder),
    CommonAppData("commonAppData", "constant.builtin.commonappdata", IssBuiltinConstantType.ShellFolder),
    UserCommonFile("userCF", "constant.builtin.usercf", IssBuiltinConstantType.ShellFolder),
    UserDesktop("userDesktop", "constant.builtin.userdesktop", IssBuiltinConstantType.ShellFolder),
    CommonDesktop("commonDesktop", "constant.builtin.commondesktop", IssBuiltinConstantType.ShellFolder),
    UserDocument("userDocs", "constant.builtin.userdocs", IssBuiltinConstantType.ShellFolder),
    CommonDocument("commonDocs", "constant.builtin.commondocs", IssBuiltinConstantType.ShellFolder),
    UserFavorites("userFavorites", "constant.builtin.userfavorites", IssBuiltinConstantType.ShellFolder),
    CommonFavorites("commonFavorites", "constant.builtin.commonfavorites", IssBuiltinConstantType.ShellFolder),
    UserProgramFile("userPF", "constant.builtin.userpf", IssBuiltinConstantType.ShellFolder),
    UserPrograms("userPrograms", "constant.builtin.userprograms", IssBuiltinConstantType.ShellFolder),
    CommonPrograms("commonPrograms", "constant.builtin.commonprograms", IssBuiltinConstantType.ShellFolder),
    UserStartMenu("userStartMenu", "constant.builtin.userstartmenu", IssBuiltinConstantType.ShellFolder),
    CommonStartMenu("commonStartMenu", "constant.builtin.commonstartmenu", IssBuiltinConstantType.ShellFolder),
    UserStartup("userStartup", "constant.builtin.userstartup", IssBuiltinConstantType.ShellFolder),
    CommonStartup("commonStartup", "constant.builtin.commonstartup", IssBuiltinConstantType.ShellFolder),
    UserTemplates("userTemplates", "constant.builtin.usertemplates", IssBuiltinConstantType.ShellFolder),
    CommonTemplates("commonTemplates", "constant.builtin.commontemplates", IssBuiltinConstantType.ShellFolder),
    /****************************************************************************************/
    CommandLineInterpreter("cmd", "constant.builtin.cmd"),
    ComputerName("computerName", "constant.builtin.computername"),
    GroupName("groupName", "constant.builtin.groupname"),
    HWND("hwnd", "constant.builtin.hwnd"),
    WizardHWND("wizardHWND", "constant.builtin.wizardhwnd"),
    Language("language", "constant.builtin.language"),
    SourceEXE("srcEXE", "constant.builtin.srcexe"),
    UninstallEXE("uninstallEXE", "constant.builtin.uninstallexe"),
    SystemUserInfoName("sysUserInfoName", "constant.builtin.sysuserinfoname"),
    SystemUserInfoOrganization("sysUserInfoOrg", "constant.builtin.sysuserinfoorg"),
    UserInfoName("userInfoName", "constant.builtin.userinfoname"),
    UserInfoOrganization("userInfoOrg", "constant.builtin.userinfoorg"),
    UserInfoSerial("userInfoSerial", "constant.builtin.userinfoserial"),
    Username("username", "constant.builtin.username"),
    Log("log", "constant.builtin.log"),
    ;

    public static IssBuiltinConstant fromId(final String id) {
        return IssPropertyValue.findById(id, IssBuiltinConstant.class);
    }

    private final String id, descriptionKey;
    private final IssBuiltinConstantType type;
    private final boolean deprecated;

    private IssBuiltinConstant(String id, String descriptionKey) {
        this(id, descriptionKey, IssBuiltinConstantType.Other);
    }

    private IssBuiltinConstant(String id, String descriptionKey, IssBuiltinConstantType type) {
        this(id, descriptionKey, type, false);
    }

    private IssBuiltinConstant(String id, String descriptionKey, IssBuiltinConstantType type, boolean deprecated) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.type = type;
        this.deprecated = deprecated;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    @NotNull
    public IssBuiltinConstantType getType() {
        return type;
    }
}

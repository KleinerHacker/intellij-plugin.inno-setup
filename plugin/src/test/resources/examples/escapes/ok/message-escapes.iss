; Message escapes: %n (line break), %% (percent sign), %1 (argument) and %7d (a "}" inside a constant argument).
[Setup]
AppName=EscapeMessages
AppVersion=1.0
AppComments={cm:Greeting,a%7db}
DefaultDirName={tmp}\EscapeMessages
CreateAppDir=no
Uninstallable=no
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
OutputBaseFilename=escape-messages

[CustomMessages]
Greeting=Hello%nWelcome to %1 (100%%)

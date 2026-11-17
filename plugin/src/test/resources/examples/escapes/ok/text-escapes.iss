; Escapes in plain values: a lone "}", "{{" inside a text and a doubled quote inside a quoted value.
[Setup]
AppName=EscapeText
AppVersion=1.0
AppComments=a}b {{c "d"
AppPublisher="My ""Quoted"" Company"
DefaultDirName={tmp}\EscapeText
CreateAppDir=no
Uninstallable=no
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
OutputBaseFilename=escape-text

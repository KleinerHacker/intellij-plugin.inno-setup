; A constant that is never closed ("{tmp" has no "}"): the compiler must reject it, and the plugin reports
; "Unclosed constant" for the same line.
[Setup]
AppName=EscapeUnclosed
AppVersion=1.0
DefaultDirName={tmp\EscapeUnclosed
CreateAppDir=no
Uninstallable=no
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
OutputBaseFilename=escape-unclosed

; Escapes inside "#" directives: a doubled quote in a double-quoted and in a single-quoted ISPP string.
#define Quoted "say ""hi"""
#define Single 'it''s'

[Setup]
AppName=EscapeDirective
AppVersion=1.0
AppComments={#Single}
AppPublisher={#Quoted}
DefaultDirName={tmp}\EscapeDirective
CreateAppDir=no
Uninstallable=no
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
OutputBaseFilename=escape-directive

; The form of ticket #63: "{{" becomes "{" but a closing "}" is never escaped, so the second "}" stays.
; The compiler accepts it (the plugin only warns); the setup proves the result (see guid-double-brace.expected).
[Setup]
AppId={{012245-13224-234235}}
AppName=EscapeGuidDouble
AppVersion=1.0
DefaultDirName={tmp}\EscapeGuidDouble
CreateAppDir=no
Uninstallable=no
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
OutputBaseFilename=escape-guid-double

[Code]
function InitializeSetup(): Boolean;
begin
  SaveStringToFile(ExpandConstant('{param:ResultFile}'), ExpandConstant('{#SetupSetting("AppId")}'), False);
  Result := False;
end;

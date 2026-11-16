; The documented GUID form: "{{" is the escape of a literal "{", the closing "}" is written once.
; The setup writes its expanded AppId into the file given by /ResultFile=... and exits (see guid-single-brace.expected).
[Setup]
AppId={{A1B2C3D4-0001-4000-8000-000000000001}
AppName=EscapeGuidSingle
AppVersion=1.0
DefaultDirName={tmp}\EscapeGuidSingle
CreateAppDir=no
Uninstallable=no
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
OutputBaseFilename=escape-guid-single

[Code]
function InitializeSetup(): Boolean;
begin
  SaveStringToFile(ExpandConstant('{param:ResultFile}'), ExpandConstant('{#SetupSetting("AppId")}'), False);
  Result := False;
end;

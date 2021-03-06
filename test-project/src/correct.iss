; -- Components.iss --
; Demonstrates a components-based installation.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

#define MyName "hello" + "world" - 1234

[Setup]
AppName=My Program
AppVersion=1.5
DefaultDirName={pf}\My Program
DefaultGroupName=My Program
UninstallDisplayIcon={app}\MyProg.exe
OutputDir=userdocs:Inno Setup Examples Output
AlwaysCreateUninstallIcon=fdff

[Types]
Name: "full"; Description: "Full installation"
Name: "compact"; Description: "Compact installation"
Name: "custom"; Description: "Custom installation"; Flags: iscustom

[Components]
Name: "program"; Description: "Program Files"; Types: full compact custom1; Flags: fixed
Name: "help"; Description: "Help File"; Types: full;
Name: "readme"; Description: "Readme File"; Types: full
Name: "readme\en"; Description: "English"; Flags: exclusive
Name: "readme\de"; Description: "German"; Flags: exclusive; MinVersion: 1.9

[Files]
Source: "MyProg.exe"; DestDir: "{app}"; Components: program1; Tasks: bub bub1
Source: "MyProg.chm"; DestDir: "{app}"; Components: help
Source: "Readme.txt"; DestDir: "{app}"; Components: readme\en; Flags: isreadme
Source: "Readme-German.txt"; DestName: "Liesmich.txt"; DestDir: "{app}"; Components: readme\de; Flags: isreadme

[Icons]
Name: "{group}\My Program"; Filename: "{app}\MyProg.exe"

[Tasks]
Name: "bub"; Description: "kflöd"

; -- Example1.iss --
; Demonstrates copying 3 files and creating an icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

[Setup]
AppName=My Program
AppVersion=1.5
DefaultDirName={pf}\My Program
DefaultGroupName=My Program
UninstallDisplayIcon={app}\MyProg.exe
Compression=lzma2
SolidCompression=yes
OutputDir=userdocs:Inno Setup Examples Output

[Types]
Description: "Hello"; Name: MyType

[Files]
Source: "MyProg.exe"; DestDir: "{app}"
Source: "MyProg.chm"; DestDir: "{app}"
Source: "Readme.txt"; DestDir: "{app}"; Flags: isreadme

[Dirs]
Name: "Hallo"

[Icons]
Name: "{group}\My Program"; Filename: "{app}\MyProg.exe"

[Languages]
Name: "en"; MessageFile: "djslk"

[LangOptions]
LanguageName=English
LanguageID=$4080
LanguageCodePage=10

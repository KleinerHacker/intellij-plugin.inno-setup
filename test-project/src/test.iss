; -- Example1.iss --
; Demonstrates copying 3 files and creating an icon.

; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!

#include hhh gh "göf"

[Setup]
AppName={pf}\My Program
AppVersion=1.5
DefaultDirName={pf}\My Program\{%cbf}
DefaultGroupName=My Program
UninstallDisplayIcon={app}\MyProg.exe
Compression=lzma2
SolidCompression=yes
OutputDir=userdocs:Inno Setup Examples Output
BackColor=$00FF
DisableDirPage=auto1
AllowNoIcon:yes; ASLRCompatible: no

[Files]
Source: 10; DestDir: "{sys}";
Source: "MyProg.chm"; DestDir: "{app}"
Source: "Readme.txt"; DestDir: "{app}"; Flags: isReadMe;
Flags=setNtfsCompression isReadMe

[Dirs]
Name: ""; Flags: setNtfsCompression; Attribs: readOnly hidden
Flags:  deleteAfterInstall hello deleteAfterInstall;

[Tasks]
GroupDescription: "a"

[Components]
Types: a;

[Types]
Description: "Hello"; Name: bla; Flags: isCustom

[Icons]
Name: "{group}\My Program"; Filename: "{app}\MyProg.exe"

[INI]
Section: "Hallo"

[Registry]
Root: HKCU; Subkey: "ff"; ValueType: expandSZ; ValueData: $00FF

[InstallDelete]
Type: files; Name: "{app:{sys:sd|{win}},aa|ss}"

[UninstallDelete]
Type: dirIfEmpty; Name: "{pup}"

[Run]
Filename: "abc";

[UninstallRun]
Filename: "bbb"

[Languages]
Name: "de"; MessageFile: "compiler:Default.isl"

[LangOptions]
LanguageName=English
LanguageID=$40FF80
RightToLeft=yes

[Messages]
de.bla=dis

[CustomMessages]
de.blu=fdu

[Code]
d d dvs sd oib s bsdo

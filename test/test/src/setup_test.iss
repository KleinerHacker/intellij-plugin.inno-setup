[SETUP]
AppName = Test
Compression = lzma
Compression = zip

[Tasks]
Name: testTask; Description: "My Test Task"; Components: testComponent bla testComponent; Flags: CheckedOnce bla CheckedOnce; Languages: de bla en de; MinVersion: 3.5
Name: doubleTask; Description: "";
Name: doubleTask; Description: "Hallo";
Flags: CheckableAlone

[TYPES]
Name: testType; Description: "Bla"; Flags: IsCustom bla IsCustom;
Name: doubleType; Description: ""
Name: doubleType; Description: "Hallo"
Flags: IsCustom

[Components]
; My Test Component
Name: testComponent; Description: "B{app}la"; Types: testType bla testType; ExtraDiskSpaceRequired: -100; Flags: CheckableAlone bla CheckableAlone
Name: doubleComponent; Description: "";
Name: doubleComponent; Description: "Hallo";
Flags: Fixed

[Files]
Source: "testFile"; DestDir: "testDest"; Flags: UninsRemoveReadOnly 32bit 32bit bla; Components: testComponent bla testComponent; CopyMode: AlwaysOverwrite; Attribs: Hidden ReadOnly Hidden bla; Permissions: Users-Modify system-full Users-Modify bla; Tasks: testTask bla testTask; ExternalSize: -100
Source: "doubleFile"; DestDir: "doubleDest";
Source: "doubleFile"; DestDir: "doubleDest";
Source: "doubleFile"; DestDir: "newDest";
Flags: 32bit

[Dirs]
Name: "tesDir"; Attribs: ReadOnly Hidden Hidden bla; Permissions: Admins-Full Users-Modify Admins-Full bla; Flags: bla DeleteAfterInstall SetNTFSCompression SetNTFSCompression bla; Tasks: testTask bla testTask; Components: testComponent bla testComponent
Name: "doubleDir";
Name: "doubleDir";
Flags: DeleteAfterInstall

[Icons]
Name: "testIcon"; Filename: "dfrfr"; Flags: FolderShortCut CloseOnExit FolderShortCut bla; Tasks: bla testTask testTask; Components: testComponent bla testComponent
Name: "doubleIcon"; Filename: "dhus";
Name: "doubleIcon"; Filename: "dcls";
Flags: CloseOnExit

[Run]
Filename: "testRun"; StatusMsg: "Hallo"; Flags: SkipIfNotSilent 32Bit 64Bit SkipIfNotSilent bla; Components: testComponent bla testComponent; Tasks: testTask bla testTask
Filename: "doubleRun";
Filename: "doubleRun";
Flags: 64Bit

[UninstallRun]
Filename: "testUninstallRun"; Flags: SkipIfDoesntExist 64Bit SkipIfDoesntExist bla; Components: testComponent bla testComponent; Tasks: testTask bla testTask
Filename: "doubleUninstallRun";
Filename: "doubleUninstallRun";
Flags: 64Bit

[INI]
Filename: "testINI"; Section: "test"; Flags: UninsDeleteEntry bla UninsDeleteEntry; Components: testComponent bla testComponent; Tasks: testTask bla testTask
Filename: "doubleINI"; Section: "single";
Filename: "doubleINI"; Section: "single"; Key: "abc";
Filename: "doubleINI"; Section: "double";
Filename: "doubleINI"; Section: "double";
Flags: UninsDeleteSection
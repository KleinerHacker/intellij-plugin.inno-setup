; [SETUP]
; AppName=Test
; Compression=lzma
; Compression=zip

[Tasks]
Name: testTask; Description: "My Test Task"; Components: testComponent unknown testComponent
Name: doubleTask; Description: "";
Name: doubleTask; Description: "Hallo";
Flags: CheckableAlone

[TYPES]
Name: bla1; Description: "Bla"; Flags: IsCustom unknown IsCustom;
Name: doubleType; Description: ""
Name: doubleType; Description: "Hallo"
Flags: IsCustom

[Components]
; My Test Component
Name: testComponent; Description: "B{app}la"; Types: bla1 blub bla1; ExtraDiskSpaceRequired: -100
Name: doubleComponent; Description: "";
Name: doubleComponent; Description: "Hallo";
Flags: Fixed

[Files]
Source: "testFile"; DestDir: "testDest"; Flags: UninsRemoveReadOnly 32bit 32bit unknown; Components: testComponent testComponent; CopyMode: AlwaysOverwrite; Attribs: Hidden ReadOnly Hidden; Permissions: Users-Modify system-full Users-Modify; Tasks: testTask unknown testTask; ExternalSize: -100
Source: "doubleFile"; DestDir: "doubleDest";
Source: "doubleFile"; DestDir: "doubleDest";
Source: "doubleFile"; DestDir: "newDest";
Flags: 32bit

[Dirs]
Name: "tesDir"; Attribs: ReadOnly Hidden Hidden; Permissions: Admins-Full Users-Modify Admins-Full; Flags: DeleteAfterInstall SetNTFSCompression SetNTFSCompression unknown; Tasks: testTask unknown testTask; Components: testComponent unknown testComponent
Name: "doubleDir";
Name: "doubleDir";
Flags: DeleteAfterInstall

[Icons]
Name: "testIcon"; Filename: "dfrfr"; Flags: FolderShortCut CloseOnExit FolderShortCut; Tasks: bla testTask testTask; Components: testComponent bla testComponent
Name: "doubleIcon"; Filename: "dhus";
Name: "doubleIcon"; Filename: "dcls";
Flags: CloseOnExit

[Run]
Filename: "testRun"; StatusMsg: "Hallo"; Flags: SkipIfNotSilent 32Bit 64Bit SkipIfNotSilent; Components: testComponent bla testComponent; Tasks: testTask bla testTask
Filename: "doubleRun";
Filename: "doubleRun";
Flags: 64Bit
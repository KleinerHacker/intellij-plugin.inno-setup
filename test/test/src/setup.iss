[SETUP]
AppName=Test
Compression=lzma
Compression=zip

[Tasks]
Name: testTask; Description: "My Test Task"; Components: testComponent unknown testComponent
Name: doubleTask; Description: "";
Name: doubleTask; Description: "Hallo";

[TYPES]
Name: bla; Description: "Bla"; Flags: IsCustom unknown IsCustom ;
Name: doubleType; Description: ""
Name: doubleType; Description: "Hallo"

[Components]
Name: testComponent; Description: "Bla"; Types: bla blub bla;
Name: doubleComponent; Description: "";
Name: doubleComponent; Description: "Hallo";

[Files]
Source: ""; DestDir: ""; Flags: UninsRemoveReadOnly 32bit 32bit unknown; Components: testComponent testComponent; CopyMode: AlwaysOverwrite; Attribs: Hidden ReadOnly Hidden; Permissions: Users-Modify system-full Users-Modify; Tasks: testTask unknown testTask
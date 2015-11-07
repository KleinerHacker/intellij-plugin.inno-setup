[SETUP]
Compression=lzma

[Tasks]
Name: testTask; Description: "My Test Task"; Components: testComponent

[TYPES]
Name: bla; Description: "Bla"; Flags: IsCustom;

[Components]
Name: testComponent; Description: "Bla"; Types: bla

[Files]
Source: ""; DestDir: ""; Flags: UninsRemoveReadOnly; Components: testComponent; CopyMode: AlwaysOverwrite; Attribs: Hidden; Permissions: users-Modify
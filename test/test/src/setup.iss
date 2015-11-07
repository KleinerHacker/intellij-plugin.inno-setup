[SETUP]
Compression=lzma

[Tasks]
Name: testTask; Description: "My Test Task"; Components: testBla

[TYPES]
Name: bla; Description: "Bla"; Flags: IsCustom;

[Components]
Name: testBla; Description: "Bla"; Types: bla

[Files]
Source: ""; DestDir: ""; Flags: UninsRemoveReadOnly; Components: testBla; CopyMode: AlwaysOverwrite
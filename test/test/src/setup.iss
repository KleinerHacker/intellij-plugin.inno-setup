[SETUP]
Compression=lzma

[Tasks]
Name: testTask; Description: "My Test Task"; Components: bla

[TYPES]
Name: bla; Description: "Bla"; Flags: IsCustom;

[Components]
Name: bla; Description: "Bla"; Types: bla

[Files]
Source: ""; DestDir: ""; Flags: UninsRemoveReadOnly; Components: bla;
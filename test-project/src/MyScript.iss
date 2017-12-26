; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "MyApp"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "MyCompany"
#define MyAppURL "http://www.mycompany.com"

#define MySetupName "MySetup"
#define MySetupOut "."

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
AppId={{7l8SiIoBz2T2t-dxdVUpOSyI1-Yd4cmL3g69-f7kady5l8k8W-k0nb4fR1Sf-X0p24iK1u4Xn-2ev4zL18805Rj3-7b4w8v6OW2-y34v7n5941Dn3}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
OutputBaseFilename={#MySetupName}
OutputDir={#MySetupOut}
Compression=lzma
SolidCompression=yes

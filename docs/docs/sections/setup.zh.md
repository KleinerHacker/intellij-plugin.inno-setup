# [Setup]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=setupsection){ .md-button .md-button--primary }

`[Setup]` 节是每个 Inno Setup 脚本的骨干。它使用简单的 `Directive=Value` 格式，控制从 Windows 添加/删除程序中显示的应用程序元数据到压缩算法、向导外观和权限要求的一切。只有 `AppName` 和 `AppVersion` 是严格必需的——所有其他指令都有合理的默认值。

---

**应用程序标识**

## AppName

`string` · **必需**

应用程序的完整显示名称。显示在整个安装向导和 Windows 添加/删除程序列表中。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_appname)

---

## AppVersion

`string` · **必需**

应用程序的版本字符串，例如 `1.0` 或 `2.3.1`。显示在添加/删除程序中，用于升级检测逻辑。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_appversion)

---

## AppPublisher

`string`

发布者名称，显示在添加/删除程序中。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_apppublisher)

---

## AppPublisherURL

`string`

发布者网站的 URL，在添加/删除程序中显示为可点击链接。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_apppublisherurl)

---

## AppSupportURL

`string`

在添加/删除程序中显示的支持 URL。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_appsupporturl)

---

## AppUpdatesURL

`string`

在添加/删除程序中显示的更新或下载 URL。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_appupdatesurl)

---

**编译器输出**

## Compression

`string`

用于打包安装程序有效负载的压缩算法。有效值：`lzma2/ultra64`（默认，最佳压缩比）、`bzip2`、`deflate`、`none`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_compression)

---

## OutputDir

`string`

编译的 Setup EXE 写入的目录，相对于脚本文件。默认为 `Output`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_outputdir)

---

## OutputBaseFilename

`string`

编译后安装程序的基本文件名（不含 `.exe`），例如 `myapp-setup`。默认为 `setup`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_outputbasefilename)

---

## SourceDir

`string`

`[Files]` 和其他节中使用的相对路径的基础目录。默认为包含脚本的目录。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_sourcedir)

---

## SetupIconFile

`string`

用作编译后 Setup EXE 图标的 `.ico` 文件路径。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_setupiconfile)

---

## EncryptionKey

`string`

用于加密安装程序文件的密码。安装程序在运行时会提示用户输入此密码。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_encryptionkey)

---

**安装位置**

## DefaultDirName

`string`

在选择目标页上显示的默认安装目录，例如 `{autopf}\MyApp`。支持所有 Inno Setup 目录常量。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_defaultdirname)

---

## DefaultGroupName

`string`

在选择开始菜单文件夹页上显示的默认开始菜单文件夹名称。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_defaultgroupname)

---

**权限**

## PrivilegesRequired

`string`

安装所需的权限级别。有效值：`admin`（默认）、`lowest`、`none`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_privilegesrequired)

---

## PrivilegesRequiredOverridesAllowed

`string`

允许用户覆盖所需的权限级别。值：`commandline`、`dialog`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_privilegesrequiredoverridesallowed)

---

**Windows 版本约束**

## MinVersion

`string`

运行安装程序所需的最低 Windows 版本，例如 Windows 7 为 `6.1`。使用 `0` 可有效禁用安装。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_minversion)

---

## ArchitecturesAllowed

`string` · **多个值**

安装程序可在其上运行的 CPU 架构的空格分隔列表：`x86`、`x64`、`arm64`、`ia64`。默认为全部。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_architecturesallowed)

---

## ArchitecturesInstallIn64BitMode

`string` · **多个值**

安装程序以 64 位模式运行的架构的空格分隔列表，例如 `x64compatible arm64`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_architecturesinstallin64bitmode)

---

**运行时行为**

## AppMutex

`string`

安装程序在启动前检查的互斥体名称。如果互斥体存在，则警告用户应用程序正在运行。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_appmutex)

---

## CloseApplications

`boolean`

当为 `yes` 时，安装程序自动尝试关闭已锁定文件进行更新的应用程序。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_closeapplications)

---

## LicenseFile

`string`

在许可协议向导页面上显示的 `.txt` 或 `.rtf` 许可文件的路径。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_licensefile)

---

## InfoBeforeFile

`string`

在安装开始前的信息页面上显示的 `.txt` 或 `.rtf` 文件的路径。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_infobeforefile)

---

## InfoAfterFile

`string`

在安装完成后的信息页面上显示的 `.txt` 或 `.rtf` 文件的路径。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_infoafterfile)

---

**卸载程序**

## AllowNoIcons

`boolean`

当为 `yes` 时，选择开始菜单文件夹页面上会出现*不创建开始菜单文件夹*复选框。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_allownoicons)

---

## UninstallDisplayName

`string`

在添加/删除程序中显示的应用程序名称。默认为 `AppName`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstalldisplayname)

---

## UninstallDisplayIcon

`string`

在添加/删除程序中用作图标的 EXE、DLL 或 ICO 的路径，例如 `{app}\MyApp.exe`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstalldisplayicon)

---

## CreateUninstallRegKey

`boolean`

控制是否在 `HKLM\Software\Microsoft\Windows\CurrentVersion\Uninstall` 下创建卸载注册表项。默认为 `yes`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_createuninstallregkey)

---

## Uninstallable

`string`

控制是否创建卸载程序条目的 Pascal 表达式或 `yes`/`no` 值。默认为 `yes`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstallable)

---

**向导页面**

## DisableDirPage

`string`

控制选择目标目录页面的可见性。有效值：`yes`、`no`、`auto`（默认）。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_disabledirpage)

---

## DisableProgramGroupPage

`string`

控制选择开始菜单文件夹页面的可见性。有效值：`yes`、`no`、`auto`（默认）。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_disableprogramgrouppage)

---

## DisableWelcomePage

`boolean`

当为 `yes` 时，完全跳过欢迎页面。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_disablewelcomepage)

---

## DisableReadyPage

`boolean`

当为 `yes` 时，完全跳过准备安装页面。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_disablereadypage)

---

## UsePreviousAppDir

`boolean`

当为 `yes` 时，重新安装时预填充之前使用的安装目录。默认为 `yes`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_usepreviousappdir)

---

## UsePreviousGroup

`boolean`

当为 `yes` 时，重新安装时预填充之前使用的开始菜单文件夹名称。默认为 `yes`。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_usepreviousgroup)

---

## UserInfoPage

`boolean`

当为 `yes` 时，显示询问用户姓名、公司名称和可选序列号的页面。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_userinfopage)

---

**向导外观**

## WizardStyle

`string`

向导的视觉风格。有效值：`classic`、`modern`（默认）。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardstyle)

---

## WizardResizable

`boolean`

当为 `yes` 时，用户可以调整向导窗口的大小。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardresizable)

---

## WizardImageFile

`string`

经典风格中在向导左侧显示的 BMP 文件路径。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardimagefile)

---

## WizardSmallImageFile

`string`

在向导右上角显示的 BMP 文件路径。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardsmallimagefile)

---

## SetupLogging

`boolean`

当为 `yes` 时，安装期间在用户的临时目录中自动创建日志文件。

[:octicons-link-external-16: 参考](https://jrsoftware.org/ishelp/index.php?topic=setup_setuplogging)

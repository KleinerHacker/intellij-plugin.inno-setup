# 许可证

## 本插件

Inno Setup JetBrains 插件依据 **Apache License, Version 2.0** 授权。

```
Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

完整许可证文本见仓库中的
[`LICENSE`](https://github.com/KleinerHacker/intellij-plugin.inno-setup/blob/master/LICENSE) 文件。

---

## Inno Setup

```
Copyright (C) 1997-2026 Jordan Russell. All rights reserved.
Portions Copyright (C) 2000-2026 Martijn Laan.
```

Inno Setup 是由 [jrsoftware.org](https://jrsoftware.org/isinfo.php) 以其自有的宽松许可证发布的免费安装程序构建工具。完整许可证文本见
[Inno Setup 仓库](https://github.com/jrsoftware/issrc/blob/main/license.txt)。

**本插件不附带 Inno Setup 的任何部分。** Inno Setup 编译器（`ISCC.exe`）需单独安装，并从插件设置中配置的安装目录调用。

### 衍生文档

插件附带的节、指令、参数、常量、语言代码与预处理器说明（用于代码补全、内联文档和校验）衍生自官方
[Inno Setup 文档](https://jrsoftware.org/ishelp/)，其著作权归 Jordan Russell 和 Martijn Laan 所有。

### 示例脚本

集成测试套件使用官方 Inno Setup 示例脚本验证本插件。这些脚本**不**属于本仓库：它们在测试运行前从固定的 `jrsoftware/issrc`
标签下载到构建目录，运行结束后再次删除。

---

## 商标与免责声明

本插件为非官方的社区开发插件，与 Jordan Russell、Martijn Laan 或 jrsoftware.org **无**任何隶属关系，也未获得其认可或赞助。
“Inno Setup”及 Inno Setup 徽标归各自所有者所有，此处仅用于标识本插件所支持的软件。

本插件徽标为原创作品，并非衍生自 Inno Setup 徽标。

---

## 依赖项

所有捆绑的第三方依赖项的许可证列在生成的[依赖项报告](licences/index.html)中。

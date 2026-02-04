# IMG_Trans 图片转图标

IMG_Trans 是一个基于 Java 22 + Maven + Swing 的 Windows 桌面应用，可将常见图片批量转换为 PNG 或 ICO 图标文件，支持多尺寸输出与 ICO 多尺寸内嵌。

## 功能说明
- 批量添加文件/文件夹（可递归），支持拖拽
- 支持格式：png/jpg/jpeg/bmp/gif/tiff/webp
- 输出 PNG：可多尺寸批量生成，保留透明通道
- 输出 ICO：单文件内嵌多尺寸位图（16/24/32/48/64/128/256）
- 缩放策略：等比补边（默认）、等比居中裁剪、强制拉伸
- 日志：界面日志 + 文件日志（默认 `C:\Users\<用户>\.img_trans\logs\img_trans.log`）
- 配置持久化：输出格式、尺寸、输出目录、缩放模式等

## 运行方式（PowerShell）
```powershell
mvn -q -DskipTests clean package
java -jar .\target\IMG_Trans-1.0.0.jar
```

## 打包产物位置
- 可执行 Jar：`target/IMG_Trans-1.0.0.jar`

## 依赖说明
- **FlatLaf**：提供现代化 Swing 主题，提升可读性与观感。
- **TwelveMonkeys ImageIO**：增强 ImageIO 对 TIFF 等格式的读取能力，保证兼容性。
- **Luciad WebP ImageIO**：为 ImageIO 增加 WebP 读取能力。
- **内置 ICO 写入器**：通过 PNG 编码封装多尺寸 ICO，避免额外依赖并保证可移植性。

## 截图
- 截图待补充：主界面包含左侧文件列表、右侧预览区、顶部工具栏、下方尺寸与缩放设置以及日志区。

## 示例资源
由于仓库禁止直接提交二进制文件，示例 PNG 以 Base64 形式提供在 `src/main/resources/sample/sample_base64.txt`。
可通过以下方式生成示例 PNG：
```powershell
$base64 = Get-Content .\src\main\resources\sample\sample_base64.txt
[IO.File]::WriteAllBytes(".\sample.png", [Convert]::FromBase64String($base64))
```

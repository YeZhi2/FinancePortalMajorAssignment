# 提交包

| 文件 | 说明 |
| --- | --- |
| `FinancePortalMajorAssignment-v1.1.zip` | 课程作业提交包，对应仓库标签 `v1.1`。含完整源码、数据库脚本、**需求文档（Word）**、**AI 使用记录（Word）**、启动说明（Word + Markdown）；不含 `.git`、`dist/`、构建产物与本地环境 |
| `SHA256SUMS.txt` | 压缩包的 SHA256 校验值 |

## 包内文档位置

| 交付物 | 文件 |
| --- | --- |
| 需求文档 | `docs/需求文档.docx`（Word 版）、`docs/requirements.md`（源文件） |
| AI 使用记录 | `docs/AI使用记录.docx`（Word 版）、`docs/ai-usage.md`（源文件） |
| 启动说明 | `docs/启动说明.docx`（Word 版）、`README.md`（随仓库） |
| 作业原文 | `docs/金融理财产品门户大作业.docx` |

## 校验

```bash
# Windows PowerShell
Get-FileHash FinancePortalMajorAssignment-v1.1.zip -Algorithm SHA256

# macOS / Linux
sha256sum -c SHA256SUMS.txt
```

## 重新生成

压缩包按「标签 `v1.1` 的内容、排除 `dist/`」生成，避免自我嵌套：

```bash
git archive --format=zip --prefix=FinancePortalMajorAssignment/ \
  -o dist/FinancePortalMajorAssignment-v1.1.zip \
  v1.1 -- . ':(exclude)dist'
```

生成后需重新计算并更新 `SHA256SUMS.txt`。

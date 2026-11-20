# 提交包

| 文件 | 说明 |
| --- | --- |
| `FinancePortalMajorAssignment-v1.0.zip` | 课程作业提交包，对应仓库标签 `v1.0`（提交 `1346668`）。含完整源码、数据库脚本、需求文档、AI 使用记录与启动说明；不含 `.git`、构建产物与本地环境 |
| `SHA256SUMS.txt` | 压缩包的 SHA256 校验值 |

## 校验

```bash
# Windows PowerShell
Get-FileHash FinancePortalMajorAssignment-v1.0.zip -Algorithm SHA256

# macOS / Linux
sha256sum -c SHA256SUMS.txt
```

## 重新生成

```bash
git archive --format=zip --prefix=FinancePortalMajorAssignment/ v1.0 \
  -o dist/FinancePortalMajorAssignment-v1.0.zip
```

压缩包是标签 `v1.0` 的静态快照，后续提交（包括本目录）不会再打入该包。

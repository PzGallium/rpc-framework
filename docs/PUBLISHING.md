# 发布到 Maven 仓库

本文说明如何将本框架发布到 **GitHub Packages** 或 **Maven 中央仓库**，供他人通过依赖使用。

## 发布到 GitHub Packages

### 1. 获取 Personal Access Token

- GitHub → Settings → Developer settings → Personal access tokens
- 生成 token，勾选 `write:packages`、`read:packages`

### 2. 配置 Maven settings.xml

在 `~/.m2/settings.xml` 的 `<servers>` 中增加：

```xml
<server>
  <id>github</id>
  <username>你的GitHub用户名</username>
  <password>你的PAT</password>
</server>
```

### 3. 发布

在项目根目录执行：

```bash
mvn clean deploy -Pgithub-packages
```

若仓库并非 `PzGallium/my-rpc-framework`，可先设置环境变量再发布：

```bash
export GITHUB_REPOSITORY_OWNER=你的用户名
mvn clean deploy -Pgithub-packages
```

### 4. 他人引用

在依赖方项目的 `pom.xml` 中增加 repository 与 dependency：

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/你的用户名/my-rpc-framework</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>io.github.PzGallium</groupId>
    <artifactId>rpc</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
</dependencies>
```

## 发布到 Maven 中央仓库

1. 在 [OSSRH](https://central.sonatype.com/) 注册并创建 GroupId（如 `io.github.PzGallium`）。
2. 按 [官方指南](https://central.sonatype.com/publish/publish-guide/) 配置 GPG 签名与 `settings.xml` 中的 Nexus 凭据。
3. 在父 POM 中配置 `distributionManagement` 指向 OSSRH，并配置 `maven-gpg-plugin`、`nexus-staging-maven-plugin`。
4. 执行 `mvn clean deploy` 并到 OSSRH 控制台完成 Release。

详细步骤见：  
https://maven.apache.org/guides/mini/guide-central-repository-upload.html

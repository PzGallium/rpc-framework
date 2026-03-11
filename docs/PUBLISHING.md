# Publishing to Maven

This document describes how to publish this framework to **GitHub Packages** or **Maven Central** so others can use it as a dependency.

## Publish to GitHub Packages

### 1. Create a Personal Access Token

- GitHub → Settings → Developer settings → Personal access tokens
- Create a token with `write:packages` and `read:packages` scopes

### 2. Configure Maven settings.xml

Add a server entry in `~/.m2/settings.xml` under `<servers>`:

```xml
<server>
  <id>github</id>
  <username>YOUR_GITHUB_USERNAME</username>
  <password>YOUR_PAT</password>
</server>
```

### 3. Publish

From the project root:

```bash
mvn clean deploy -Pgithub-packages
```

If your repo is not `PzGallium/my-rpc-framework`, set the owner and run:

```bash
export GITHUB_REPOSITORY_OWNER=your_username
mvn clean deploy -Pgithub-packages
```

### 4. Consuming the published artifact

In the consuming project’s `pom.xml`, add the repository and dependency:

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/YOUR_USERNAME/my-rpc-framework</url>
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

## Publish to Maven Central

1. Register at [OSSRH](https://central.sonatype.com/) and create a GroupId (e.g. `io.github.PzGallium`).
2. Follow the [publish guide](https://central.sonatype.com/publish/publish-guide/) to set up GPG signing and Nexus credentials in `settings.xml`.
3. Configure `distributionManagement` in the parent POM to point to OSSRH, and add `maven-gpg-plugin` and `nexus-staging-maven-plugin`.
4. Run `mvn clean deploy` and complete the release in the OSSRH console.

For full steps see:  
https://maven.apache.org/guides/mini/guide-central-repository-upload.html

# RPC Framework

A lightweight **Java RPC (Remote Procedure Call) framework** built from scratch using **Netty 4.x**, **Spring**, and **Zookeeper**.  
It provides efficient, persistent, and extensible communication between distributed systems with nearly zero configuration.

---

## 前置条件（Prerequisites）

| 依赖 | 说明 |
|------|------|
| **JDK 8** | 编译与运行需 Java 8 |
| **Maven 3.x** | 用于构建 |
| **Zookeeper** | 服务注册中心，默认端口 2181；也可用 Docker 启动（见下文） |

---

## 配置说明（Configuration）

支持通过 **系统属性** 或 **环境变量** 覆盖默认值，便于不同环境使用。

| 配置项 | 系统属性 | 环境变量 | 默认值 |
|--------|----------|----------|--------|
| Zookeeper 地址 | `-Drpc.zk.address=host:port` | `RPC_ZK_ADDRESS` | `localhost:2181` |
| RPC 服务端端口 | `-Drpc.server.port=9000` | `RPC_SERVER_PORT` | `9000` |

示例：

```bash
# 使用系统属性
java -Drpc.zk.address=192.168.1.100:2181 -Drpc.server.port=9000 -jar rpc/target/rpc-0.0.1-SNAPSHOT-jar-with-dependencies.jar

# 或使用环境变量
export RPC_ZK_ADDRESS=192.168.1.100:2181
export RPC_SERVER_PORT=9000
java -jar rpc/target/rpc-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

---

## 🚀 Overview

This project implements a fully functional RPC framework similar in architecture to Dubbo or gRPC.  
It supports both **long-lived Netty connections** and **annotation-based configuration**, enabling seamless service invocation between distributed Java applications.

### Architecture
- **`rpc/`** – Server-side module (service provider)
- **`consumer/`** – Client-side module (service consumer)
- **Zookeeper** – Acts as the service registry center
- **Netty 4.x** – Provides asynchronous network communication and connection management
- **Spring** – Enables dependency injection and annotation-based service exposure
- **JSON Serialization** – Handles message serialization and deserialization

---

## ⚙️ Core Features

| Feature | Description |
|----------|-------------|
| **Long Connection Support** | Persistent TCP connections using Netty's event loop model for high performance. |
| **Asynchronous Invocation** | Client requests are non-blocking, handled through `Future` and callback mechanisms. |
| **Heartbeat Detection** | Maintains connection stability between client and server. |
| **JSON Serialization** | Converts requests and responses to lightweight JSON format for transport. |
| **Annotation-Based Configuration** | Nearly zero manual configuration; services and clients use annotations for registration and discovery. |
| **Zookeeper-Based Service Registry** | Centralized service registration, discovery, and monitoring. |
| **Dynamic Client Connection Management** | Clients automatically manage connections to available servers in Zookeeper. |
| **Service Watching and Discovery** | Clients subscribe to registry changes and update service lists dynamically. |
| **Server-Side Service Registration** | Providers automatically register on startup and deregister on shutdown. |
| **Netty 4.x Implementation** | Built entirely on Netty 4.x for scalability and non-blocking I/O. |

---

## 首次运行步骤（How to Run）

### 1. 启动 Zookeeper

**方式 A：本机已安装 Zookeeper**

```shell
# Linux / macOS
bin/zkServer.sh start
```

**方式 B：使用 Docker（推荐，无需本地安装）**

```shell
docker-compose up -d
```

详见下文 [使用 Docker 启动 Zookeeper](#使用-docker-启动-zookeeper)。

### 2. 一键构建项目

在项目根目录执行：

```shell
mvn clean install
```

会先构建并安装 `rpc`，再构建 `consumer`。

### 3. 启动 RPC 服务端

```shell
java -jar rpc/target/rpc-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

如需指定 Zookeeper 或端口：

```shell
java -Drpc.zk.address=localhost:2181 -Drpc.server.port=9000 -jar rpc/target/rpc-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

### 4. 运行客户端

**方式 A：运行示例入口（推荐）**

```shell
java -jar consumer/target/consumer-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

若 Zookeeper 不在本机或非默认端口，请先设置环境变量或系统属性（与服务端一致）：

```shell
java -Drpc.zk.address=localhost:2181 -jar consumer/target/consumer-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

**方式 B：在业务项目中作为依赖使用**

将本框架作为依赖引入，使用 `@RemoteInvoke` 注入远程接口并调用。参见 [作为依赖使用](#作为依赖使用)。

**方式 C：运行单元测试**

```shell
cd consumer
mvn test -Dtest=RemoteInvokingTest
```

---

## 使用 Docker 启动 Zookeeper

项目根目录提供 `docker-compose.yml`，可快速启动单机 Zookeeper（端口 2181）：

```shell
docker-compose up -d
```

停止：

```shell
docker-compose down
```

---

## 作为依赖使用

若希望在自己的项目中作为 RPC 客户端/服务端使用，可将本仓库发布到本地仓库或 Maven 私服后引入：

```xml
<dependency>
    <groupId>io.github.PzGallium</groupId>
    <artifactId>rpc</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<!-- 仅客户端需要 consumer 时再引入 consumer 模块或复制接口与注解 -->
```

发布到本地仓库：

```shell
mvn clean install
```

发布到 Maven 中央仓库或 GitHub Packages 的配置与步骤见 [发布到 Maven 仓库](#发布到-maven-仓库)。

---

## 发布到 Maven 仓库

如需将 `rpc`（及可选 `consumer`）发布到 **Maven 中央仓库** 或 **GitHub Packages**，可在父 POM 中配置 `maven-deploy-plugin` / `maven-publish`，并在 CI（如 GitHub Actions）中配置相应凭据。

- **Maven 中央仓库**：需在 [OSSRH](https://central.sonatype.com/) 申请 GroupId，并配置 GPG 与 Nexus 凭据。
- **GitHub Packages**：在仓库 Settings → Secrets 中配置 `GITHUB_TOKEN`，在父 POM 中配置 `distributionManagement` 指向 `https://maven.pkg.github.com/你的用户名/my-rpc-framework`。

具体配置示例可参考 [Maven 官方文档](https://maven.apache.org/guides/mini/guide-central-repository-upload.html) 与 [GitHub Packages 文档](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)。

---

## 🧩 Quick Start（开发示例）

### ✅ Server-Side Development

Create your business service:

```java
@Service
public class TestService {
    public void test(User user){
        System.out.println("调用了TestService.test");
    }
}
```

### ✅ Define the interface and implementation:

```java
public interface TestRemote {
    Response testUser(User user);
}

@Remote
public class TestRemoteImpl implements TestRemote {
    @Resource
    private TestService service;

    public Response testUser(User user){
        service.test(user);
        return ResponseUtil.createSuccessResponse(user);
    }
}
```

### ✅ Client-Side Development

Define the client interface:

```java
public interface TestRemote {
    Response testUser(User user);
}
```

Invoke with annotation:

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RemoteInvokingTest.class)
@ComponentScan("io.github.PzGallium")
public class RemoteInvokingTest {

    @RemoteInvoke
    private UserRemote userRemote;

    @Test
    public void testSaveUser() {
        User user = new User(1000, "张三");
        userRemote.saveUser(user);
    }
}
```

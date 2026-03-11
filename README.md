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
- **`rpc/`** – Server-side module (service provider)；**对外契约（接口与公共 DTO）仅在此定义**，consumer 依赖 rpc 引用同一份
- **`consumer/`** – Client-side module (service consumer)，依赖 rpc 获得接口与 DTO，无需重复定义
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

## 在框架下开发（Develop Within This Repo）

本框架采用 **契约在 rpc、consumer 只引用** 的方式：**接口与公共 DTO 只在 rpc 模块定义一份**，consumer 依赖 rpc 后直接引用，无需重复定义。在仓库内开发时只需改 rpc 一处即可同步服务端与客户端契约。

### 契约与模块职责

| 内容 | 所在模块 | 说明 |
|------|----------|------|
| 远程接口（如 `UserRemote`） | **rpc** | 在 `rpc` 中定义，consumer 通过依赖 rpc 引用 |
| 公共 DTO（如 `User`）、返回类型 `Response` | **rpc** | 与接口同放在 rpc，供服务端与客户端共用 |
| 接口实现（如 `UserRemoteImpl`） | **rpc** | 打 `@Remote`，随服务端启动注册 |
| 客户端调用代码 | **consumer** | 使用 `@RemoteInvoke` 注入 rpc 中的接口并调用 |

本仓库示例：接口与 DTO 见 `rpc/src/main/java/io/github/PzGallium/rpc/user/`，实现见 `UserRemoteImpl`，客户端示例见 `consumer/.../SampleClientMain.java` 与测试类 `RemoteInvokingTest`。

### 开发约定（必读）

- **方法名全局唯一**：所有 `@Remote` 实现类中的**方法名**在整机内不能重复，服务端按方法名路由，重名会互相覆盖。
- **仅支持单参数**：远程方法目前只支持**一个参数**。多参数请封装成一个 DTO，放在 rpc 中供双方使用。
- **返回值类型**：建议统一使用 rpc 提供的 `io.github.PzGallium.rpc.netty.util.Response`，便于客户端统一解析。

### 新增一个 RPC 接口的完整步骤

#### 1. 在 rpc 中定义契约与实现（只改 rpc）

**1.1 公共 DTO**（若无则新建）  
路径示例：`rpc/src/main/java/io/github/PzGallium/rpc/xxx/bean/XxxBean.java`

```java
package io.github.PzGallium.rpc.xxx.bean;

public class XxxBean {
    private Long id;
    private String name;
    // getters / setters
}
```

**1.2 远程接口**  
路径示例：`rpc/src/main/java/io/github/PzGallium/rpc/xxx/remote/XxxRemote.java`

```java
package io.github.PzGallium.rpc.xxx.remote;

import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.xxx.bean.XxxBean;

public interface XxxRemote {
    Response doSomething(XxxBean param);
}
```

**1.3 实现类（打 @Remote）**  
路径示例：`rpc/src/main/java/io/github/PzGallium/rpc/xxx/remote/XxxRemoteImpl.java`

```java
package io.github.PzGallium.rpc.xxx.remote;

import io.github.PzGallium.rpc.netty.annotation.Remote;
import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.netty.util.ResponseUtil;
import io.github.PzGallium.rpc.xxx.bean.XxxBean;

@Remote
public class XxxRemoteImpl implements XxxRemote {

    @Override
    public Response doSomething(XxxBean param) {
        // 业务逻辑
        return ResponseUtil.createSuccessResponse(param);
    }
}
```

确保实现类在 Spring 扫描范围内（本仓库已 `@ComponentScan("io.github.PzGallium")`，放在上述包下即可）。

#### 2. 在 consumer 中调用（只引用 rpc，不再定义接口/DTO）

consumer 已依赖 rpc，**无需**在 consumer 中再写接口或 DTO，直接引用 rpc 的类即可。

**2.1 在需要调用的类中注入并调用**

```java
package io.github.PzGallium.consumer.xxx;

import io.github.PzGallium.consumer.annotation.RemoteInvoke;
import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.xxx.bean.XxxBean;
import io.github.PzGallium.rpc.xxx.remote.XxxRemote;

public class YourClientService {

    @RemoteInvoke
    private XxxRemote xxxRemote;

    public void callRemote() {
        XxxBean param = new XxxBean();
        param.setName("hello");
        Response resp = xxxRemote.doSomething(param);
        System.out.println(resp.getResult());
    }
}
```

**2.2 若写可运行入口**  
像 `SampleClientMain` 那样，用 `@Configuration` + `@ComponentScan("io.github.PzGallium")` 启动 Spring 上下文，在某个 Bean 中注入 `XxxRemote` 并调用即可。

#### 3. 运行与验证

1. 启动 Zookeeper（如 `docker-compose up -d`）。
2. 启动 rpc 服务端：`java -jar rpc/target/rpc-0.0.1-SNAPSHOT-jar-with-dependencies.jar`。
3. 运行 consumer：执行你的 main 或 `java -jar consumer/target/consumer-0.0.1-SNAPSHOT-jar-with-dependencies.jar`，或跑单元测试。

### 本仓库示例索引

| 说明 | 路径 |
|------|------|
| 接口与 DTO | `rpc/src/main/java/io/github/PzGallium/rpc/user/remote/UserRemote.java`、`rpc/.../user/bean/User.java` |
| 服务端实现 | `rpc/.../user/remote/UserRemoteImpl.java` |
| 返回类型 | `rpc/.../netty/util/Response.java`、`ResponseUtil.java` |
| 客户端示例入口 | `consumer/.../SampleClientMain.java` |
| 客户端单元测试 | `consumer/.../client/RemoteInvokingTest.java` |

按上述步骤即可在本仓库内直接开发、启动服务端与客户端，契约只维护一份（在 rpc），适合开源社区在框架下扩展与二次开发。

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

若希望在自己的项目中作为 RPC 客户端/服务端使用，可将本仓库发布到本地仓库或 Maven 私服后引入。**接口与公共 DTO 均在 rpc 中**，引入 rpc 即可获得契约，无需再复制一份接口：

```xml
<dependency>
    <groupId>io.github.PzGallium</groupId>
    <artifactId>rpc</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<!-- 仅客户端需再引入 consumer 模块，或自行实现 @RemoteInvoke 的代理与 TcpClient 调用 -->
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

## 🧩 Quick Start（代码示例）

以下与仓库内 **契约只在 rpc、consumer 只引用** 的用法一致，可直接对照 `rpc/user` 与 `consumer/SampleClientMain`、`RemoteInvokingTest` 阅读。

### 服务端（rpc 模块）

业务类与接口、实现均放在 rpc 中，实现类打 `@Remote`：

```java
// 接口与 DTO 仅在 rpc 定义，见 rpc/.../user/remote/UserRemote.java、user/bean/User.java
public interface UserRemote {
    Response saveUser(User user);
    Response saveUsers(List<User> users);
}

@Remote
public class UserRemoteImpl implements UserRemote {
    @Resource
    private UserService userService;

    public Response saveUser(User user) {
        userService.save(user);
        return ResponseUtil.createSuccessResponse(user);
    }
    // ...
}
```

### 客户端（consumer 模块）

**不再重复定义**接口与 User，直接从 rpc 引用并注入调用：

```java
import io.github.PzGallium.consumer.annotation.RemoteInvoke;
import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.user.bean.User;
import io.github.PzGallium.rpc.user.remote.UserRemote;

@RemoteInvoke
private UserRemote userRemote;

// 调用
User user = new User();
user.setId(1);
user.setName("张三");
Response resp = userRemote.saveUser(user);
```

单元测试中同样只引用 rpc 的接口与 DTO，见 `consumer/.../client/RemoteInvokingTest.java`。

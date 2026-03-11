# RPC Framework

A lightweight **Java RPC (Remote Procedure Call) framework** built from scratch using **Netty 4.x**, **Spring**, and **Zookeeper**.  
It provides efficient, persistent, and extensible communication between distributed systems with nearly zero configuration.

---

## Prerequisites

| Requirement | Description |
|-------------|-------------|
| **JDK 8** | Required for build and runtime |
| **Maven 3.x** | For building the project |
| **Zookeeper** | Service registry; default port 2181. You can also start it via Docker (see below) |

---

## Configuration

You can override defaults via **system properties** or **environment variables** for different environments.

| Option | System property | Environment variable | Default |
|--------|-----------------|----------------------|---------|
| Zookeeper address | `-Drpc.zk.address=host:port` | `RPC_ZK_ADDRESS` | `localhost:2181` |
| RPC server port | `-Drpc.server.port=9000` | `RPC_SERVER_PORT` | `9000` |

Example:

```bash
# Using system properties
java -Drpc.zk.address=192.168.1.100:2181 -Drpc.server.port=9000 -jar rpc/target/rpc-0.0.1-SNAPSHOT-jar-with-dependencies.jar

# Or using environment variables
export RPC_ZK_ADDRESS=192.168.1.100:2181
export RPC_SERVER_PORT=9000
java -jar rpc/target/rpc-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

---

## Overview

This project implements a fully functional RPC framework similar in architecture to Dubbo or gRPC.  
It supports both **long-lived Netty connections** and **annotation-based configuration**, enabling seamless service invocation between distributed Java applications.

### Architecture

- **`rpc/`** – Server-side module (service provider). **API contracts (interfaces and shared DTOs) are defined only here**; consumer depends on rpc and uses the same types.
- **`consumer/`** – Client-side module (service consumer). It depends on rpc for interfaces and DTOs; no duplicate definitions.
- **Zookeeper** – Service registry center.
- **Netty 4.x** – Asynchronous network communication and connection management.
- **Spring** – Dependency injection and annotation-based service exposure.
- **JSON** – Message serialization and deserialization.

---

## Core Features

| Feature | Description |
|---------|-------------|
| **Long connection** | Persistent TCP connections using Netty’s event loop for high performance. |
| **Asynchronous invocation** | Non-blocking client requests via `Future` and callbacks. |
| **Heartbeat** | Keeps client–server connections alive. |
| **JSON serialization** | Lightweight request/response over the wire. |
| **Annotation-based config** | Services and clients use annotations for registration and discovery. |
| **Zookeeper registry** | Central registration, discovery, and monitoring. |
| **Dynamic client connections** | Clients connect to servers discovered from Zookeeper. |
| **Service watching** | Clients subscribe to registry changes and update server list. |
| **Server registration** | Providers register on startup and unregister on shutdown. |
| **Netty 4.x** | Implementation is built on Netty 4.x for scalability and non-blocking I/O. |

---

## How to Run

### 1. Start Zookeeper

**Option A: Zookeeper installed locally**

```shell
# Linux / macOS
bin/zkServer.sh start
```

**Option B: Docker (recommended)**

```shell
docker-compose up -d
```

See [Start Zookeeper with Docker](#start-zookeeper-with-docker) below.

### 2. Build the project

From the project root:

```shell
mvn clean install
```

This builds and installs `rpc` first, then builds `consumer`.

### 3. Start the RPC server

```shell
java -jar rpc/target/rpc-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

To override Zookeeper or port:

```shell
java -Drpc.zk.address=localhost:2181 -Drpc.server.port=9000 -jar rpc/target/rpc-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

### 4. Run the client

**Option A: Sample main (recommended)**

```shell
java -jar consumer/target/consumer-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

If Zookeeper is not on localhost or uses a non-default port, set the same env or system properties as the server:

```shell
java -Drpc.zk.address=localhost:2181 -jar consumer/target/consumer-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

**Option B: Use as a dependency**

Add this framework as a dependency and use `@RemoteInvoke` to inject and call remote interfaces. See [Use as a dependency](#use-as-a-dependency).

**Option C: Run tests**

```shell
cd consumer
mvn test -Dtest=RemoteInvokingTest
```

---

## Develop within this repo

The framework uses a **single contract in rpc** model: **interfaces and shared DTOs are defined only in the rpc module**. The consumer depends on rpc and references those types, so you do not duplicate them. When developing in this repo, you only change the contract in rpc and both server and client stay in sync.

### Contract and module roles

| Item | Module | Notes |
|------|--------|--------|
| Remote interface (e.g. `UserRemote`) | **rpc** | Defined in rpc; consumer references it via the rpc dependency |
| Shared DTO (e.g. `User`) and `Response` | **rpc** | Lives in rpc and is used by both server and client |
| Implementation (e.g. `UserRemoteImpl`) | **rpc** | Annotated with `@Remote`; registered when the server starts |
| Client call site | **consumer** | Injects the rpc interface with `@RemoteInvoke` and calls it |

Example in this repo: interface and DTO under `rpc/src/main/java/io/github/PzGallium/rpc/user/`, implementation in `UserRemoteImpl`, client usage in `consumer/.../SampleClientMain.java` and test `RemoteInvokingTest`.

### Conventions (required)

- **Globally unique method names** – Method names of all `@Remote` implementations must be unique across the server. The server routes by method name; duplicates overwrite each other.
- **Single parameter only** – Remote methods currently support **one parameter**. For multiple arguments, use one DTO in rpc.
- **Return type** – Use the framework’s `io.github.PzGallium.rpc.netty.util.Response` so the client can parse results consistently.

### Steps to add a new RPC interface

#### 1. Define contract and implementation in rpc (only rpc changes)

**1.1 Shared DTO** (if needed)  
Example path: `rpc/src/main/java/io/github/PzGallium/rpc/xxx/bean/XxxBean.java`

```java
package io.github.PzGallium.rpc.xxx.bean;

public class XxxBean {
    private Long id;
    private String name;
    // getters / setters
}
```

**1.2 Remote interface**  
Example path: `rpc/src/main/java/io/github/PzGallium/rpc/xxx/remote/XxxRemote.java`

```java
package io.github.PzGallium.rpc.xxx.remote;

import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.xxx.bean.XxxBean;

public interface XxxRemote {
    Response doSomething(XxxBean param);
}
```

**1.3 Implementation with @Remote**  
Example path: `rpc/src/main/java/io/github/PzGallium/rpc/xxx/remote/XxxRemoteImpl.java`

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
        // your logic
        return ResponseUtil.createSuccessResponse(param);
    }
}
```

Ensure the implementation is in the Spring scan path (this repo uses `@ComponentScan("io.github.PzGallium")`, so the package above is enough).

#### 2. Call from consumer (reference rpc only; do not redefine interface/DTO)

Consumer already depends on rpc. **Do not** redefine the interface or DTO in consumer; use the types from rpc.

**2.1 Inject and call in your client class**

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

**2.2 Runnable entry**  
Like `SampleClientMain`: use `@Configuration` and `@ComponentScan("io.github.PzGallium")` to start the Spring context, then inject `XxxRemote` in a bean and call it.

#### 3. Run and verify

1. Start Zookeeper (e.g. `docker-compose up -d`).
2. Start the rpc server: `java -jar rpc/target/rpc-0.0.1-SNAPSHOT-jar-with-dependencies.jar`.
3. Run the consumer: your main, or `java -jar consumer/target/consumer-0.0.1-SNAPSHOT-jar-with-dependencies.jar`, or run the unit test.

### Example locations in this repo

| Description | Path |
|-------------|------|
| Interface and DTO | `rpc/.../user/remote/UserRemote.java`, `rpc/.../user/bean/User.java` |
| Server implementation | `rpc/.../user/remote/UserRemoteImpl.java` |
| Response type | `rpc/.../netty/util/Response.java`, `ResponseUtil.java` |
| Client sample entry | `consumer/.../SampleClientMain.java` |
| Client unit test | `consumer/.../client/RemoteInvokingTest.java` |

Following these steps you can develop and run both server and client in this repo with a single contract maintained in rpc.

---

## Start Zookeeper with Docker

The root directory includes `docker-compose.yml` to run a single Zookeeper node (port 2181):

```shell
docker-compose up -d
```

To stop:

```shell
docker-compose down
```

---

## Use as a dependency

To use this framework as an RPC client or server in your own project, publish to your local repo or a Maven repository and add the dependency. **Interfaces and shared DTOs are in the rpc artifact**; depend on rpc to get the contract without copying it:

```xml
<dependency>
    <groupId>io.github.PzGallium</groupId>
    <artifactId>rpc</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<!-- For client usage you may also depend on consumer, or implement @RemoteInvoke-style proxy and TcpClient yourself -->
```

Install to local repo:

```shell
mvn clean install
```

For publishing to Maven Central or GitHub Packages, see [Publishing to Maven](#publishing-to-maven).

---

## Publishing to Maven

To publish `rpc` (and optionally `consumer`) to **Maven Central** or **GitHub Packages**, configure `maven-deploy-plugin` / `maven-publish` in the parent POM and set up credentials in CI (e.g. GitHub Actions).

- **Maven Central**: Register at [OSSRH](https://central.sonatype.com/), create a GroupId, and configure GPG and Nexus credentials.
- **GitHub Packages**: Add `GITHUB_TOKEN` in repository Settings → Secrets and set `distributionManagement` in the parent POM to `https://maven.pkg.github.com/YOUR_USERNAME/my-rpc-framework`.

See [Maven Central upload guide](https://maven.apache.org/guides/mini/guide-central-repository-upload.html) and [GitHub Packages for Maven](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry) for details.

---

## Quick Start (code samples)

The following matches the **contract in rpc, consumer only references** usage. You can compare with `rpc/user` and `consumer/SampleClientMain`, `RemoteInvokingTest`.

### Server (rpc module)

Define interface and implementation in rpc; annotate the implementation with `@Remote`:

```java
// Interface and DTO are defined only in rpc; see rpc/.../user/remote/UserRemote.java, user/bean/User.java
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

### Client (consumer module)

**Do not** redefine the interface or `User`; import from rpc and inject:

```java
import io.github.PzGallium.consumer.annotation.RemoteInvoke;
import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.user.bean.User;
import io.github.PzGallium.rpc.user.remote.UserRemote;

@RemoteInvoke
private UserRemote userRemote;

// call
User user = new User();
user.setId(1);
user.setName("Alice");
Response resp = userRemote.saveUser(user);
```

Tests also reference rpc’s interface and DTO only; see `consumer/.../client/RemoteInvokingTest.java`.

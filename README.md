# RPC Framework

A lightweight **Java RPC (Remote Procedure Call) framework** built from scratch using **Netty 4.x**, **Spring**, and **Zookeeper**.  
It provides efficient, persistent, and extensible communication between distributed systems with nearly zero configuration.

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
| **Long Connection Support** | Persistent TCP connections using Netty’s event loop model for high performance. |
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

## 🧩 Quick Start

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
@ContextConfiguration(classes = RemoteInvokeTest.class)
@ComponentScan("\\")
public class RemoteInvokeTest {

    @RemoteInvoke
    public static TestRemote userRemote;

    @Test
    public void testSaveUser() {
        User user = new User(1000, "张三");
        userRemote.testUser(user);
    }
}
```

## 🛠️ How to Run

### Start Zookeeper
Ensure Zookeeper is running (default port 2181):

```shell
bin/zkServer.sh start
```

### Start the server

```shell
cd rpc
mvn clean package
java -cp target/rpc.jar io.github.PzGallium.rpc.server.SpringServer
```
### Start the Client
```shell
cd consumer
mvn clean package
java -cp target/consumer.jar io.github.PzGallium.consumer.core.TcpClient

```
package io.github.PzGallium.consumer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.github.PzGallium.consumer.annotation.RemoteInvoke;
import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.user.bean.User;
import io.github.PzGallium.rpc.user.remote.UserRemote;

/**
 * 示例客户端入口：演示如何在本项目中启动 Spring 上下文并调用 RPC 接口。
 * 运行前请确保 Zookeeper 已启动，且 rpc 服务端已运行。
 */
@Configuration
@ComponentScan("io.github.PzGallium")
public class SampleClientMain {

	@RemoteInvoke
	private UserRemote userRemote;

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(SampleClientMain.class);
		SampleClientMain app = context.getBean(SampleClientMain.class);
		app.run();
	}

	private void run() {
		User user = new User();
		user.setId(100);
		user.setName("SampleClient");
		Response resp = userRemote.saveUser(user);
		System.out.println("RPC Response: " + resp.getResult());
	}
}

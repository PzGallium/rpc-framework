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
 * Sample client entry: starts Spring context and invokes an RPC interface.
 * Ensure Zookeeper and the rpc server are running before starting the client.
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

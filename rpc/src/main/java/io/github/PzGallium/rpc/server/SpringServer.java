package io.github.PzGallium.rpc.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.github.PzGallium.rpc.netty.medium.Medium;

@Configuration
@ComponentScan("io.github.PzGallium")
public class SpringServer {
	
	public static void main (String[] args) throws InterruptedException {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringServer.class);
	}
}

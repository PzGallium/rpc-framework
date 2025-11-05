package io.github.netty.factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperFactory {
	public static CuratorFramework client;
	
	public static CuratorFramework create() {
		if (client == null) {
			//retry 3 times 1 second waiting between each retry
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(100, 3);
			client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
			client.start();
		}
		return client;
	}
	
	public static void main(String[] args) throws Exception {
		CuratorFramework client = create();
		client.create().forPath("/netty");
	}
}

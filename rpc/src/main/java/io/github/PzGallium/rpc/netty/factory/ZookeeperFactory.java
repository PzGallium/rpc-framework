package io.github.PzGallium.rpc.netty.factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import io.github.PzGallium.rpc.config.RpcConfig;

public class ZookeeperFactory {
	public static CuratorFramework client;

	public static CuratorFramework create() {
		if (client == null) {
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(100, 3);
			client = CuratorFrameworkFactory.newClient(RpcConfig.getZkAddress(), retryPolicy);
			client.start();
		}
		return client;
	}
	
	public static void main(String[] args) throws Exception {
		CuratorFramework client = create();
		client.create().forPath("/netty");
	}
}

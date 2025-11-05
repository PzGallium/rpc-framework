package io.github.PzGallium.consumer.core;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import io.github.PzGallium.rpc.netty.factory.ZookeeperFactory;
import io.netty.channel.ChannelFuture;

public class ServerWatcher implements CuratorWatcher {

	
	@Override
	public void process(WatchedEvent event) throws Exception {
		CuratorFramework client = ZookeeperFactory.create();
		String path = event.getPath();
		client.getChildren().usingWatcher(this).forPath(path);
		List<String> serverPaths = client.getChildren().forPath(path);
		ChannelManager.realServerPath.clear();
		for (String serverPath : serverPaths) { 
			String[] str = serverPath.split("#");
			ChannelManager.realServerPath.add(str[0] + "#" + str[1]);
		}
		ChannelManager.clear();
		for(String realServer : ChannelManager.realServerPath) {
			String[] str = realServer.split("#");
			try {
				ChannelFuture channelFuture = TcpClient.b.connect(str[0], Integer.valueOf(str[1]));
				ChannelManager.add(channelFuture);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}

}

package io.github.PzGallium.consumer.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import com.alibaba.fastjson.JSONObject;

import io.github.PzGallium.consumer.constant.Constants;
import io.github.PzGallium.consumer.handler.SimpleClientHandler;
import io.github.PzGallium.consumer.param.ClientRequest;
import io.github.PzGallium.consumer.param.Response;
import io.github.PzGallium.rpc.netty.factory.ZookeeperFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TcpClient {
	static final Bootstrap b =new Bootstrap();
	static ChannelFuture f = null;
	static {
		String host = "localhost";
        int port = 9000;
		EventLoopGroup workerGroup = new NioEventLoopGroup();
        b.group(workerGroup); 
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true); 
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
            	ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                ch.pipeline().addLast(new StringDecoder());
            	ch.pipeline().addLast(new SimpleClientHandler());
            	ch.pipeline().addLast(new StringEncoder());
            }
        });
        
		CuratorFramework client = ZookeeperFactory.create();
		try {
			List<String> serverPaths = client.getChildren().forPath(Constants.SERVER_PATH);
			//zookeeper listener
			CuratorWatcher watcher = new ServerWatcher();
			client.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);
			
			for (String serverPath : serverPaths) {
				String[] str = serverPath.split("#");
				ChannelManager.realServerPath.add(str[0] + "#" + str[1]);
				ChannelFuture channelFuture = TcpClient.b.connect(str[0], Integer.valueOf(str[1]));
				ChannelManager.add(channelFuture);
			}
			
			
			
			if (ChannelManager.realServerPath.size() > 0) {
				String[] hostAndPort = ChannelManager.realServerPath.toArray()[0].toString().split("#");
				host = hostAndPort[0];
				port = Integer.valueOf(hostAndPort[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
//        try {
//        	f = b.connect(host, port).sync();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
		
	
	public static Response send(ClientRequest request) {
 		f = ChannelManager.get(ChannelManager.position);
		f.channel().writeAndFlush(JSONObject.toJSONString(request));
		f.channel().writeAndFlush("\r\n");
		DefaultFuture df = new DefaultFuture(request);
		return df.get();
	}
}

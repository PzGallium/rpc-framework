package io.github.PzGallium.rpc.netty.handler;

import com.alibaba.fastjson.JSONObject; 

import io.github.PzGallium.rpc.netty.client.DefaultFuture;
import io.github.PzGallium.rpc.netty.util.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {	
		if("ping".equals(msg.toString())) {
			System.out.println("Recieve idle ping. Sending pong to the server");
			ctx.channel().writeAndFlush("pong\r\n");
			return;
		}
		
		//ctx.channel().attr(AttributeKey.valueOf("xxxx")).set(msg);
		Response response = JSONObject.parseObject(msg.toString(), Response.class);
		DefaultFuture.recieve(response);
		// ctx.channel().close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	}

	
	

}

package io.github.PzGallium.rpc.netty.handler;

import com.alibaba.fastjson.JSONObject;

import io.github.PzGallium.rpc.netty.handler.param.ServerRequest;
import io.github.PzGallium.rpc.netty.medium.Medium;
import io.github.PzGallium.rpc.netty.util.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		ServerRequest request = JSONObject.parseObject(msg.toString(), ServerRequest.class);
		Medium medium = Medium.newInstance();
		Response result = medium.process(request);
		System.out.println("Server：" + JSONObject.toJSONString(result));
		ctx.channel().writeAndFlush(JSONObject.toJSONString(result));
		ctx.channel().writeAndFlush("\r\n");
	}

//	@Override
//	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//		if (evt instanceof IdleStateEvent) {
//			IdleStateEvent event = (IdleStateEvent) evt;
//			if(event.state().equals(IdleState.READER_IDLE)) {
//				System.out.println("reader idle----channel closed");
//				//ctx.channel().close();
//			} else if (event.state().equals(IdleState.WRITER_IDLE)) {
//				System.out.println("writer idle----");
//			} else if (event.state().equals(IdleState.ALL_IDLE)) {
//				System.out.println("all idle-----heartbeat");
//				ctx.channel().writeAndFlush("ping\r\n");
//			}
//		}
//	}
	
	

}

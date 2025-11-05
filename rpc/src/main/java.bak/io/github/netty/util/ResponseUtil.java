package io.github.netty.util;

public class ResponseUtil {
	
	public static Response createSuccessResponse() {
		return new Response();
	}
	
	public static Response createFailResponse(String code, String msg) {
		Response Response = new Response();
		Response.setCode(code);
		Response.setMsg(msg);	
		return Response;
	}
	
	public static Response createSuccessResponse(Object content) {
		Response response = new Response();
		response.setResult(content);
		return response;
		
	}
}

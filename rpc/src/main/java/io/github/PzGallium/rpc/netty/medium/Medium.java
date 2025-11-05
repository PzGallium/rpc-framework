package io.github.PzGallium.rpc.netty.medium;

import java.lang.reflect.Method;
import java.util.HashMap;
import com.alibaba.fastjson.JSONObject;

import io.github.PzGallium.rpc.netty.handler.param.ServerRequest;
import io.github.PzGallium.rpc.netty.util.Response;

public class Medium {
	private static Medium m = null;
	public static final HashMap<String, BeanMethod> beanMap = new HashMap<String,BeanMethod>();
	
	private Medium() {}
	
	public static Medium newInstance() {
		return m == null? new Medium() : m;
	}

	
	//use reflection
	public Response process(ServerRequest request) {
		Response result = null;
		
		try {
			String command = request.getCommand();
			BeanMethod beanMethod = beanMap.get(command);  
			if (beanMethod == null) return null;
			Object bean = beanMethod.getBean();
			Method m = beanMethod.getMethod();
			Class paramType = m.getParameterTypes()[0];
			Object content = request.getContent();
			Object args = JSONObject.parseObject(JSONObject.toJSONString(content), paramType);
			result = (Response) m.invoke(bean, args);
			result.setId(request.getId());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}

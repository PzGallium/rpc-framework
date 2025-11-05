package io.github.PzGallium.consumer.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import io.github.PzGallium.consumer.annotation.RemoteInvoke;
import io.github.PzGallium.consumer.core.TcpClient;
import io.github.PzGallium.consumer.param.ClientRequest;
import io.github.PzGallium.consumer.param.Response;

@Component
public class InvokeProxy implements BeanPostProcessor{

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(RemoteInvoke.class)) {
				field.setAccessible(true);
				final Map<Method, Class> methodClassMap = new HashMap<>();
				putMethodClass(methodClassMap, field);
				Enhancer enhancer = new Enhancer();
				enhancer.setInterfaces(new Class[] {field.getType()});
				enhancer.setCallback(new MethodInterceptor() {

					@Override
					public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy)
							throws Throwable {
						
						ClientRequest request = new ClientRequest();
						request.setContent(args[0]);
//						String command= methodClassMap.get(method).getName()+"."+method.getName();  
						String command = method.getName();
						request.setCommand(command);
						Response resp = TcpClient.send(request);
						return resp;
					}
					
				});
				
				try {
					field.set(bean, enhancer.create());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return bean;
	}


	private void putMethodClass(Map<Method, Class> methodClassMap, Field field) {
		Method[] methods = field.getType().getDeclaredMethods();
		for (Method m : methods) {
			methodClassMap.put(m, field.getType());
		}
		
	}


	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		
		return bean;
	}

	
	
}

package io.github.PzGallium.rpc.netty.client;

import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.github.PzGallium.rpc.netty.util.Response;

public class DefaultFuture {
	private Response response;
	public static final ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<>();
	final Lock lock = new ReentrantLock();
	public Condition condition = lock.newCondition();
	
	public DefaultFuture(ClientRequest request) {
		allDefaultFuture.put(request.getId(), this);
	}

	public Response get() {
		lock.lock();
		try {
			while(!done()) {
				condition.await();
			}
		}catch (Exception e) {
			
		}finally {
			lock.unlock();
		}
		return this.response;
	}

	public static void recieve(Response response) {
		DefaultFuture df = allDefaultFuture.get(response.getId());
		if (df != null) {
			Lock lock = df.lock;
			lock.lock();
			try {
				df.setResponse(response);
				df.condition.signal();
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				lock.unlock();
			}
		}
	}
	
	
	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	private boolean done() {
		if(this.response != null) return true;
		return false;
	}

}

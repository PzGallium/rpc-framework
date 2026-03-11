package io.github.PzGallium.consumer.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.github.PzGallium.consumer.param.ClientRequest;
import io.github.PzGallium.rpc.netty.util.Response;

public class DefaultFuture {
	private Response response;
	public static final ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<>();
	final Lock lock = new ReentrantLock();
	public Condition condition = lock.newCondition();
	private long timeout = 2*60*1000l;
	private long startTime = System.currentTimeMillis();
	
	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public long getStartTime() {
		return startTime;
	}

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

	public Response get(long time) {
		lock.lock();
		try {
			while(!done()) {
				condition.await(time, TimeUnit.SECONDS);
				if(System.currentTimeMillis() - startTime > time) {
					System.out.println("Request timeout");
					break;
				}
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
	
	static class FutureThread extends Thread {

		@Override
		public void run() {
			Set<Long> ids = allDefaultFuture.keySet();
			for (Long id : ids) {
				DefaultFuture df = allDefaultFuture.get(id);
				if(df == null) {
					allDefaultFuture.remove(df);
				} else {
					//timeout
					if(df.getTimeout() < System.currentTimeMillis() - df.getStartTime()) {
						Response resp = new Response();
						resp.setId(id);
						resp.setCode("333333");
						resp.setMsg("timeout");
						recieve(resp);
					}
				}
			}
		}
	}
	
	static {
		FutureThread futureThread = new FutureThread();
		futureThread.setDaemon(true);
		futureThread.start();
	}
}

package io.github.PzGallium.consumer.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;

import io.github.PzGallium.consumer.annotation.RemoteInvoke;
import io.github.PzGallium.consumer.core.TcpClient;
import io.github.PzGallium.consumer.param.ClientRequest;
import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.user.bean.User;
import io.github.PzGallium.rpc.user.remote.UserRemote;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RemoteInvokingTest.class)
@ComponentScan("io.github.PzGallium")
public class RemoteInvokingTest {

	@RemoteInvoke
	private UserRemote userRemote;
	
	
	@Test
	public void testGetResponse() {
		ClientRequest request = new ClientRequest();
		request.setContent("Testing TCP connection");
		Response resp = TcpClient.send(request);
		System.out.println(resp.getResult());
	}
	
	@Test
	public void testSaveUser() {
		User u = new User();
		u.setId(1);
		u.setName("Samuel");
		Response resp =  userRemote.saveUser(u);
		System.out.println(JSONObject.toJSONString(resp));
	}
	
	@Test
	public void testSaveUsers() {
		List<User> users = new ArrayList<>();
		User u = new User();
		u.setId(1);
		u.setName("Samuel");
		users.add(u);

		Response resp = userRemote.saveUsers(users);
		System.out.println(resp.getResult());
	}
}

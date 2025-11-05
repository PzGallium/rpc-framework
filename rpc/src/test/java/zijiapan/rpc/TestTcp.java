package zijiapan.rpc;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.github.PzGallium.rpc.netty.client.ClientRequest;
import io.github.PzGallium.rpc.netty.client.TcpClient;
import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.user.bean.User;

public class TestTcp {
	@Test
	public void testGetResponse() {
		ClientRequest request = new ClientRequest();
		request.setContent("Testing TCP connection");
		Response resp = TcpClient.send(request);
		System.out.println(resp.getResult());
	}
	
	@Test
	public void testSaveUser() {
		ClientRequest request = new ClientRequest();
		User u = new User();
		u.setId(1);
		u.setName("Samuel");
		request.setContent(u);
		request.setCommand("io.github.PzGallium.user.controller.UserController.saveUser");
		Response resp = TcpClient.send(request);
		System.out.println(resp.getResult());
	}
	
	@Test
	public void testSaveUsers() {
		ClientRequest request = new ClientRequest();
		List<User> users = new ArrayList<>();
		User u = new User();
		u.setId(1);
		u.setName("Samuel");
		users.add(u);
		request.setContent(users);
		request.setCommand("io.github.PzGallium.user.controller.UserController.saveUsers");
		Response resp = TcpClient.send(request);
		System.out.println(resp.getResult());
	}
}

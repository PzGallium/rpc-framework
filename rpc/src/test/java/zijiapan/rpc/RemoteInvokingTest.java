package zijiapan.rpc;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.github.PzGallium.rpc.netty.annotation.RemoteInvoke;
import io.github.PzGallium.rpc.netty.client.ClientRequest;
import io.github.PzGallium.rpc.netty.client.TcpClient;
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
		ClientRequest request = new ClientRequest();
		User u = new User();
		u.setId(1);
		u.setName("Samuel");
		userRemote.saveUser(u);
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
		userRemote.saveUsers(users);
		Response resp = TcpClient.send(request);
		System.out.println(resp.getResult());
	}
}

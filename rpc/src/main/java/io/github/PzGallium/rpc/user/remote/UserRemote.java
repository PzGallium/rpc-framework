package io.github.PzGallium.rpc.user.remote;

import java.util.List;

import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.user.bean.User;

public interface UserRemote {

	public Response saveUser(User user);
	
	
	public Response saveUsers(List<User> users);
	
}

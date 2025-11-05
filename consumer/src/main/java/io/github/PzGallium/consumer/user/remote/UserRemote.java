package io.github.PzGallium.consumer.user.remote;

import java.util.List;

import io.github.PzGallium.consumer.param.Response;
import io.github.PzGallium.consumer.user.bean.User;


public interface UserRemote {

	public Response saveUser(User user);
	
	
	public Response saveUsers(List<User> users);
	
}

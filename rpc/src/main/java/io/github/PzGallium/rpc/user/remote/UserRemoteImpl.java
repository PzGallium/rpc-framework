package io.github.PzGallium.rpc.user.remote;

import java.util.List;

import javax.annotation.Resource;

import io.github.PzGallium.rpc.netty.annotation.Remote;
import io.github.PzGallium.rpc.netty.util.Response;
import io.github.PzGallium.rpc.netty.util.ResponseUtil;
import io.github.PzGallium.rpc.user.bean.User;
import io.github.PzGallium.rpc.user.service.UserService;

@Remote
public class UserRemoteImpl implements UserRemote{
	
	@Resource
	private UserService userService;
	
	public Response saveUser(User user) {
		userService.save(user);
		return ResponseUtil.createSuccessResponse(user);
	}
	
	public Response saveUsers(List<User> users) {
		userService.saveList(users);
		return ResponseUtil.createSuccessResponse(users);
	}
}

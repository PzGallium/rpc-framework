package io.github.user.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import io.github.netty.util.Response;
import io.github.netty.util.ResponseUtil;
import io.github.user.bean.User;
import io.github.user.service.UserService;


@Controller
public class UserController {
	
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

package com.javachina.service.impl;

import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.Service;
import com.blade.jdbc.ActiveRecord;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.javachina.config.DBConfig;
import com.javachina.model.Activecode;
import com.javachina.model.User;
import com.javachina.service.ActivecodeService;
import com.javachina.service.SendMailService;
import com.javachina.service.UserService;
import com.javachina.service.UserinfoService;

@Service
public class ActivecodeServiceImpl implements ActivecodeService {

	private ActiveRecord activeRecord = DBConfig.activeRecord;

	@Inject
	private SendMailService sendMailService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private UserinfoService userinfoService;
	
	@Override
	public Activecode getActivecode(String code) {
		if(StringKit.isBlank(code)){
			return null;
		}
		Activecode temp = new Activecode();
		temp.setCode(code);
		return activeRecord.one(temp);
	}
	
	public Activecode getActivecodeById(Integer id) {
		if(null == id){
			return null;
		}
		return activeRecord.byId(Activecode.class, id);
	}
		
	@Override
	public String save(User user, String type) {
		
		if(null == user || StringKit.isBlank(type)){
			return null;
		}
		
		int time = DateKit.getCurrentUnixTime();
		int expires_time = time + 3600;
		String code = StringKit.getRandomChar(32);
		try {

			Activecode activecode = new Activecode();
			activecode.setUid(user.getUid());
			activecode.setCode(code);
			activecode.setType(type);
			activecode.setExpires_time(expires_time);
			activecode.setCreate_time(time);

			activeRecord.insert(activecode);
			
			userinfoService.save(user.getUid());
			
			if(type.equals("signup")){
				sendMailService.signup(user.getLogin_name(), user.getEmail(), code);
			}
			
			if(type.equals("forgot")){
				sendMailService.forgot(user.getLogin_name(), user.getEmail(), code);
			}
			
			return code;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean useCode(String code) {
		if(StringKit.isBlank(code)){
			return false;
		}
		activeRecord.execute("update t_activecode set is_use = 1 where code = '"+code+"'");
		return true;
	}

	@Override
	public boolean resend(Integer uid) {
		if(null != uid){
			User user = userService.getUser(uid);
			if(null == user){
				return false;
			}
			
			int time = DateKit.getCurrentUnixTime();
			int expires_time = time + 3600;
			String code = StringKit.getRandomChar(32);
			try {

				Activecode activecode = new Activecode();
				activecode.setUid(user.getUid());
				activecode.setCode(code);
				activecode.setType("signup");
				activecode.setExpires_time(expires_time);
				activecode.setCreate_time(time);

				activeRecord.insert(activecode);

				sendMailService.signup(user.getLogin_name(), user.getEmail(), code);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
}

package com.javachina;

import com.blade.Blade;
import com.blade.kit.base.Config;
import jetbrick.template.JetGlobalContext;

import java.util.HashMap;
import java.util.Map;

public class Constant {

	public static Config config = Blade.$().config();

	/**
	 * 登录用户session key
	 */
	public static final String LOGIN_SESSION_KEY = "login_user";

	public static final String UPLOAD_FOLDER = "assets/temp";

	public static final String USER_IN_COOKIE = "SH_SIGNIN_USER";
	public static final String JC_REFERRER_COOKIE = "JC_REFERRER_COOKIE";

	public static String SITE_URL = config.get("app.site_url");
	public static String CDN_URL = config.get("app.cdn_url");

	/**
	 * github密钥配置
	 */
	public static String GITHUB_CLIENT_ID = config.get("github.client_id");
	public static String GITHUB_CLIENT_SECRET = config.get("github.client_secret");
	public static String GITHUB_REDIRECT_URL = config.get("github.redirect_url");

	/**
	 * 邮件配置
	 */
	public static String MAIL_HOST = config.get("mail.smtp.host");
	public static String MAIL_USER = config.get("mail.user");
	public static String MAIL_USERNAME = config.get("mail.from");
	public static String MAIL_PASS = config.get("mail.pass");
	public static int MIAL_PORT = config.getInt("mail.smtp.port");
	
	public static JetGlobalContext VIEW_CONTEXT = null;
	public static Map<String, Object> SYS_INFO = new HashMap<>();

}

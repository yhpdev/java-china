package com.javachina.config;

import com.blade.annotation.Order;
import com.blade.config.BaseConfig;
import com.blade.config.Configuration;
import com.blade.ioc.annotation.Component;
import com.blade.kit.AES;
import com.javachina.Constant;

/**
 * Created by biezhi on 2016/12/25.
 */
@Component
@Order(sort = 3)
public class ConstConfig implements BaseConfig {

    @Override
    public void config(Configuration configuration) {
        // 初始化配置
        Constant.IS_DEV = configuration.isDev();
        Constant.SITE_URL = configuration.config().get("app.site_url");
        Constant.APP_VERSION = configuration.config().get("app.version");
        AES.setKey(configuration.config().get("app.aes_salt"));
        Constant.CDN_URL = configuration.config().get("qiniu.cdn");
        Constant.FAMOUS_KEY = configuration.config().get("famous.key");

        // 配置邮箱
        Constant.MAIL_API_USER = configuration.config().get("mail.api_user");
        Constant.MAIL_API_KEY = configuration.config().get("mail.api_key");

        // github授权key
        Constant.GITHUB_CLIENT_ID = configuration.config().get("github.CLIENT_ID");
        Constant.GITHUB_CLIENT_SECRET = configuration.config().get("github.CLIENT_SECRET");
        Constant.GITHUB_REDIRECT_URL = configuration.config().get("github.REDIRECT_URL");

        Constant.VIEW_CONTEXT.set(String.class, "site_url", Constant.SITE_URL);

    }
}

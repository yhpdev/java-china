package com.javachina.kit;

import com.javachina.Constant;
import org.apache.commons.mail.HtmlEmail;

/**
 * Created by biezhi on 2016/12/30.
 */
public class MailKit {

    public static void sendForgot(String login_name, String email, String code) {

    }

    public static void sendSignup(String login_name, String to_addr, String code){
        String content = "";
        send(login_name + ", 欢迎你加入" + Constant.MAIL_USERNAME, to_addr, content);
    }

    public static void send(String subject, String to_addr, String content){
        try {
            // Create the email message
            HtmlEmail email = new HtmlEmail();
            email.setHostName(Constant.MAIL_HOST);
            email.addTo(to_addr);
//			email.setStartTLSEnabled(true);
            email.setFrom(Constant.MAIL_USER, Constant.MAIL_USERNAME);
            email.setAuthentication(Constant.MAIL_USER, Constant.MAIL_PASS);
            email.setCharset("UTF-8");

            if(content.length()<6)
                content = "您的验证码是：asdsad。请不要把验证码泄露给其他人。如非本人操作，可不用理会！";

            email.setSubject(subject);
            // set the html message
			email.setHtmlMsg(content);
            // send the email
            email.send();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}

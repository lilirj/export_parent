package com.itit.web.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import sun.security.provider.MD5;

/**
 * 自定义凭证匹配器, 自己指定对用户输入的密码的加密方式
 */
public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {
    /**
     * 密码校验的方法
     * @param token 封装用户输入的信息
     * @param info  认证的信息
     * @return 返回true表示用户输入的密码是正确的；否则错误。
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

        //转换
        UsernamePasswordToken userToken=(UsernamePasswordToken) token;

        //获得用户输入的密码
        String password =String.valueOf(userToken.getPassword());

        //获得用户输入的账号
        String username = userToken.getUsername();

        //进行加密 加盐
        String encodePwd = new Md5Hash(password, username).toString();
        System.out.println(encodePwd);

        //获得数据库中正确的密码
        String dbpwd =(String) info.getCredentials();

        //用户输入的加密后的密码与数据库中的密码进行对比
        boolean equals = encodePwd.equals(dbpwd);
        return equals;
    }
}

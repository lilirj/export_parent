package com.itit.web.shiro;

import com.itit.domain.system.Module;
import com.itit.domain.system.User;
import com.itit.service.system.ModuleService;
import com.itit.service.system.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 自定义的realm，
 * 1. 作用是访问我们的权限数据
 * 2. 当我们做认证授权操作时候，需要查询数据库，在这里完成数据库的查询操作
 */
public class AuthRealm extends AuthorizingRealm {
    //AuthRealm 可以查询数据库中的权限数据

    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;

    // 授权方法：当调用subject.checkPermission()方法时候，自动来到此方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        /* 需求： 告诉shiro当前登录用户有哪些权限  */

        //1. 获取登录认证的对象；其实就是认证方法返回对象的构造函数的第一个参数
        User loninUser =(User) principalCollection.getPrimaryPrincipal();

        //2.根据用户id，查询登陆用户所具有的权限
        List<Module> moduleList = moduleService.findModuleByUserId(loninUser.getId());

        //3. 返回，告诉shiro用户有哪些权限
        SimpleAuthorizationInfo sia = new SimpleAuthorizationInfo();
        if (moduleList!=null && moduleList.size()>0){
            for (Module module : moduleList) {
                // 添加权限（告诉shiro用户的权限）
                sia.addStringPermission(module.getName());
            }
        }
        return sia;
    }


    /**
     * 认证方法：当调用subject.login()方法时候，自动来到这里
     * 1. 如果当前方法返回NULL, 报错：UnknownAccountException 未知账号异常
     * 2. shiro如何进行登录认证校验？
     *    2.1 用户名是否再数据库存在，我们自己判断。我们根据用户名查询数据库
     *    2.2 shiro只要判断密码即可？
     *        A. shiro要知道用户输入的密码  （我们有告诉shiro）
     *        B. shiro还要知道数据库中正确的密码 （我们有告诉shiro）
     */
    // 认证方法：当调用subject.login()方法时候，自动来到这里
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        //转换
        UsernamePasswordToken token=(UsernamePasswordToken) authenticationToken;

        //获得用户输入的email（账号）
        String email = token.getUsername();

        //根据用户输入的email查询数据库
        User user = userService.findUserByEmail(email);
        if (user==null){
            return null; // 这里报错：UnknownAccountException
        }
        //获得数据库中的正确密码
        String dbpsw = user.getPassword();

        // 返回对象
        // 参数1：认证的身份对象，这里的参数1通过 subject.getPrincipal() 获取
        // 参数2：数据库中正确的密码
        // 参数3：realm的名称，可以随便给，但有多个realm要保证唯一。
        SimpleAuthenticationInfo sia = new SimpleAuthenticationInfo(user,dbpsw,getName());
        return sia;
    }

}

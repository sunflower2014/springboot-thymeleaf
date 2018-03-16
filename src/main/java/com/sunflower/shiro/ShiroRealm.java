package com.sunflower.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class ShiroRealm extends AuthorizingRealm {
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
//		User userInfo = (User) principals.fromRealm(this.getClass().getName()).iterator().next();
//		if (userInfo != null) {
//			authorizationInfo.addRole("admin");
//			authorizationInfo.addStringPermission("usermanager");
//			return authorizationInfo;
//		}
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
//		User hasUser = userService.selectByUserName(token.getUsername());
//		if (hasUser != null) {
//			return new SimpleAuthenticationInfo(hasUser, hasUser.getPassword(), this.getClass().getName());
//		}
		return null;
	}

}

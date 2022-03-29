package com.lyj.vueblog.shiro;

import cn.hutool.json.JSONUtil;
import com.lyj.vueblog.common.Result;
import com.lyj.vueblog.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("Authorization");

        // 没有token 游客 无需登录
        if (!StringUtils.hasText(token)) {
            return null;
        }

        return new JwtToken(token);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("Authorization");

        // 没有token 游客 无需登录
        if (!StringUtils.hasText(token)) {
            return true;
        }

        // 检验jwt
        Claims claim = jwtUtils.getClaimByToken(token);
        if (claim == null || jwtUtils.isTokenExpired(claim.getExpiration())) {
            throw new ExpiredCredentialsException("token已失效，请重新登录");
        }

        // 执行登录
        return executeLogin(servletRequest, servletResponse);
    }

    /*登陆失败执行该方法*/
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        Throwable throwable = e.getCause() == null ? e : e.getCause();
        Result fail = Result.fail(throwable.getMessage());
        String jsonStr = JSONUtil.toJsonStr(fail);
        try {
            httpServletResponse.getWriter().println(jsonStr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}

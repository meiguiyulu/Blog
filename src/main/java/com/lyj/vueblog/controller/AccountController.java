package com.lyj.vueblog.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lyj.vueblog.common.Result;
import com.lyj.vueblog.dto.LoginDTO;
import com.lyj.vueblog.pojo.User;
import com.lyj.vueblog.service.IUserService;
import com.lyj.vueblog.utils.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
public class AccountController {

    @Autowired
    IUserService userService;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * 登录
     * @param loginDTO
     * @param response
     * @return
     */
    @RequestMapping("/login")
    public Result login(@Validated @RequestBody LoginDTO loginDTO,
                        HttpServletResponse response) {
        String username = loginDTO.getUsername();
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("username", username);
        User one = userService.getOne(wrapper);
        Assert.notNull(one, "用户不存在");
        if (!one.getPassword().equals(SecureUtil.md5(loginDTO.getPassword()))) {
            return Result.fail("密码错误！");
        }
        String token = jwtUtils.generateToken(one.getId());
        response.setHeader("Authorization", token);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        one.setLastLogin(new Date());
        userService.updateById(one);
        return Result.success(one);
    }

    /**
     * 退出登录
     * @return
     */
    @GetMapping("/logout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.success(null);
    }

}

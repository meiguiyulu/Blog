package com.lyj.vueblog.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.vueblog.common.Result;
import com.lyj.vueblog.pojo.Blog;
import com.lyj.vueblog.service.IBlogService;
import com.lyj.vueblog.utils.ShiroUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-03-29
 */
@RestController
public class BlogController {

    @Autowired
    IBlogService blogService;

    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage,
                       @RequestParam(defaultValue = "5") Integer pageSize) {
        if (currentPage < 1) currentPage = 1;

        QueryWrapper<Blog> wrapper = new QueryWrapper<Blog>()
                .orderByDesc("created");
        return Result.success(blogService.page(new Page(currentPage, pageSize), wrapper));
    }

    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable(name = "id") Long id) {
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "该博客已被删除");
        return Result.success(blog);
    }

    @RequiresAuthentication
    @PostMapping("/blog/edit")
    public Result editOrSave(@Validated @RequestBody Blog blog) {
        Blog temp;

        if (blog.getId() != null) {
            // 更新
            temp = blogService.getById(blog);
            Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getId().longValue(), "没有权限编辑");
            blog.setModified(new Date());
        } else {
            // 添加博客
            temp = new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(new Date());
            temp.setModified(new Date());
            temp.setStatus(0);
            temp.setViews(0);
        }
        BeanUtil.copyProperties(blog, temp, "id", "userId", "created", "modified", "views");
        blogService.saveOrUpdate(temp);
        return Result.success(null);
    }

}

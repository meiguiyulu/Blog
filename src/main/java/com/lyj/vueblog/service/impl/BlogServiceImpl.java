package com.lyj.vueblog.service.impl;

import com.lyj.vueblog.pojo.Blog;
import com.lyj.vueblog.mapper.BlogMapper;
import com.lyj.vueblog.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-03-29
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

}

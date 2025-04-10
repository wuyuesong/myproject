package com.hmdp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.Follow;
import com.hmdp.mapper.FollowMapper;
import com.hmdp.service.IFollowService;
import com.hmdp.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Result follow(Long followUserId, boolean isFollow) {
        Long userId = UserHolder.getUser().getId();
        String key = "follow:" + userId;
        // 1. 判断是关注还是取关
        if (isFollow) {
            //2.关注，新增数据
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            boolean isSuccess = save(follow);

            if (isSuccess) {
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }
        } else {
            //3.取关，删除
            boolean isSuccess = remove(new QueryWrapper<Follow>()
                    .eq("user_id", userId).eq("follow_user_id", followUserId));
            if (isSuccess) {
                stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
            }

        }

        return Result.ok();
    }

    @Override
    public Result isFollow(Long followUserId) {
        Long userId = UserHolder.getUser().getId();
        //1.查询
        Long count = query().eq("user_id", userId).eq("follow_user_id", followUserId).count();


        return Result.ok(count > 0);
    }
}

package top.hcode.hoj.dao.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.hcode.hoj.dao.user.UserThirdAuthEntityService;
import top.hcode.hoj.mapper.UserThirdAuthMapper;
import top.hcode.hoj.pojo.entity.user.UserThirdAuth;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: 第三方账号绑定 Service 实现类
 */
@Service
public class UserThirdAuthEntityServiceImpl extends ServiceImpl<UserThirdAuthMapper, UserThirdAuth> implements UserThirdAuthEntityService {

    @Resource
    private UserThirdAuthMapper userThirdAuthMapper;

    @Override
    public UserThirdAuth getByPlatformAndOpenId(String platform, String openId) {
        return userThirdAuthMapper.selectByPlatformAndOpenId(platform, openId);
    }

    @Override
    public UserThirdAuth getByUidAndPlatform(String uid, String platform) {
        return userThirdAuthMapper.selectByUidAndPlatform(uid, platform);
    }

    @Override
    public List<UserThirdAuth> getByUid(String uid) {
        QueryWrapper<UserThirdAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return this.list(queryWrapper);
    }

    @Override
    public boolean unbind(String uid, String platform) {
        QueryWrapper<UserThirdAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid).eq("platform", platform);
        return this.remove(queryWrapper);
    }
}

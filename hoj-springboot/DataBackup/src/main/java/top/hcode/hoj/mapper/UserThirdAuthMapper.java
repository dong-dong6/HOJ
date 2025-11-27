package top.hcode.hoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.hcode.hoj.pojo.entity.user.UserThirdAuth;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: 第三方账号绑定 Mapper 接口
 */
@Mapper
@Repository
public interface UserThirdAuthMapper extends BaseMapper<UserThirdAuth> {

    /**
     * 根据平台和openId查询绑定信息
     */
    UserThirdAuth selectByPlatformAndOpenId(@Param("platform") String platform, @Param("openId") String openId);

    /**
     * 根据用户ID和平台查询绑定信息
     */
    UserThirdAuth selectByUidAndPlatform(@Param("uid") String uid, @Param("platform") String platform);
}

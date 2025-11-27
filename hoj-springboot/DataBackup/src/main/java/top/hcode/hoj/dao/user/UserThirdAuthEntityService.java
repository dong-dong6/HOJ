package top.hcode.hoj.dao.user;

import com.baomidou.mybatisplus.extension.service.IService;
import top.hcode.hoj.pojo.entity.user.UserThirdAuth;

import java.util.List;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: 第三方账号绑定 Service 接口
 */
public interface UserThirdAuthEntityService extends IService<UserThirdAuth> {

    /**
     * 根据平台和openId查询绑定信息
     */
    UserThirdAuth getByPlatformAndOpenId(String platform, String openId);

    /**
     * 根据用户ID和平台查询绑定信息
     */
    UserThirdAuth getByUidAndPlatform(String uid, String platform);

    /**
     * 根据用户ID查询所有绑定信息
     */
    List<UserThirdAuth> getByUid(String uid);

    /**
     * 解绑第三方账号
     */
    boolean unbind(String uid, String platform);
}

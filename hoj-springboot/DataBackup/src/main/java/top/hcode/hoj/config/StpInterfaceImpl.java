package top.hcode.hoj.config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;
import top.hcode.hoj.dao.user.UserRoleEntityService;
import top.hcode.hoj.mapper.RoleAuthMapper;
import top.hcode.hoj.pojo.entity.user.Auth;
import top.hcode.hoj.pojo.entity.user.Role;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Himit_ZH
 * @Date: 2020/7/19 22:57
 * @Description: Sa-Token 权限认证接口实现类
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private UserRoleEntityService userRoleEntityService;

    @Resource
    private RoleAuthMapper roleAuthMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> permissionsNameList = new LinkedList<>();
        String uid = (String) loginId;
        List<Role> roles = userRoleEntityService.getRolesByUid(uid);
        for (Role role : roles) {
            for (Auth auth : roleAuthMapper.getRoleAuths(role.getId()).getAuths()) {
                permissionsNameList.add(auth.getPermission());
            }
        }
        return permissionsNameList;
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roleNameList = new LinkedList<>();
        String uid = (String) loginId;
        List<Role> roles = userRoleEntityService.getRolesByUid(uid);
        for (Role role : roles) {
            roleNameList.add(role.getRole());
        }
        return roleNameList;
    }
}

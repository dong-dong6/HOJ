package top.hcode.hoj.pojo.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: 第三方账号绑定实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_third_auth")
@ApiModel(value = "UserThirdAuth对象", description = "第三方账号绑定表")
public class UserThirdAuth implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "第三方平台类型：github, gitee, gitlab, custom等")
    private String platform;

    @ApiModelProperty(value = "第三方平台的用户唯一标识")
    private String openId;

    @ApiModelProperty(value = "第三方平台的用户名")
    private String username;

    @ApiModelProperty(value = "第三方平台的昵称")
    private String nickname;

    @ApiModelProperty(value = "第三方平台的头像")
    private String avatar;

    @ApiModelProperty(value = "第三方平台的邮箱")
    private String email;

    @ApiModelProperty(value = "第三方平台返回的原始用户信息JSON")
    private String rawUserInfo;

    @ApiModelProperty(value = "access_token")
    private String accessToken;

    @ApiModelProperty(value = "refresh_token")
    private String refreshToken;

    @ApiModelProperty(value = "token过期时间")
    private Long expireIn;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}

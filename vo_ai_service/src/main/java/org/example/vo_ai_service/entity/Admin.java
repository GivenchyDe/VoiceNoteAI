package org.example.vo_ai_service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Data;

@TableName(value = "admin")
@Data
public class Admin {
    @Schema(description = "管理员ID", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "管理员用户名", example = "admin")
    private String username;

    @Schema(description = "管理员密码", example = "password")
    private String password;

    @Schema(description = "管理员角色", example = "超级管理员")
    private String role;

    @Schema(description = "管理员状态", example = "1")
    private Integer status;

    @Schema(description = "管理员创建时间", example = "2021-01-01 00:00:00")
    private Date createTime;
}

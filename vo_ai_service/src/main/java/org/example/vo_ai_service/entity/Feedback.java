package org.example.vo_ai_service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 反馈表
 * @TableName feedback
 */
@TableName(value ="feedback")
@Data
public class Feedback {
    /**
     * 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String username;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 附件地址（截图/音频）
     */
    private String attachment;

    /**
     * 处理状态（0-未处理，1-处理中，2-已处理）
     */
    private Integer status;

    /**
     * 管理员回复
     */
    private String reply;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
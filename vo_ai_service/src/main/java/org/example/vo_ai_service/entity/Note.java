package org.example.vo_ai_service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 笔记表
 * @TableName note
 */
@TableName(value ="note")
@Data
public class Note {
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
     * 笔记标题
     */
    private String title;

    /**
     * 笔记内容（识别后的文字）
     */
    private String content;

    /**
     * 笔记分类
     */
    private String category;

    /**
     * 标签，逗号分隔
     */
    private String tags;

    /**
     * 状态（0-归档，1-正常）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
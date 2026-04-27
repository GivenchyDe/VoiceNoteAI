package org.example.vo_ai_service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 识别日志表
 * @TableName recognition_log
 */
@TableName(value ="recognition_log")
@Data
public class RecognitionLog {
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
     * 关联音频文件ID
     */
    private Long audioId;

    /**
     * 识别结果文字
     */
    private String result;

    /**
     * 识别耗时（毫秒）
     */
    private Integer duration;

    /**
     * 识别准确率（%）
     */
    private BigDecimal accuracy;

    /**
     * 识别引擎（讯飞/百度）
     */
    private String engine;

    /**
     * 识别状态（0-失败，1-成功）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;
}
package cn.fufeii.ds.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 会员账户
 *
 * @author FuFei
 */
@Data
@TableName(value = "ds_account")
public class Account {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 会员主键
     */
    @TableField
    private Long memberId;

    /**
     * 历史总金额
     */
    @TableField
    private Integer moneyTotalHistory;

    /**
     * 总金额
     */
    @TableField
    private Integer moneyTotal;

    /**
     * 可用金额
     */
    @TableField
    private Integer moneyAvailable;

    /**
     * 冻结金额
     */
    @TableField
    private Integer moneyFrozen;

    /**
     * 历史总积分
     */
    @TableField
    private Integer pointsTotalHistory;

    /**
     * 总积分
     */
    @TableField
    private Integer pointsTotal;

    /**
     * 可用积分
     */
    @TableField
    private Integer pointsAvailable;

    /**
     * 冻结积分
     */
    @TableField
    private Integer pointsFrozen;

    /**
     * 乐观锁
     */
    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDateTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createDateTime;

}
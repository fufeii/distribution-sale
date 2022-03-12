package cn.fufeii.ds.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 提现申请
 *
 * @author FuFei
 */
@Data
@TableName(value = "ds_withdraw_apply")
public class WithdrawApply {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 用户主键
     */
    @TableField
    private Long memberId;

    /**
     * 提现单号
     */
    @TableField
    private String withdrawNumber;

    /**
     * 提现金额
     */
    @TableField
    private Long withdrawAmount;

    /**
     * 手续费
     */
    @TableField
    private Long feeAmount;

    /**
     * 审批时间
     */
    @TableField
    private Date approvalTime;

    /**
     * 状态
     */
    @TableField
    private Integer state;

    /**
     * 备注
     */
    @TableField
    private String memo;

    /**
     * 乐观锁
     */
    @Version
    @TableField
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
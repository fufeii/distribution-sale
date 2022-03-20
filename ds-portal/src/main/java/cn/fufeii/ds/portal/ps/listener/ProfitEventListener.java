package cn.fufeii.ds.portal.ps.listener;

import cn.fufeii.ds.portal.ps.event.InviteEvent;
import cn.fufeii.ds.portal.ps.event.MoneyEvent;
import cn.fufeii.ds.portal.ps.event.UpgradeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 分销事件监听器
 *
 * @author FuFei
 * @date 2022/3/20
 */
@Component
public class ProfitEventListener {

    /**
     * 监听会员邀请事件
     */
    @Async
    @TransactionalEventListener
    public void handle(InviteEvent inviteEvent) {
        // 执行邀请分润机制
    }

    /**
     * 监听段位升级事件
     */
    @Async
    @TransactionalEventListener
    public void handle(UpgradeEvent upgradeEvent) {
        // 执行邀请分润机制
    }


    /**
     * 监听货币交易事件
     */
    @EventListener
    public void handle(MoneyEvent moneyEvent) {
        // 执行交易分润机制
    }


}
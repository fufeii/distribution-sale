package cn.fufeii.ds.server.service;

import cn.fufeii.ds.common.annotation.GlobalLock;
import cn.fufeii.ds.common.enumerate.ExceptionEnum;
import cn.fufeii.ds.common.enumerate.biz.AccountTypeEnum;
import cn.fufeii.ds.common.enumerate.biz.ChangeTypeEnum;
import cn.fufeii.ds.common.enumerate.biz.StateEnum;
import cn.fufeii.ds.common.exception.BizException;
import cn.fufeii.ds.common.util.PageUtil;
import cn.fufeii.ds.repository.crud.CrudAccountChangeRecordService;
import cn.fufeii.ds.repository.crud.CrudAccountService;
import cn.fufeii.ds.repository.crud.CrudMemberService;
import cn.fufeii.ds.repository.entity.Account;
import cn.fufeii.ds.repository.entity.AccountChangeRecord;
import cn.fufeii.ds.repository.entity.Member;
import cn.fufeii.ds.server.config.constant.DsServerConstant;
import cn.fufeii.ds.server.model.api.request.AccountChangeRequest;
import cn.fufeii.ds.server.model.api.response.AccountChangeRecordResponse;
import cn.fufeii.ds.server.security.CurrentPlatformHelper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户服务
 *
 * @author FuFei
 */
@Service
public class AccountService {
    @Autowired
    private CrudAccountService crudAccountService;
    @Autowired
    private CrudAccountChangeRecordService crudAccountChangeRecordService;
    @Autowired
    private CrudMemberService crudMemberService;


    /**
     * 账户变动
     *
     * @param request -
     */
    @GlobalLock(key = DsServerConstant.CPUS + "#request.memberUsername")
    @Transactional
    public void change(AccountChangeRequest request) {
        // 校验参数
        ChangeTypeEnum changeType = request.getChangeType();
        boolean isNegative = ChangeTypeEnum.DECREASE == changeType || ChangeTypeEnum.FREEZE == changeType;
        Integer changeCount = request.getChangeCount();
        if (ChangeTypeEnum.PROFIT == changeType) {
            throw new BizException();
        }

        // 执行业务
        Member member = crudMemberService.selectByUsernameAndPlatformUsername(request.getMemberUsername(), CurrentPlatformHelper.username());
        if (StateEnum.DISABLE == member.getState()) {
            throw new BizException(ExceptionEnum.MEMBER_DISABLED, request.getMemberUsername());
        }
        Account account = crudAccountService.selectByMemberId(member.getId());
        AccountTypeEnum accountType = request.getAccountType();
        boolean isMoneyAccount = AccountTypeEnum.MONEY == accountType;

        // 如果为消极的（针对可用余额做减法），则判断当前是否足够
        if (isNegative && ((isMoneyAccount && account.getMoneyAvailable() < changeCount) || (!isMoneyAccount && account.getPointsAvailable() < changeCount))) {
            throw new BizException(ExceptionEnum.BIZ_COMMON_ERROR, "可用余额不足");
        }
        Integer moneyChangeCount = 0;
        Integer pointsChangeCount = 0;
        if (isMoneyAccount) {
            moneyChangeCount = isNegative ? changeCount : -changeCount;
        } else {
            pointsChangeCount = isNegative ? changeCount : -changeCount;
        }

        // 记录账户变动日志
        AccountChangeRecord accountChangeRecord = new AccountChangeRecord();
        Integer beforeAvailableCount = isMoneyAccount ? account.getMoneyAvailable() : account.getPointsAvailable();

        // 进行账户的操作
        switch (changeType) {
            case INCREASE:
                account.setMoneyTotalHistory(account.getMoneyTotalHistory() + moneyChangeCount);
                account.setPointsTotalHistory(account.getPointsTotalHistory() + pointsChangeCount);
            case DECREASE:
                account.setMoneyAvailable(account.getMoneyAvailable() + moneyChangeCount);
                account.setMoneyTotal(account.getMoneyTotal() + moneyChangeCount);
                account.setPointsAvailable(account.getPointsAvailable() + pointsChangeCount);
                account.setPointsTotal(account.getPointsTotal() + pointsChangeCount);
                break;
            case FREEZE:
            case UNFREEZE:
                account.setMoneyAvailable(account.getMoneyAvailable() + moneyChangeCount);
                account.setMoneyFrozen(account.getMoneyFrozen() + -moneyChangeCount);
                account.setPointsAvailable(account.getPointsAvailable() + -pointsChangeCount);
                account.setPointsFrozen(account.getPointsFrozen() + pointsChangeCount);
                break;
            default:
                // 理论上不会走到这个分支
                break;
        }
        crudAccountService.updateById(account);

        accountChangeRecord.setMemberId(member.getId());
        accountChangeRecord.setAccountId(account.getId());
        accountChangeRecord.setAccountType(accountType);
        accountChangeRecord.setChangeType(changeType);
        accountChangeRecord.setChangeBizNumber(request.getChangeBizNumber());
        accountChangeRecord.setBeforeAvailableCount(beforeAvailableCount);
        accountChangeRecord.setAfterAvailableCount(isMoneyAccount ? account.getMoneyAvailable() : account.getPointsAvailable());
        accountChangeRecord.setChangeCount(changeCount);
        accountChangeRecord.setMemo(request.getMemo());
        crudAccountChangeRecordService.insert(accountChangeRecord);


    }

    /**
     * @param memberUsername 会员标识
     * @param page           *
     * @param size           *
     */
    public IPage<AccountChangeRecordResponse> accountChangeRecordRecordPage(String memberUsername, Integer page, Integer size) {
        Member member = crudMemberService.selectByUsernameAndPlatformUsername(memberUsername, CurrentPlatformHelper.username());
        return crudAccountChangeRecordService.selectByMemberIdPage(member.getId(), PageUtil.pageAndOrderByIdDesc(page, size))
                .convert(it -> {
                    AccountChangeRecordResponse response = new AccountChangeRecordResponse();
                    response.setId(it.getId());
                    response.setMemberId(it.getMemberId());
                    response.setAccountId(it.getAccountId());
                    response.setMemberUsername(memberUsername);
                    response.setAccountType(it.getAccountType().name());
                    response.setBeforeAvailableCount(it.getBeforeAvailableCount());
                    response.setAfterAvailableCount(it.getAfterAvailableCount());
                    response.setChangeCount(it.getChangeCount());
                    response.setChangeType(it.getChangeType().name());
                    response.setChangeBizNumber(it.getChangeBizNumber());
                    response.setMemo(it.getMemo());
                    response.setCreateDateTime(it.getCreateDateTime());
                    return response;
                });
    }

}

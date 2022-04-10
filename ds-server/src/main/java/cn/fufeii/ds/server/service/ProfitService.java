package cn.fufeii.ds.server.service;

import cn.fufeii.ds.common.annotation.GlobalLock;
import cn.fufeii.ds.common.enumerate.ExceptionEnum;
import cn.fufeii.ds.common.enumerate.biz.ProfitTypeEnum;
import cn.fufeii.ds.common.exception.BizException;
import cn.fufeii.ds.repository.crud.CrudMemberService;
import cn.fufeii.ds.repository.crud.CrudProfitEventService;
import cn.fufeii.ds.repository.crud.CrudProfitRecordService;
import cn.fufeii.ds.repository.entity.Member;
import cn.fufeii.ds.repository.entity.ProfitEvent;
import cn.fufeii.ds.repository.entity.ProfitRecord;
import cn.fufeii.ds.server.config.constant.DsServerConstant;
import cn.fufeii.ds.server.model.api.request.ProfitTradeRequest;
import cn.fufeii.ds.server.model.api.response.ProfitEventInfoResponse;
import cn.fufeii.ds.server.model.api.response.ProfitRecordInfoResponse;
import cn.fufeii.ds.server.model.api.response.ProfitTradeResponse;
import cn.fufeii.ds.server.security.CurrentPlatformHelper;
import cn.fufeii.ds.server.subscribe.event.TradeEvent;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ProfitService
 *
 * @author FuFei
 * @date 2022/4/10
 */
@Service
public class ProfitService {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private CrudMemberService crudMemberService;
    @Autowired
    private CrudProfitRecordService crudProfitRecordService;
    @Autowired
    private CrudProfitEventService crudProfitEventService;

    /**
     * 金钱交易-分润请求
     */
    @GlobalLock(key = DsServerConstant.CURRENT_PLATFORM_USERNAME_SPEL + "#request.tradeNumber")
    public ProfitTradeResponse trade(ProfitTradeRequest request) {
        // 检查是否已经存在了该事件
        LambdaQueryWrapper<ProfitEvent> lambdaQueryWrapper = Wrappers.<ProfitEvent>lambdaQuery()
                .eq(ProfitEvent::getEventNumber, request.getTradeNumber())
                .eq(ProfitEvent::getPlatformUsername, CurrentPlatformHelper.username());
        if (crudProfitEventService.exist(lambdaQueryWrapper)) {
            throw new BizException(ExceptionEnum.PROFIT_EVENT_EXIST, request.getTradeNumber());
        }
        // 组装事件
        Member member = crudMemberService.selectByUsernameAndPlatformUsername(request.getUsername(), CurrentPlatformHelper.username());
        TradeEvent.Source source = new TradeEvent.Source();
        source.setMemberId(member.getId());
        source.setTradeAmount(request.getTradeAmount());
        source.setTradeNumber(request.getTradeNumber());
        // 发布事件
        applicationEventPublisher.publishEvent(new TradeEvent(ProfitTypeEnum.TRADE, source));

        // FIXME 分销成功异步通知
        // 这里是临时代码，任何分销成功后应该异步通知上游系统分销成功了，以便上游系统调用[分销事件查询]接口获取分销事件的详情
        ProfitTradeResponse response = new ProfitTradeResponse();
        ProfitEvent profitEvent = crudProfitEventService.selectOne(lambdaQueryWrapper);
        response.setEventId(profitEvent.getId());
        return response;
    }

    /**
     * 查询分销记录
     *
     * @param memberUsername 会员标识
     * @param page           *
     * @param size           *
     */
    public IPage<ProfitRecordInfoResponse> memberRecord(String memberUsername, Integer page, Integer size) {
        Member member = crudMemberService.selectByUsernameAndPlatformUsername(memberUsername, CurrentPlatformHelper.username());
        LambdaQueryWrapper<ProfitRecord> lambdaQueryWrapper = Wrappers.<ProfitRecord>lambdaQuery().eq(ProfitRecord::getImpactMemberId, member.getId());
        IPage<ProfitRecord> profitRecordIPage = crudProfitRecordService.selectPage(lambdaQueryWrapper, Page.of(page, size));
        return profitRecordIPage.convert(it -> {
            ProfitRecordInfoResponse response = new ProfitRecordInfoResponse();
            response.setId(it.getId());
            response.setProfitEventId(it.getProfitEventId());
            response.setAccountType(it.getAccountType());
            response.setImpactMemberId(it.getImpactMemberId());
            response.setImpactMemberUsername(memberUsername);
            response.setIncomeCount(it.getIncomeCount());
            response.setMemo(it.getMemo());
            response.setCreateDateTime(it.getCreateDateTime());
            return response;
        });
    }


    /**
     * 查询分润事件
     *
     * @param eventId 事件主键
     */
    public ProfitEventInfoResponse event(Long eventId) {
        ProfitEvent profitEvent = crudProfitEventService.selectByIdAndPlatformUsername(eventId, CurrentPlatformHelper.username());
        // 通用组装
        ProfitEventInfoResponse response = new ProfitEventInfoResponse();
        response.setProfitType(profitEvent.getProfitType());
        response.setTriggerMemberId(profitEvent.getTriggerMemberId());
        response.setEventNumber(profitEvent.getEventNumber());
        response.setEventAmount(profitEvent.getEventAmount());
        response.setMemo(profitEvent.getMemo());
        response.setCreateDateTime(profitEvent.getCreateDateTime());
        // 组装分销记录
        List<ProfitRecord> profitRecordList = crudProfitRecordService.selectByProfitEventId(eventId);
        List<ProfitEventInfoResponse.ProfitRecordInfo> profitRecordInfoList = new ArrayList<>(profitRecordList.size());
        for (ProfitRecord profitRecord : profitRecordList) {
            ProfitEventInfoResponse.ProfitRecordInfo profitRecordInfo = new ProfitEventInfoResponse.ProfitRecordInfo();
            profitRecordInfo.setId(profitRecord.getId());
            profitRecordInfo.setProfitEventId(eventId);
            profitRecordInfo.setAccountType(profitRecord.getAccountType());
            profitRecordInfo.setImpactMemberId(profitRecord.getImpactMemberId());
            profitRecordInfo.setIncomeCount(profitRecord.getIncomeCount());
            profitRecordInfo.setMemo(profitRecord.getMemo());
            profitRecordInfo.setCreateDateTime(profitRecord.getCreateDateTime());
            profitRecordInfoList.add(profitRecordInfo);
        }
        response.setProfitRecordInfoList(profitRecordInfoList);
        return response;
    }


}

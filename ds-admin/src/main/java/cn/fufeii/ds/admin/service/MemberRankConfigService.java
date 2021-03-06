package cn.fufeii.ds.admin.service;

import cn.fufeii.ds.admin.config.constant.DsAdminConstant;
import cn.fufeii.ds.admin.model.vo.request.MemberRankConfigQueryRequest;
import cn.fufeii.ds.admin.model.vo.request.MemberRankConfigUpsertRequest;
import cn.fufeii.ds.admin.model.vo.response.MemberRankConfigResponse;
import cn.fufeii.ds.admin.security.CurrentUserHelper;
import cn.fufeii.ds.common.annotation.GlobalLock;
import cn.fufeii.ds.common.enumerate.ExceptionEnum;
import cn.fufeii.ds.common.enumerate.biz.StateEnum;
import cn.fufeii.ds.common.exception.BizException;
import cn.fufeii.ds.common.util.BeanCopierUtil;
import cn.fufeii.ds.repository.crud.CrudMemberRankConfigService;
import cn.fufeii.ds.repository.entity.MemberRankConfig;
import cn.fufeii.ds.repository.entity.SystemUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分润配置 Service
 *
 * @author FuFei
 */
@Slf4j
@Service
public class MemberRankConfigService {

    @Autowired
    private CrudMemberRankConfigService crudMemberRankConfigService;

    /**
     * 分页查询
     */
    public IPage<MemberRankConfigResponse> page(MemberRankConfigQueryRequest pageParam, IPage<MemberRankConfig> pageable) {
        LambdaQueryWrapper<MemberRankConfig> queryWrapper = Wrappers.lambdaQuery(BeanCopierUtil.copy(pageParam, MemberRankConfig.class));
        IPage<MemberRankConfig> selectPage = crudMemberRankConfigService.selectPage(queryWrapper, pageable);
        // 组装response对象返回
        return selectPage.convert(it -> {
            MemberRankConfigResponse response = new MemberRankConfigResponse();
            this.setResponse(it, response);
            return response;
        });
    }

    /**
     * 获取
     */
    public MemberRankConfigResponse get(Long id) {
        MemberRankConfig rankParam = crudMemberRankConfigService.selectById(id);
        MemberRankConfigResponse response = new MemberRankConfigResponse();
        this.setResponse(rankParam, response);
        return response;
    }

    /**
     * 通用响应设置
     */
    public void setResponse(MemberRankConfig memberRankConfig, MemberRankConfigResponse response) {
        response.setId(memberRankConfig.getId());
        response.setPlatformUsername(memberRankConfig.getPlatformUsername());
        response.setPlatformNickname(memberRankConfig.getPlatformNickname());
        response.setMemberRankType(memberRankConfig.getMemberRankType().getMessage());
        response.setBeginPoints(memberRankConfig.getBeginPoints());
        response.setEndPoints(memberRankConfig.getEndPoints());
        response.setState(memberRankConfig.getState().getMessage());
        response.setCreateDateTime(memberRankConfig.getCreateDateTime());
        response.setUpdateDateTime(memberRankConfig.getUpdateDateTime());
    }

    /**
     * 保存
     */
    @GlobalLock(key = DsAdminConstant.CPUS + "':mrc-create'")
    public void create(MemberRankConfigUpsertRequest request) {
        if (request.getMemberRankType() == null) {
            throw BizException.client("memberRankType不能为空");
        }
        this.checkPointsRange(request);
        SystemUser currentUser = CurrentUserHelper.self();
        // 检查是否存在并插入数据
        LambdaQueryWrapper<MemberRankConfig> queryWrapper = Wrappers.<MemberRankConfig>lambdaQuery()
                .eq(MemberRankConfig::getPlatformUsername, currentUser.getPlatformUsername())
                .eq(MemberRankConfig::getMemberRankType, request.getMemberRankType());
        if (crudMemberRankConfigService.exist(queryWrapper)) {
            throw BizException.client("该参数已存在");
        }
        MemberRankConfig rankParam = new MemberRankConfig();
        // 建议使用setter, 字段类型问题能在编译期发现
        BeanCopierUtil.copy(request, rankParam);
        rankParam.setPlatformUsername(currentUser.getPlatformUsername());
        rankParam.setPlatformNickname(currentUser.getPlatformNickname());
        rankParam.setState(StateEnum.ENABLE);
        crudMemberRankConfigService.insert(rankParam);
    }

    /**
     * 更新
     */
    public void modify(MemberRankConfigUpsertRequest request) {
        MemberRankConfig rankParam = crudMemberRankConfigService.selectById(request.getId());
        // 这个字段是不能改变的
        if (request.getMemberRankType() != null) {
            throw BizException.admin("用户段位类型不能修改");
        }
        this.checkPointsRange(request);
        // 建议使用setter, 字段类型问题能在编译期发现
        BeanCopierUtil.copy(request, rankParam);
        crudMemberRankConfigService.updateById(rankParam);
    }

    /**
     * 检查分数范围
     */
    private void checkPointsRange(MemberRankConfigUpsertRequest request) {
        Integer begin = request.getBeginPoints();
        Integer end = request.getEndPoints();
        if (begin > end || begin.equals(end)) {
            throw BizException.client("起始分数必须大于结束分数");
        }
        // 需要检查begin和end没有占用其他配置的分段
        LambdaQueryWrapper<MemberRankConfig> queryWrapper = Wrappers.<MemberRankConfig>lambdaQuery()
                .ne(MemberRankConfig::getMemberRankType, request.getMemberRankType())
                .eq(MemberRankConfig::getPlatformUsername, CurrentUserHelper.platformUsername())
                .and(it -> it.le(MemberRankConfig::getBeginPoints, begin).ge(MemberRankConfig::getEndPoints, begin)
                        .or().le(MemberRankConfig::getBeginPoints, end).ge(MemberRankConfig::getEndPoints, end));
        if (crudMemberRankConfigService.exist(queryWrapper)) {
            throw BizException.admin("此分段已被其他配置占用");
        }
    }

    /**
     * 删除
     */
    public void remove(Long id) {
        MemberRankConfig rankParam = crudMemberRankConfigService.selectById(id);
        crudMemberRankConfigService.deleteById(id);
    }


    /**
     * 修改状态
     */
    public void changeState(Long id, StateEnum stateEnum) {
        MemberRankConfig rankParam = crudMemberRankConfigService.selectById(id);
        if (stateEnum == rankParam.getState()) {
            throw new BizException(ExceptionEnum.UPDATE_STATE_REPEATEDLY);
        }
        rankParam.setState(stateEnum);
        crudMemberRankConfigService.updateById(rankParam);
    }


}
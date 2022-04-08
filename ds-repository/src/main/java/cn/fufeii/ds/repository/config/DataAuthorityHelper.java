package cn.fufeii.ds.repository.config;

import cn.fufeii.ds.common.enumerate.ExceptionEnum;
import cn.fufeii.ds.common.exception.BizException;
import cn.fufeii.ds.repository.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Objects;

/**
 * 数据权限Helper
 * 针对平台标识（类似租户）进行操作，每个平台只能读取自己的数据
 *
 * @author FuFei
 * @date 2022/4/3
 */
public final class DataAuthorityHelper {

    /**
     * 设置[平台标识]字段到LambdaQueryWrapper中
     */
    @SuppressWarnings("unchecked")
    public static <T> void setPlatform(LambdaQueryWrapper<T> queryWrapper, String platformUsername) {
        if (Member.class.equals(queryWrapper.getEntityClass())) {
            ((LambdaQueryWrapper<Member>) queryWrapper).eq(Member::getPlatformUsername, platformUsername);
            return;
        }
        if (Platform.class.equals(queryWrapper.getEntityClass())) {
            ((LambdaQueryWrapper<Platform>) queryWrapper).eq(Platform::getUsername, platformUsername);
            return;
        }
        if (ProfitParam.class.equals(queryWrapper.getEntityClass())) {
            ((LambdaQueryWrapper<ProfitParam>) queryWrapper).eq(ProfitParam::getPlatformUsername, platformUsername);
            return;
        }
        if (RankParam.class.equals(queryWrapper.getEntityClass())) {
            ((LambdaQueryWrapper<RankParam>) queryWrapper).eq(RankParam::getPlatformUsername, platformUsername);
            return;
        }
        if (ProfitEvent.class.equals(queryWrapper.getEntityClass())) {
            ((LambdaQueryWrapper<ProfitEvent>) queryWrapper).eq(ProfitEvent::getPlatformUsername, platformUsername);
            return;
        }
        if (WithdrawApply.class.equals(queryWrapper.getEntityClass())) {
            ((LambdaQueryWrapper<WithdrawApply>) queryWrapper).eq(WithdrawApply::getPlatformUsername, platformUsername);
            return;
        }
        if (SystemUser.class.equals(queryWrapper.getEntityClass())) {
            ((LambdaQueryWrapper<SystemUser>) queryWrapper).eq(SystemUser::getPlatformUsername, platformUsername);
        }
    }


    /**
     * 检查数据上的平台与当前获取此数据的平台是否一致
     */
    public static boolean checkPlatform(String currentPlatformUsername, String dataPlatformUsername) {
        return Objects.equals(currentPlatformUsername, dataPlatformUsername);
    }

    /**
     * 检查数据上的平台信息当前获取此数据的平台是否一致
     */
    public static void checkPlatformThrow(String currentPlatformUsername, String dataPlatformUsername) {
        if (!checkPlatform(currentPlatformUsername, dataPlatformUsername)) {
            throw new BizException(ExceptionEnum.NO_DATA_PERMISSION);
        }
    }

}
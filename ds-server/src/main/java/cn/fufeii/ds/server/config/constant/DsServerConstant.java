package cn.fufeii.ds.server.config.constant;

/**
 * server项目常量
 *
 * @author FuFei
 * @date 2022/4/5
 */
public final class DsServerConstant {

    public static final int DEFAULT_EVENT_AMOUNT = 0;

    /**
     * CURRENT_PLATFORM_USERNAME_SPEL
     */
    public static final String CPUS = "T(cn.fufeii.ds.server.security.CurrentPlatformHelper).username() + ':' + ";

    public static final String NOTIFY_SUCCESS_FLAG = "SUCCESS";

}

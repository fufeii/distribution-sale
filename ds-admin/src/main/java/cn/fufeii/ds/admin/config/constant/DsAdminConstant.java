package cn.fufeii.ds.admin.config.constant;

/**
 * admin项目常量
 *
 * @author FuFei
 * @date 2022/3/27
 */
public final class DsAdminConstant {

    public static final String ROOT_PATH = "/";
    public static final String VIEW_PATH_PREFIX = "/view";
    public static final String API_PATH_PREFIX = "/admin";
    public static final String LOGIN_URL = API_PATH_PREFIX + "/login";
    public static final String LOGOUT_URL = API_PATH_PREFIX + "/logout";
    public static final String LOGIN_PAGE_PLATFORM_LIST_URL = API_PATH_PREFIX + "/platform/optional/list";

    public static final String REDIS_PREFIX = "ds:";
    public static final String REDIS_JWT_PREFIX = REDIS_PREFIX + "jwt:";


    public static final String ADMIN_USERNAME = "admin";

    /**
     * CURRENT_PLATFORM_USERNAME_SPEL
     */
    public static final String CPUS = "T(cn.fufeii.ds.admin.security.CurrentUserHelper).platformUsername() + ':' + ";

}

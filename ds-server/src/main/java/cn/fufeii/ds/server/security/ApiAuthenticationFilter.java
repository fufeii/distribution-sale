package cn.fufeii.ds.server.security;

import cn.fufeii.ds.common.constant.DsConstant;
import cn.fufeii.ds.common.enumerate.ExceptionEnum;
import cn.fufeii.ds.common.enumerate.biz.StateEnum;
import cn.fufeii.ds.common.model.CommonResult;
import cn.fufeii.ds.common.util.ObjectMapperUtil;
import cn.fufeii.ds.common.util.ResponseUtil;
import cn.fufeii.ds.repository.crud.CrudPlatformService;
import cn.fufeii.ds.repository.entity.Platform;
import cn.fufeii.ds.server.config.property.DsServerProperties;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import jodd.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * api认证filter
 * 从header中获取平台信息
 * 按照配置进行解密和验签
 *
 * @author FuFei
 * @date 2022/4/5
 */
@Slf4j
@Component
public class ApiAuthenticationFilter extends OncePerRequestFilter {
    private final AntPathMatcher matcher = new AntPathMatcher();
    @Autowired
    private DsServerProperties dsServerProperties;
    @Autowired
    private CrudPlatformService crudPlatformService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // doc文档不需要过滤
        return Arrays.stream(DsConstant.KNIFE4J_URL).anyMatch(it -> matcher.match(it, request.getServletPath()));
    }

    /**
     * 生成签字符串
     *
     * @param sk               密钥
     * @param waitSignatureStr 待签名串
     * @return *
     */
    public static String generateSignature(String sk, String waitSignatureStr) {
        return SecureUtil.hmacSha256(sk).digestHex(waitSignatureStr);
    }

    /**
     * 生成待签名字符串
     *
     * @param requestMethod 请求方法
     * @param requestPath   请求路径
     * @param requestBody   请求体
     * @return *
     */
    public static String generateWaitSignatureStr(String requestMethod, String requestPath, String requestBody) {
        return requestMethod + "\n" + requestPath + "\n" + requestBody + "\n";
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationStr = request.getHeader(DsConstant.HEADER_AUTHORIZATION);
        Boolean enableApiSignature = dsServerProperties.getEnableApiSignature();
        int loginInErrorCode = ExceptionEnum.LOGIN_IN_ERROR.getCode();
        // 没传有效的签名
        if (CharSequenceUtil.isBlank(authorizationStr)) {
            // 即使关闭验签验证, 至少也需要有身份标识数据存在
            if (log.isDebugEnabled()) {
                log.debug("请求头参数值不存在, {}", DsConstant.HEADER_AUTHORIZATION);
            }
            ResponseUtil.write(response, CommonResult.fail(loginInErrorCode, DsConstant.HEADER_AUTHORIZATION + "请求头为空"));
            return;
        }

        // 准备参数
        authorizationStr = authorizationStr.trim();
        Map<String, String> authMap = Arrays.stream(authorizationStr.split(StrPool.COMMA)).collect(Collectors.toMap(
                itKey -> itKey.split(StringPool.EQUALS)[0]
                , itValue -> itValue.substring(itValue.indexOf(StringPool.EQUALS) + 1)
                , (o, n) -> n));
        // 需要更优雅的方式转换
        AuthorizationParam authorizationParam = ObjectMapperUtil.toObject(ObjectMapperUtil.toJsonString(authMap), AuthorizationParam.class);
        Long pid = authorizationParam.getPid();
        if (pid == null) {
            ResponseUtil.write(response, CommonResult.fail(loginInErrorCode, DsConstant.HEADER_AUTHORIZATION + "请求头中pid为空"));
            return;
        }
        String signature = authorizationParam.getSignature();
        if (enableApiSignature && StrUtil.isBlank(signature)) {
            ResponseUtil.write(response, CommonResult.fail(loginInErrorCode, DsConstant.HEADER_AUTHORIZATION + "请求头中signature为空"));
            return;
        }

        // 获取平台信息
        Optional<Platform> platformOptional = crudPlatformService.selectByIdOptional(pid);
        if (!platformOptional.isPresent()) {
            ResponseUtil.write(response, CommonResult.fail(loginInErrorCode, "平台未创建"));
            return;
        }
        Platform platform = platformOptional.get();
        if (StateEnum.DISABLE == platform.getState()) {
            ResponseUtil.write(response, CommonResult.fail(loginInErrorCode, "平台被禁用"));
            return;
        }

        // 验签操作
        HttpServletRequest requestWrapper = request;
        try {
            // 验签, platform.sk
            if (enableApiSignature) {
                ContentReuseRequestWrapper reuseRequestWrapper = new ContentReuseRequestWrapper(request);
                // 改变引用, 以便进一步处理请求体
                requestWrapper = reuseRequestWrapper;
                String requestBody = StrUtil.utf8Str(reuseRequestWrapper.getContent());
                // 执行验签逻辑
                String waitSignatureStr = generateWaitSignatureStr(requestWrapper.getMethod(), requestWrapper.getServletPath(), requestBody);
                String signatureStr = generateSignature(platform.getSk(), waitSignatureStr);
                boolean checkSignature = StrUtil.equals(authorizationParam.getSignature(), signatureStr);
                if (!checkSignature) {
                    if (log.isDebugEnabled()) {
                        log.debug("验签失败：待签名字符串[{}],签名[{}],原签名[{}]", waitSignatureStr, signatureStr, signature);
                    }
                    ResponseUtil.write(response, CommonResult.fail(loginInErrorCode, "验签错误"));
                    return;
                }
            }
        } catch (RuntimeException e) {
            log.warn("执行错误：", e);
            ResponseUtil.write(response, CommonResult.fail(loginInErrorCode, "未知错误"));
            return;
        }

        // 验证通过
        PlatformContextHolder.set(platform);
        try {
            filterChain.doFilter(requestWrapper, response);
        } finally {
            PlatformContextHolder.remove();
        }

    }


}

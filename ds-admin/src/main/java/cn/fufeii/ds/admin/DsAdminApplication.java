package cn.fufeii.ds.admin;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * 启动类
 *
 * @author FuFei
 * @date 2021/8/22
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "cn.fufeii.ds")
public class DsAdminApplication {

    public static void main(String[] args) {
        ConfigurableEnvironment environment = SpringApplication.run(DsAdminApplication.class).getEnvironment();
        logApplicationStartup(environment);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
        String serverPort = env.getProperty("server.port");
        String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path")).filter(StrUtil::isNotBlank).orElse("/");
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        String[] profile = env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles();
        log.info(
                "\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Profile(s): {}\n\t" +
                        "Local: \t\t{}://localhost:{}{}\n\t" +
                        "External: \t{}://{}:{}{}\n" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"), profile, protocol, serverPort, contextPath, protocol, hostAddress, serverPort, contextPath
        );
    }

}
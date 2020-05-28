package com.middol.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统入口
 *
 * @author <a href="mailto:guzhongtao@middol.com">guzhongtao</a>
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = {"com.middol.dubbo.impl.provider", "com.middol.dubbo.consumer"})
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        String banner = "       __            __        __                 \n" +
                "      /  |          /  |      /  |                \n" +
                "  ____$$ | __    __ $$ |____  $$ |____    ______  \n" +
                " /    $$ |/  |  /  |$$      \\ $$      \\  /      \\ \n" +
                "/$$$$$$$ |$$ |  $$ |$$$$$$$  |$$$$$$$  |/$$$$$$  |\n" +
                "$$ |  $$ |$$ |  $$ |$$ |  $$ |$$ |  $$ |$$ |  $$ |\n" +
                "$$ \\__$$ |$$ \\__$$ |$$ |__$$ |$$ |__$$ |$$ \\__$$ |\n" +
                "$$    $$ |$$    $$/ $$    $$/ $$    $$/ $$    $$/ \n" +
                " $$$$$$$/  $$$$$$/  $$$$$$$/  $$$$$$$/   $$$$$$/  \n";
        SpringApplication.run(Application.class, args);
        logger.info(banner);
    }
}


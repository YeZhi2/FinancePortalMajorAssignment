package com.finance.portal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 金融理财产品门户 - 应用入口。
 */
@SpringBootApplication
@MapperScan("com.finance.portal.mapper")
public class FinancePortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancePortalApplication.class, args);
    }
}

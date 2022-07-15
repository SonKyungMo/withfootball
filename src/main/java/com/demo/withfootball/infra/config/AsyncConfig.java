package com.demo.withfootball.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        log.info("processors count {}", processors);
        executor.setCorePoolSize(processors);       //cpu, task 를 처리할 수 있는 core 갯수
        executor.setMaxPoolSize(processors * 2);    //cpu, task 대기줄이 있을 시 만드는 core 갯수의 최대
        executor.setQueueCapacity(50);              //memory, task 대기줄, task 가 최대 갯수를 넘어가면 reject
        executor.setKeepAliveSeconds(60);           //maxPoolSize 에서 만든 core 갯수의 수명
        executor.setThreadNamePrefix("AsyncExecutor");
        executor.initialize();
        return executor;
    }

}

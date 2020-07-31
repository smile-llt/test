package com.learn.lilintong.apitoken.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 *
 * springBoot定时任务执行的时候默认使用单线程执行，
 * 故在此配置一个线程池时定时任务可以并发执行
 * @author lilintong
 * @create 2020/4/7
 */
@Configuration
public class TaskSchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);//暂定5个线程
        taskScheduler.setThreadNamePrefix("taskExecutor-");
        return taskScheduler;
    }

}

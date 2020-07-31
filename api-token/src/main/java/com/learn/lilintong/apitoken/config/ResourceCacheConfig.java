package com.learn.lilintong.apitoken.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.learn.lilintong.apitoken.common.CacheNameEnum;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lilintong
 * @create 2020/3/31
 */
@Configuration
public class ResourceCacheConfig {


    @Bean
    public CacheManager cacheManagerCaffeine() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder()
                .initialCapacity(5) //初始容量值
                .maximumSize(10)//最大缓存数量，
                .expireAfterWrite(3600, TimeUnit.SECONDS); //暂定过期时间写入后一个小时，定时任务更新缓存未过期数据
        cacheManager.setCaffeine(caffeine);
        cacheManager.setCacheNames(getNames());//缓存名称列表
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }

    public List<String> getNames(){
        List<String> enumNames = Stream.of(CacheNameEnum.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return enumNames;
    }

}

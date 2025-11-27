package top.hcode.hoj.oauth;

import me.zhyd.oauth.cache.AuthStateCache;
import org.springframework.stereotype.Component;
import top.hcode.hoj.utils.RedisUtils;

import javax.annotation.Resource;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: OAuth State Redis 缓存实现
 */
@Component
public class AuthStateRedisCache implements AuthStateCache {

    private static final String CACHE_PREFIX = "oauth:state:";
    private static final long TIMEOUT = 300; // 5分钟

    @Resource
    private RedisUtils redisUtils;

    @Override
    public void cache(String key, String value) {
        redisUtils.set(CACHE_PREFIX + key, value, TIMEOUT);
    }

    @Override
    public void cache(String key, String value, long timeout) {
        redisUtils.set(CACHE_PREFIX + key, value, timeout / 1000);
    }

    @Override
    public String get(String key) {
        Object value = redisUtils.get(CACHE_PREFIX + key);
        return value == null ? null : String.valueOf(value);
    }

    @Override
    public boolean containsKey(String key) {
        return redisUtils.hasKey(CACHE_PREFIX + key);
    }
}

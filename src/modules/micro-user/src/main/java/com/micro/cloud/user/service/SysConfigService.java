package com.micro.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.cloud.common.redis.utils.CacheUtils;
import com.micro.cloud.user.domain.SysConfig;
import com.micro.cloud.user.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 参数配置服务
 */
@Service
@RequiredArgsConstructor
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig> {

    private final CacheUtils cacheUtils;

    private static final String CONFIG_CACHE_KEY = "sys:config:";
    private static final long CONFIG_TTL = 3600;

    /** 参数列表 */
    public List<SysConfig> selectConfigList(SysConfig config) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(config.getConfigName() != null, SysConfig::getConfigName, config.getConfigName())
                .like(config.getConfigKey() != null, SysConfig::getConfigKey, config.getConfigKey())
                .eq(config.getConfigType() != null, SysConfig::getConfigType, config.getConfigType());
        return list(wrapper);
    }

    /** 根据键名查询参数值（带缓存） */
    public String selectConfigByKey(String configKey) {
        return cacheUtils.get(CONFIG_CACHE_KEY + configKey, CONFIG_TTL, () -> {
            SysConfig config = getOne(new LambdaQueryWrapper<SysConfig>()
                    .eq(SysConfig::getConfigKey, configKey));
            return config == null ? null : config.getConfigValue();
        });
    }

    /** 新增参数并清除缓存 */
    public void insertConfig(SysConfig config) {
        save(config);
        cacheUtils.remove(CONFIG_CACHE_KEY + config.getConfigKey());
    }

    /** 修改参数并清除缓存 */
    public void updateConfig(SysConfig config) {
        updateById(config);
        cacheUtils.remove(CONFIG_CACHE_KEY + config.getConfigKey());
    }

    /** 删除参数并清除缓存 */
    public void deleteConfigByIds(List<Long> configIds) {
        for (Long configId : configIds) {
            SysConfig config = getById(configId);
            removeById(configId);
            if (config != null) {
                cacheUtils.remove(CONFIG_CACHE_KEY + config.getConfigKey());
            }
        }
    }
}

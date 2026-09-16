package com.micro.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.cloud.common.redis.utils.CacheUtils;
import com.micro.cloud.user.domain.SysDictData;
import com.micro.cloud.user.domain.SysDictType;
import com.micro.cloud.user.mapper.SysDictDataMapper;
import com.micro.cloud.user.mapper.SysDictTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典服务（类型 + 数据）
 */
@Service
@RequiredArgsConstructor
public class SysDictService extends ServiceImpl<SysDictTypeMapper, SysDictType> {

    private final SysDictDataMapper dictDataMapper;
    private final CacheUtils cacheUtils;

    private static final String DICT_CACHE_KEY = "dict:data:";
    private static final long DICT_TTL = 3600;

    /** 字典类型列表 */
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(dictType.getDictName() != null, SysDictType::getDictName, dictType.getDictName())
                .like(dictType.getDictType() != null, SysDictType::getDictType, dictType.getDictType())
                .eq(dictType.getStatus() != null, SysDictType::getStatus, dictType.getStatus());
        return list(wrapper);
    }

    /** 根据字典类型查询字典数据（带缓存，支撑前端动态渲染） */
    public List<SysDictData> selectDictDataByType(String dictType) {
        return cacheUtils.get(DICT_CACHE_KEY + dictType, DICT_TTL, () ->
                dictDataMapper.selectList(new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .eq(SysDictData::getStatus, "0")
                        .orderByAsc(SysDictData::getDictSort)));
    }

    /** 新增字典数据并清除缓存 */
    public void insertDictData(SysDictData dictData) {
        dictDataMapper.insert(dictData);
        cacheUtils.remove(DICT_CACHE_KEY + dictData.getDictType());
    }

    /** 修改字典数据并清除缓存 */
    public void updateDictData(SysDictData dictData) {
        dictDataMapper.updateById(dictData);
        cacheUtils.remove(DICT_CACHE_KEY + dictData.getDictType());
    }

    /** 删除字典数据并清除缓存 */
    public void deleteDictData(Long dictCode) {
        SysDictData data = dictDataMapper.selectById(dictCode);
        dictDataMapper.deleteById(dictCode);
        if (data != null) {
            cacheUtils.remove(DICT_CACHE_KEY + data.getDictType());
        }
    }
}

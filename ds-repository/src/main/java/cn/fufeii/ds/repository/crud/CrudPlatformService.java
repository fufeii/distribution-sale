package cn.fufeii.ds.repository.crud;

import cn.fufeii.ds.repository.dao.PlatformDao;
import cn.fufeii.ds.repository.entity.Platform;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 平台
 * CRUD Platform Service
 *
 * @author FuFei
 */
@Service
public class CrudPlatformService {

    @Autowired
    private PlatformDao platformDao;

    /**
     * 列表查询
     */
    public List<Platform> selectList(Wrapper<Platform> queryWrapper) {
        return platformDao.selectList(queryWrapper);
    }

    /**
     * 分页查询
     */
    public IPage<Platform> selectPage(Wrapper<Platform> queryWrapper, IPage<Platform> pageable) {
        return platformDao.selectPage(pageable, queryWrapper);
    }

    /**
     * 通过ID获取一个可能存在的实体
     */
    public Optional<Platform> selectByIdOpt(Long id) {
        return Optional.ofNullable(platformDao.selectById(id));
    }

    /**
     * 通过ID获取一个存在的实体
     */
    public Platform selectById(Long id) {
        return this.selectByIdOpt(id).orElseThrow(RuntimeException::new);
    }

    /**
     * 通过条件获取一个可能存在的实体
     */
    public Optional<Platform> selectOneOpt(Wrapper<Platform> queryWrapper) {
        return Optional.ofNullable(platformDao.selectOne(queryWrapper));
    }

    /**
     * 通过条件获取一个存在的实体
     */
    public Platform selectOne(Wrapper<Platform> queryWrapper) {
        return this.selectOneOpt(queryWrapper).orElseThrow(RuntimeException::new);
    }

    /**
     * 统计个数
     */
    public long count(Wrapper<Platform> queryWrapper) {
        return platformDao.selectCount(queryWrapper);
    }

    /**
     * 是否存在
     */
    public boolean exist(Wrapper<Platform> queryWrapper) {
        return platformDao.selectCount(queryWrapper) > 0;
    }

    /**
     * 插入或者更新实体
     */
    public Platform insertOrUpdate(Platform entityParam) {
        if (entityParam.getId() == null) {
            platformDao.insert(entityParam);
        } else {
            platformDao.updateById(entityParam);
        }
        return entityParam;
    }

    /**
     * 删除一个实体
     */
    public void deleteById(Long id) {
        platformDao.deleteById(id);
    }


    // --------------------------------------------------------------------------------- //
    // ---------------------------- 下面基础CRUD的扩展 ----------------------------------- //
    // --------------------------------------------------------------------------------- //

    public Platform selectByUsername(String username) {
        return this.selectOne(Wrappers.<Platform>lambdaQuery().eq(Platform::getUsername, username));
    }

}
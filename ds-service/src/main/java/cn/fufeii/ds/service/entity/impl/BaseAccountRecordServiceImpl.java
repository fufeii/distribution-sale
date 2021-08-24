package cn.fufeii.ds.service.entity.impl;

import cn.fufeii.ds.repository.dao.AccountRecordDao;
import cn.fufeii.ds.repository.entity.AccountRecord;
import cn.fufeii.ds.service.entity.BaseAccountRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 账户记录 ServiceImpl
 *
 * @author FuFei
 */
@Service
public class BaseAccountRecordServiceImpl extends ServiceImpl<AccountRecordDao, AccountRecord> implements BaseAccountRecordService {

}
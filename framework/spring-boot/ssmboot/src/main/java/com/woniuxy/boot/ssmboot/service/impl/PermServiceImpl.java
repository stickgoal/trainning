package com.woniuxy.boot.ssmboot.service.impl;

import com.woniuxy.boot.ssmboot.entity.Perm;
import com.woniuxy.boot.ssmboot.dao.PermMapper;
import com.woniuxy.boot.ssmboot.service.IPermService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Lucas
 * @since 2019-11-14
 */
@Service
public class PermServiceImpl extends ServiceImpl<PermMapper, Perm> implements IPermService {

}

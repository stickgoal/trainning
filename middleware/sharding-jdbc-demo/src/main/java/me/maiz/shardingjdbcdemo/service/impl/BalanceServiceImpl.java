package me.maiz.shardingjdbcdemo.service.impl;

import me.maiz.shardingjdbcdemo.model.Balance;
import me.maiz.shardingjdbcdemo.mapper.BalanceMapper;
import me.maiz.shardingjdbcdemo.service.BalanceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Lucas
 * @since 2025-11-18
 */
@Service
public class BalanceServiceImpl extends ServiceImpl<BalanceMapper, Balance> implements BalanceService {

}

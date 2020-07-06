package me.maiz.project.highconcurrency.service;

import me.maiz.project.highconcurrency.model.SecKillOrder;
import me.maiz.project.highconcurrency.model.Seckill;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lucas
 * @since 2020-03-03
 */
public interface ISeckillService extends IService<Seckill> {

    Seckill getSecKill(int id);


    void process(SecKillOrder secKillOrder);
}

package me.maiz.project.highconcurrency.service;

import me.maiz.project.highconcurrency.model.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品 服务类
 * </p>
 *
 * @author lucas
 * @since 2020-03-03
 */
public interface IProductService extends IService<Product> {

    public List<Product> query(int seckillId);


}

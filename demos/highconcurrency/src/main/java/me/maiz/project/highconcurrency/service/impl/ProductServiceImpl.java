package me.maiz.project.highconcurrency.service.impl;

import me.maiz.project.highconcurrency.model.Product;
import me.maiz.project.highconcurrency.dao.ProductMapper;
import me.maiz.project.highconcurrency.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品 服务实现类
 * </p>
 *
 * @author lucas
 * @since 2020-03-03
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {


    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> query(int seckillId) {
        return productMapper.query(seckillId);
    }
}

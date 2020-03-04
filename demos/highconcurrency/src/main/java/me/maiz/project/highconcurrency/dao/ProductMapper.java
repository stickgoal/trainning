package me.maiz.project.highconcurrency.dao;

import me.maiz.project.highconcurrency.model.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 商品 Mapper 接口
 * </p>
 *
 * @author lucas
 * @since 2020-03-03
 */
public interface ProductMapper extends BaseMapper<Product> {


    @Select("select * from sk_product where seckill_id=#{seckillId}")
    public List<Product> query(int seckillId);


    @Update("update sk_product set stock=#{stock} where product_id=#{productId}")
    public void updateStock(Product product);

}

package me.maiz.demo.moderntech.cache;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.maiz.demo.moderntech.cache.model.Category;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository("categoryRepository")
@CacheConfig(cacheNames="categories")
public class CategoryRepositoryImpl implements CategoryRepository {

    @Cacheable
    @Override
    public List<Category> findAllCategory(int userId) {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Category> categories = Lists.newArrayList();
        categories.add(new Category(1,"聚餐",1,true));
        categories.add(new Category(1,"交通",1,false));
        categories.add(new Category(1,"房租",1,false));
        return categories;
    }

    /**
     * @CachePut会将返回值而不是参数放到缓存里;返回值必须和缓存的类型相同,这里必须是List
     * @param category
     * @return
     */
    @CachePut(key = "#category.userId")
    @Override
    public List<Category> addCategoryAndFind(Category category) {
        //实际上没有存储,只发生在缓存中
        return ImmutableList.of(category);
    }

    @CacheEvict(key = "#userId")
    @Override
    public void removeCategory(int categoryId,int userId) {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

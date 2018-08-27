package me.maiz.demo.moderntech.cache;

import lombok.extern.slf4j.Slf4j;
import me.maiz.demo.moderntech.cache.CategoryRepository;
import me.maiz.demo.moderntech.cache.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CacheTestController {


    @Autowired
    private CategoryRepository categoryRepository;

    @RequestMapping("cacheTest")
    public String cacheTest(){
        testCache(1);
        log.info("再次查询");
        testCache(1);

        log.info("现在remove掉1的cache");
        categoryRepository.removeCategory(1,1);
        log.info("再次测试查询");
        testCache(1);

        categoryRepository.addCategoryAndFind(new Category(2,"xxx",1,false));
        categoryRepository.addCategoryAndFind(new Category(3,"yyy",1,false));
        categoryRepository.addCategoryAndFind(new Category(4,"zzz",1,false));

        log.info("==>{}",categoryRepository.findAllCategory(1));

        return "success";
    }

    private void testCache(int userId) {
        long begin = System.currentTimeMillis();
        log.info("["+userId+"] 第1次调用开始");
        categoryRepository.findAllCategory(userId);
        long first = System.currentTimeMillis();
        log.info("["+userId+"] 第1次调用完毕");
        categoryRepository.findAllCategory(userId);
        long second = System.currentTimeMillis();
        log.info("["+userId+"] 第2次调用完毕");
        categoryRepository.findAllCategory(userId);
        long third = System.currentTimeMillis();
        log.info("["+userId+"] 第3次调用完毕");
        log.info("["+userId+"] 第一次调用耗时:"+(first-begin)+" 2:"+(second-first)+" 3:"+(third-second));



    }


}

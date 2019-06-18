package me.maiz.demo.moderntech.cache;


import me.maiz.demo.moderntech.cache.model.Category;

import java.util.List;

public interface CategoryRepository {

    List<Category> findAllCategory(int userId);

    List<Category> addCategoryAndFind(Category category);

    void removeCategory(int categoryId,int userId);

}

package me.maiz.app.dailycost.service.impl;

import com.google.common.collect.Maps;
import me.maiz.app.dailycost.dal.CategoryDAO;
import me.maiz.app.dailycost.dal.model.Category;
import me.maiz.app.dailycost.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDAO categoryDAO;


    @Override
    public Map<String, List<Category>> prepareCategories(int userId) {
        Map<String,List<Category>> categories= Maps.newHashMap();
        categories.put("IN",categoryDAO.findByDirection("IN",userId));
        categories.put("OUT",categoryDAO.findByDirection("OUT",userId));

        return categories;
    }
}

package me.maiz.app.dailycost.service;

import me.maiz.app.dailycost.dal.model.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    Map<String,List<Category>> prepareCategories(int userId);


}

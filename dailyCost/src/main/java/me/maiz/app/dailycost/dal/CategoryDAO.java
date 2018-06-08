package me.maiz.app.dailycost.dal;

import me.maiz.app.dailycost.dal.model.Category;

import java.util.List;

public interface CategoryDAO {

    List<Category> findByDirection(String direction,int userId);


}

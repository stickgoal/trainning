package me.maiz.app.dailycost.dal.impl;

import me.maiz.app.dailycost.dal.CategoryDAO;
import me.maiz.app.dailycost.dal.model.Category;
import me.maiz.app.dailycost.enums.DailyCostResultCode;
import me.maiz.app.dailycost.exception.AppException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("categoryDAO")
public class CategoryDAOImpl extends BaseDAO<Category> implements CategoryDAO {
    @Override
    public List<Category> findByDirection(String direction,int userId) {

        try {
            List<Category> categories = getSession()
                    .createQuery("From Category where direction=:direction and userId=:userId", Category.class)
                    .setParameter("direction", direction)
                    .setParameter("userId", userId)
                    .getResultList();
            return categories;
        } catch (DataAccessException e) {
            throw new AppException(DailyCostResultCode.DATA_ACCESS_FAIL);
        }
    }
}

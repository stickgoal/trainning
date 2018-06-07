package me.maiz.app.dailycost.dal.model;

import javax.persistence.*;

@Entity
@Table(name="dc_category",uniqueConstraints = @UniqueConstraint(name="uk_category_name",columnNames = "name"))
public class Category {

    /*
    *分类ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId;

    /**
     * 所属用户
     */
    private int userId;

    /**
     * 方向
     */
    private String direction;

    /**
     * 分类名
     */
    private String name;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", userId=" + userId +
                ", direction='" + direction + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

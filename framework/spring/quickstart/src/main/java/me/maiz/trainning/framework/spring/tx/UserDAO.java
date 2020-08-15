package me.maiz.trainning.framework.spring.tx;

public interface UserDAO {
    public void insert(String username);
    public void update(int userId,String username);
}

package me.maiz.trainning.project.flashsale.service;


import me.maiz.trainning.project.flashsale.entity.User;

public interface UserService {

    User findByUsername(int userId);

}

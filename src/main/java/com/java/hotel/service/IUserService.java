package com.java.hotel.service;

import com.java.hotel.service.model.domain.User;

public interface IUserService {

    User save(User user);

    User create(User user);

    User getByUsername(String username);

    User getCurrentUser();

    void getAdmin();
}

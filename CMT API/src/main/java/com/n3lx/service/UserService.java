package com.n3lx.service;

import com.n3lx.dao.GenericDao;
import com.n3lx.exception.InputValidationException;
import com.n3lx.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements GenericService<User> {

    @Autowired
    GenericDao<User> userDao;

    @Override
    public User get(int id) throws Exception {
        //Input validation
        if (id <= 0) {
            throw new InputValidationException("\"id\" parameter has to be greater than 0.");
        }

        //Process input
        return userDao.get(id);
    }

    @Override
    public List<User> getAll() throws Exception {
        //Process input
        return userDao.getAll();
    }

    @Override
    public void save(User user) throws Exception {
        //Input validation
        if (user == null) {
            throw new InputValidationException("The user is null or it was not provided properly.");
        }
        if (user.getUserName() == null) {
            throw new InputValidationException("One of parameters is null.");
        }
        if (user.getUserName().length() > 32) {
            throw new InputValidationException("\"user_name\" parameter cannot be longer than 32 characters.");
        }

        //Process input
        userDao.save(user);
    }

    @Override
    public void update(User user) throws Exception {
        //Input validation
        if (user == null) {
            throw new InputValidationException("The user is null or it was not provided properly.");
        }
        if (user.getUserName() == null) {
            throw new InputValidationException("One of parameters is null.");
        }
        if (user.getUserName().length() > 32) {
            throw new InputValidationException("\"user_name\" parameter cannot be longer than 32 characters.");
        }

        //Process input
        userDao.update(user);
    }

    @Override
    public void delete(User user) throws Exception {
        //Input validation
        if (user == null) {
            throw new InputValidationException("The user is null or it was not provided properly.");
        }
        if (user.getUserName() == null) {
            throw new InputValidationException("One of parameters is null.");
        }

        //Process input
        userDao.delete(user);
    }

}

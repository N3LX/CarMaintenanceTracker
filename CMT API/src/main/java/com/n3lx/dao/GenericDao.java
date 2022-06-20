package com.n3lx.dao;

import com.n3lx.jdbc.exception.CustomSQLException;

import java.util.List;

/**
 * Basic CRUD interface
 *
 * @param <T> POJO representing an entry in database
 */
public interface GenericDao<T> {

    T get(int id) throws CustomSQLException;

    List<T> getAll() throws CustomSQLException;

    void save(T t) throws CustomSQLException;

    void update(T t) throws CustomSQLException;

    void delete(T t) throws CustomSQLException;

}

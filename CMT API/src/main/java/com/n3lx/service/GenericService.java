package com.n3lx.service;

import java.util.List;

/**
 * Basic CRUD service interface
 *
 * @param <T> POJO representing an entry in database
 */
public interface GenericService<T> {

    T get(int id) throws Exception;

    List<T> getAll() throws Exception;

    void save(T t) throws Exception;

    void update(T t) throws Exception;

    void delete(T t) throws Exception;

}

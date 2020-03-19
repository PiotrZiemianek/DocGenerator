package pl.piotrziemianek.dao;

import java.util.List;
import java.util.Optional;

public interface CrudAccessible<T> {
    List<T> findAll();

    Optional<T> findById(int id);

    int create(T entity);

    int update(T entity);

    int createOrUpdate(T entity);

    boolean delete(int id);

    boolean deleteAll();
}

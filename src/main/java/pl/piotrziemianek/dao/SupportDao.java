package pl.piotrziemianek.dao;

import pl.piotrziemianek.domain.Support;

import java.util.List;
import java.util.Optional;

public class SupportDao implements CrudAccessible<Support> {


    @Override
    public List<Support> findAll() {
        return null;
    }

    @Override
    public Optional<Support> findById(int id) {
        return Optional.empty();
    }

    @Override
    public int create(Support entity) {
        return 0;
    }

    @Override
    public int update(Support entity) {
        return 0;
    }

    @Override
    public int createOrUpdate(Support entity) {
        return 0;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public boolean deleteAll() {
        return false;
    }
}

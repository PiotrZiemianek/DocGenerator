package pl.piotrziemianek.dao;

import pl.piotrziemianek.domain.Therapy;

import java.util.List;
import java.util.Optional;

public class TherapyDao implements CrudAccessible<Therapy> {

    @Override
    public List<Therapy> findAll() {
        return null;
    }

    @Override
    public Optional<Therapy> findById(int id) {
        return Optional.empty();
    }

    @Override
    public int create(Therapy entity) {
        return 0;
    }

    @Override
    public int update(Therapy entity) {
        return 0;
    }

    @Override
    public int createOrUpdate(Therapy entity) {
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

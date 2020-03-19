package pl.piotrziemianek.dao;

import pl.piotrziemianek.domain.Subject;

import java.util.List;
import java.util.Optional;


public class SubjectDao implements CrudAccessible<Subject> {


    @Override
    public List<Subject> findAll() {
        return null;
    }

    @Override
    public Optional<Subject> findById(int id) {
        return Optional.empty();
    }

    @Override
    public int create(Subject entity) {
        return 0;
    }

    @Override
    public int update(Subject entity) {
        return 0;
    }

    @Override
    public int createOrUpdate(Subject entity) {
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

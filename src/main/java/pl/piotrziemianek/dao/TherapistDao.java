package pl.piotrziemianek.dao;

import pl.piotrziemianek.domain.Therapist;

import java.util.List;
import java.util.Optional;

public class TherapistDao implements CrudAccessible<Therapist> {

    @Override
    public List<Therapist> findAll() {
        return null;
    }

    @Override
    public Optional<Therapist> findById(int id) {
        return Optional.empty();
    }

    @Override
    public int create(Therapist entity) {
        return 0;
    }

    @Override
    public int update(Therapist entity) {
        return 0;
    }

    @Override
    public int createOrUpdate(Therapist entity) {
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

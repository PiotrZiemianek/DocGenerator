package pl.piotrziemianek.dao;

import pl.piotrziemianek.domain.TherapiesCard;

import java.util.List;
import java.util.Optional;

public class TherapiesCardDao implements CrudAccessible<TherapiesCard> {

    @Override
    public List<TherapiesCard> findAll() {
        return null;
    }

    @Override
    public Optional<TherapiesCard> findById(int id) {
        return Optional.empty();
    }

    @Override
    public int create(TherapiesCard entity) {
        return 0;
    }

    @Override
    public int update(TherapiesCard entity) {
        return 0;
    }

    @Override
    public int createOrUpdate(TherapiesCard entity) {
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

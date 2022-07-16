package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.MPAStorage;

import java.util.Collection;

@Slf4j
@Service
public class MPAService {
    private final MPAStorage mpa;

    @Autowired
    public MPAService(MPAStorage mpa) {
        this.mpa = mpa;
    }

    public Collection<MPA> getAllMPA() {
        return mpa.findAll();
    }

    public MPA getMPA(int id) {
        MPA m = mpa.getMPA(id);
        if (m == null) {
            throw new NotFoundException(String.format(
                    "MPA (id = %s) не найден",
                    id));
        }
        log.debug("Получение MPA с id = {}", id);
        return m;
    }
}

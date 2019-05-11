package co.ga.freshpotatoes.domain.repository;

import co.ga.freshpotatoes.domain.entity.Film;
import co.ga.freshpotatoes.domain.entity.Genre;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FilmRepository extends CrudRepository<Film, Long> {
  List<Film> findByGenre(Genre genre);
  Film findById(Long filmId);
}

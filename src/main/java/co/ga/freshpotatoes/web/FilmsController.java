package co.ga.freshpotatoes.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import co.ga.freshpotatoes.domain.entity.Film;
import co.ga.freshpotatoes.domain.entity.Genre;
import co.ga.freshpotatoes.domain.repository.FilmRepository;
import co.ga.freshpotatoes.domain.repository.GenreRepository;

@RestController
public class FilmsController {
  @Autowired GenreRepository genreRepository;
  @Autowired FilmRepository filmRepository;

  private static final String template = "id=%s, offset=%s, limit=%s\n";

  @RequestMapping(value="/films/{film_id}/recommendations", method=RequestMethod.GET)
  public Set<Film> recommendations(@PathVariable Long film_id,
                                   @RequestParam (required = false) Integer offset,
                                   @RequestParam (required = false) Integer limit) {

    Film film = this.filmRepository.findById(film_id);
    Genre genre = this.genreRepository.findOne(film.getGenre().getId());

    Set<Film> films = genre.getFilms();
    Set<Film> filteredFilmsByDate = null;

    LocalDate originalDate = LocalDate.parse(film.getReleaseDate());
    LocalDate originalDatePlusFifteen = originalDate.plusYears(15);
    LocalDate originalDateMinusFifteen = originalDate.plusYears(-15);

    for (Film f : films) {
      LocalDate newDate = LocalDate.parse(f.getReleaseDate());

      if ((newDate.compareTo(originalDatePlusFifteen) > 0)
              || (newDate.compareTo(originalDateMinusFifteen) > 0)
              || newDate.compareTo(originalDate) == 0) {

        if (filteredFilmsByDate == null) {
          filteredFilmsByDate = new HashSet<>();
        }

        System.out.println("Initial---" + originalDate);
        System.out.println("Before----" + originalDateMinusFifteen);
        System.out.println("After-----" + originalDatePlusFifteen);
        System.out.println("New-------" + newDate);
      }
    }

    return new java.util.LinkedHashSet<Film>();
  }
}

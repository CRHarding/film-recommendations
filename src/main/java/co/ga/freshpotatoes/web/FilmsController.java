package co.ga.freshpotatoes.web;

import org.json.JSONException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONArray;


import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

import co.ga.freshpotatoes.domain.entity.Film;
import co.ga.freshpotatoes.domain.entity.Genre;
import co.ga.freshpotatoes.domain.repository.FilmRepository;
import co.ga.freshpotatoes.domain.repository.GenreRepository;
import org.springframework.web.client.RestTemplate;

@RestController
public class FilmsController {
  @Autowired GenreRepository genreRepository;
  @Autowired FilmRepository filmRepository;

  private static final String template = "id=%s, offset=%s, limit=%s\n";

  @RequestMapping(value="/films/{film_id}/recommendations", method=RequestMethod.GET)
  public Set<Film> recommendations(@PathVariable Long film_id,
                                   @RequestParam (required = false) Integer offset,
                                   @RequestParam (required = false) Integer limit) throws JSONException {

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

        final String uri = "http://credentials-api.generalassemb.ly/4576f55f-c427-4cfc-a11c-5bfe914ca6c1?films=" + f.getId();

        RestTemplate restTemplate = new RestTemplate();
        String resp = restTemplate.getForObject(uri, String.class);

        JSONArray reviewObject = new JSONArray(resp);


        filteredFilmsByDate.add(f);
      }
    }

    List<Film> sortedFilms = new ArrayList<>(filteredFilmsByDate);
    Collections.sort(sortedFilms);
    return new java.util.LinkedHashSet<Film>(sortedFilms);
  }
}

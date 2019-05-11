package co.ga.freshpotatoes.web;

import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.json.JSONArray;
import org.json.JSONObject;

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
  public ResponseEntity<?> recommendations(@PathVariable Long film_id,
                                   @RequestParam (required = false) Integer offset,
                                   @RequestParam (required = false) Integer limit) throws JSONException {

    if (limit == null) limit = 10;

    Film film = this.filmRepository.findById(film_id);

    if (film != null) {

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

          for (int i = 0; i < reviewObject.length(); i++) {
            JSONObject jsonObj = reviewObject.getJSONObject(i);
            Object k = jsonObj.keys().next();
            JSONArray reviews = jsonObj.getJSONArray("reviews");

            if (reviews.length() > 5) {
              double rating = 0.0;
              int numRatings = 0;

              for (int j = 0; j < reviews.length(); j++) {
                JSONObject ratingObject = reviews.getJSONObject(j);
                rating = rating + (int) ratingObject.get("rating");
                numRatings = numRatings + 1;
              }

              if (rating / numRatings > 4.0) {
                filteredFilmsByDate.add(f);
                System.out.println(f);
                System.out.println(rating / numRatings);
              }
            }
          }
        }
      }
      List<Film> sortedFilms = new ArrayList<>(filteredFilmsByDate);
      Collections.sort(sortedFilms);
      return ResponseEntity.ok(sortedFilms);
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
  }
}

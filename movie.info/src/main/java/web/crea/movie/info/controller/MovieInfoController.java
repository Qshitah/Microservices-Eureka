package web.crea.movie.info.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import web.crea.movie.info.model.Movie;
import web.crea.movie.info.model.MovieSummary;

@RestController
@RequestMapping("/movies")
public class MovieInfoController {

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private WebClient.Builder webClient;

    @GetMapping("/{movieId}")
    public Mono<MovieSummary> getMovie(@PathVariable Long movieId){
        return webClient.build()
                .get()
                .uri("https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" +  apiKey)
                .retrieve()
                .bodyToMono(MovieSummary.class);
    }
}

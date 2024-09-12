package web.crea.movie.catalog.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.crea.movie.catalog.model.CatalogItem;
import web.crea.movie.catalog.model.Movie;
import web.crea.movie.catalog.model.UserRating;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    private static final Logger LOG = LoggerFactory.getLogger(MovieCatalogController.class);

    @Autowired
    private WebClient.Builder webClientBuilder;


   private final ReactiveCircuitBreaker readingListCircuitBreaker;

    public MovieCatalogController(ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        this.readingListCircuitBreaker = circuitBreakerFactory.create("userRatingsService");
    }

    @GetMapping("/{userId}")
    public Mono<List<CatalogItem>> getCatalog(@PathVariable Long userId) {

        WebClient webClient = webClientBuilder.build();

        // Call to rating data service
        Mono<UserRating> ratings = readingListCircuitBreaker.run(webClient
                .get()
                .uri("http://rating.data/rating-data/users/" + userId)
                .retrieve()
                .bodyToMono(UserRating.class), throwable -> {
            LOG.warn("Fallback for user ratings due to: {}", throwable.getMessage());
            return Mono.just(new UserRating(Collections.emptyList()));
        });

        return ratings.flatMap(userRating ->
                Flux.fromIterable(userRating.getRatings())
                        .flatMap(rating -> {
                            // Call to movie info service
                            Mono<Movie> movieMono = webClient
                                    .get()
                                    .uri("http://movie.info/movies/" + rating.getMovieId())
                                    .retrieve()
                                    .bodyToMono(Movie.class);

                            return movieMono.map(movie ->
                                    new CatalogItem(movie.getName(), "test", rating.getRating()));
                        }).collectList()
        );
    }

    // Fallback method for Circuit Breaker
    public Mono<List<CatalogItem>> getFallbackCatalog(Long userId, Throwable ex) {
        System.out.println("Fallback for user: " + userId + " due to: " + ex.getMessage());
        return Mono.just(Arrays.asList(
                new CatalogItem("No Movie", "Fallback description", 0)
        ));
    }
}

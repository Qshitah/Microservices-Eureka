package web.crea.rating.data.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.crea.rating.data.model.Rating;
import web.crea.rating.data.model.UserRating;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/rating-data")
public class RatingDataController {

    @GetMapping("/{movieId}")
    private Rating getRating(@PathVariable Long movieId){
        return new Rating(movieId,5);
    }

    @GetMapping("/users/{userId}")
    private UserRating getUserRating(@PathVariable Long userId){
        List<Rating> ratings = Arrays.asList(
                new Rating(55L,5),
                new Rating(100L,6)
        );

        UserRating userRating = new UserRating();
        userRating.setRatings(ratings);
        return userRating;
    }
}

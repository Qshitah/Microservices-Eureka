package web.crea.movie.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogItem {

    private String name;
    private String desc;
    private int rating;


}

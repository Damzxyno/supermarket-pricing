package entity;

import lombok.*;
import java.util.LinkedList;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private long id;
    private String name;
    private double price;
    private boolean measuredPerKg;
    private ProductCategory Category;
}

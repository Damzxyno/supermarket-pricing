package entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductGroupCache extends HashMap<ProductCategory, List<ProductGroupCacheItems>> {
}



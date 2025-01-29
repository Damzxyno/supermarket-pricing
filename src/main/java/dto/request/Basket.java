package dto.request;

import entity.Product;
import java.util.HashMap;


public class Basket extends HashMap<Product, Double> {
    public void put(Product product, int quantity){
        super.put(product, (double) quantity);
    }
}

import dto.request.Basket;
import entity.PricingStructure;
import entity.Product;
import entity.ProductCategory;
import interfaces.BasketPrinter;
import interfaces.BasketCalculator;
import services.BasketPrinterImpl;
import services.BasketCalculatorImpl;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Products
        var beans = Product.builder().id(1L).name("Beans Tin").Category(ProductCategory.FOOD).price(0.50).build();
        var onions = Product.builder().id(2).name("Onions").Category(ProductCategory.FOOD).price(0.29).build();
        var cocacola = Product.builder().id(3L).name("Coca-cola").Category(ProductCategory.DRINK).price(0.70).build();
        var oranges = Product.builder().id(4L).name("Oranges").Category(ProductCategory.FOOD).measuredPerKg(true).price(1.99).build();

        var bassPaleAle = Product.builder().id(5L).Category(ProductCategory.ALE).name("Bass Pale Ale").price(2.5).build();
        var greenKingIpa = Product.builder().id(6L).Category(ProductCategory.ALE).name("Green King IPA").price(3).build();
        var timothyTailor = Product.builder().id(7L).Category(ProductCategory.ALE).name("Timothy Taylor").price(3.5).build();



        // uniProductPricing
        List<PricingStructure> beansPricings = new LinkedList<>();
        var beans3For2ItemsPricing = PricingStructure.builder().name("Beans 3 for 2").quantity(3).newQuantity(2).build();
        beansPricings.add(beans3For2ItemsPricing);

        List<PricingStructure> cocacolaPricings = new LinkedList<>();
        var cocacola2For1PoundsPricing = PricingStructure.builder().name("Coke 2 for £1").quantity(2).price(1).build();
        cocacolaPricings.add(cocacola2For1PoundsPricing);

        Map<Long, List<PricingStructure>> uniProductPricing = new HashMap<>();
        uniProductPricing.put(beans.getId(), beansPricings);
        uniProductPricing.put(cocacola.getId(), cocacolaPricings);

        // categoryProductPricing
        var aleSet = new HashSet<>(List.of(bassPaleAle.getId(), greenKingIpa.getId(), timothyTailor.getId()));
        var ale3for6PoundsPricing = PricingStructure.builder().name("Any 3 ales from ales set").quantity(3).price(6).build();

        Map<ProductCategory, PricingStructure> productCategoryPricing = new HashMap<>();
        productCategoryPricing.put(ProductCategory.ALE, ale3for6PoundsPricing);

        Map<ProductCategory, Set<Long>> productCategoryProductSet = new HashMap<>();
        productCategoryProductSet.put(ProductCategory.ALE, aleSet);



        // Basket
        var basket = new Basket();
        basket.put(beans, 6);
        basket.put(cocacola, 2);
        basket.put(oranges, 0.2);

        // Ales
//        basket.put(bassPaleAle, 2.0);
//        basket.put(timothyTailor, 2.0);
//        basket.put(greenKingIpa, 2.0);




        // Service
        BasketCalculator pc = new BasketCalculatorImpl(
                uniProductPricing,
                productCategoryPricing,
                productCategoryProductSet);
        BasketPrinter bp = new BasketPrinterImpl();



        var receiptResponse = pc.calculateBasketPrice(basket);
        bp.printPriceCalculationBreakDown(receiptResponse);
    }
}

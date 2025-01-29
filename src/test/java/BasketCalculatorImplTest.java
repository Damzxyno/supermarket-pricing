import dto.request.Basket;
import dto.response.ReceiptResponse;
import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.BasketCalculatorImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BasketCalculatorImplTest {
    private BasketCalculatorImpl basketCalculator;
    private Map<Long, List<PricingStructure>> uniProductPricing;
    private Map<ProductCategory, PricingStructure> productCategoryPricing;
    private Map<ProductCategory, Set<Long>> productCategoryProductSet;
    private Product beans, cocacola, onions, oranges, bassPaleAle, greenKingIpa, timothyTailor;

    @BeforeEach
    public void setUp() {
        beans = Product.builder().id(1L).name("Beans Tin").Category(ProductCategory.FOOD).price(0.50).build();
        cocacola = Product.builder().id(2L).name("Coca-cola").Category(ProductCategory.DRINK).price(0.70).build();
        onions = Product.builder().id(3L).name("Onions").Category(ProductCategory.FOOD).measuredPerKg(true).price(0.29).build();
        oranges = Product.builder().id(4L).name("Oranges").Category(ProductCategory.FOOD).measuredPerKg(true).price(1.99).build();

        bassPaleAle = Product.builder().id(5L).Category(ProductCategory.ALE).name("Bass Pale Ale").price(2.5).build();
        greenKingIpa = Product.builder().id(6L).Category(ProductCategory.ALE).name("Green King IPA").price(3).build();
        timothyTailor = Product.builder().id(7L).Category(ProductCategory.ALE).name("Timothy Taylor").price(3.5).build();

        // Product-specific pricing
        uniProductPricing = new HashMap<>();
        uniProductPricing.put(beans.getId(), List.of(PricingStructure.builder().name("Beans 3 for 2").quantity(3).newQuantity(2).build()));
        uniProductPricing.put(cocacola.getId(), List.of(PricingStructure.builder().name("Coke 2 for £1").quantity(2).price(1).build()));

        // Category pricing
        productCategoryPricing = new HashMap<>();
        productCategoryPricing.put(ProductCategory.ALE, PricingStructure.builder().name("Any 3 ales for £6").quantity(3).price(6).build());

        // Category-product mapping
        productCategoryProductSet = new HashMap<>();
        productCategoryProductSet.put(ProductCategory.ALE, new HashSet<>(List.of(bassPaleAle.getId(), greenKingIpa.getId(), timothyTailor.getId())));

        basketCalculator = new BasketCalculatorImpl(uniProductPricing, productCategoryPricing, productCategoryProductSet);
    }

    @Test
    public void testCalculateBasketPriceWithEmptyBasket_ShouldReturnZeroEmptyItemsAndZeroTotals() {
        Basket basket = new Basket();
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(0.00, receipt.getGrandTotal());
        assertEquals(0.00, receipt.getSavingsSubTotal());
        assertEquals(0.00, receipt.getProductSubTotal());

        assertTrue(receipt.getProductItems().isEmpty());
        assertTrue(receipt.getSavingsItems().isEmpty());
    }
    @Test
    public void testCalculateBasketPriceWithSingleBeansProductNotEligibleForDiscount3ItemsFor1() {
        Basket basket = new Basket();
        basket.put(beans, 1);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);


        assertEquals(0.50, receipt.getGrandTotal());
        assertEquals(0.00, receipt.getSavingsSubTotal());
        assertEquals(0.50, receipt.getProductSubTotal());

        assertEquals(1, receipt.getProductItems().size());
        assertTrue(receipt.getSavingsItems().isEmpty());
    }

    @Test
    public void testCalculateBasketPriceWithTwoBeansProductNotEligibleForDiscount3ItemsFor1() {
        Basket basket = new Basket();
        basket.put(beans, 2);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);


        assertEquals(1.00, receipt.getGrandTotal());
        assertEquals(0.00, receipt.getSavingsSubTotal());
        assertEquals(1.00, receipt.getProductSubTotal());

        assertEquals(1, receipt.getProductItems().size());
        assertTrue(receipt.getSavingsItems().isEmpty());
    }

    @Test
    public void testCalculateBasketPriceWithThreeBeansProductEligibleForDiscount3ItemsFor1() {
        Basket basket = new Basket();
        basket.put(beans, 3);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);


        assertEquals(1.00, receipt.getGrandTotal());
        assertEquals(-0.50, receipt.getSavingsSubTotal());
        assertEquals(1.50, receipt.getProductSubTotal());

        assertEquals(1, receipt.getProductItems().size());
        assertEquals(1, receipt.getSavingsItems().size());
    }
    @Test
    public void testCalculateBasketPriceWith4BeansProductPartlyEligibleForDiscount3ItemsFor1() {
        Basket basket = new Basket();
        basket.put(beans, 4);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);


        assertEquals(1.50, receipt.getGrandTotal());
        assertEquals(-0.50, receipt.getSavingsSubTotal());
        assertEquals(2.00, receipt.getProductSubTotal());

        assertEquals(1, receipt.getProductItems().size());
        assertEquals(1, receipt.getSavingsItems().size());
    }

    @Test
    public void testCalculateBasketPriceWith6BeansProductEligibleForDiscount3ItemsFor1() {
        Basket basket = new Basket();
        basket.put(beans, 6);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);


        assertEquals(2.00, receipt.getGrandTotal());
        assertEquals(-1.00, receipt.getSavingsSubTotal());
        assertEquals(3.00, receipt.getProductSubTotal());

        assertEquals(1, receipt.getProductItems().size());
        assertEquals(1, receipt.getSavingsItems().size());
    }
    @Test
    public void testCalculateBasketPriceWithSingleCocaColaNotEligibleForDiscount2ForPriceOf1() {
        Basket basket = new Basket();
        basket.put(cocacola, 1);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(0.70, receipt.getGrandTotal());
        assertEquals(0.00, receipt.getSavingsSubTotal());
        assertEquals(0.70, receipt.getProductSubTotal());

        assertEquals(1, receipt.getProductItems().size());
        assertTrue(receipt.getSavingsItems().isEmpty());
    }

    @Test
    public void testCalculateBasketPriceWithTwoCocaColaEligibleForDiscount2ForPriceOf1() {
        Basket basket = new Basket();
        basket.put(cocacola, 2);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(1.00, receipt.getGrandTotal());
        assertEquals(-0.40, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(1.40, receipt.getProductSubTotal());

        assertEquals(1, receipt.getProductItems().size());
        assertEquals(1, receipt.getSavingsItems().size());
    }

    @Test
    public void testCalculateBasketPriceWithThreeCocaColaPartlyEligibleForDiscount2ForPriceOf1() {
        Basket basket = new Basket();
        basket.put(cocacola, 3);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(1.70, receipt.getGrandTotal());
        assertEquals(-0.40, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(2.10, receipt.getProductSubTotal(), 0.1);

        assertEquals(1, receipt.getProductItems().size());
        assertEquals(1, receipt.getSavingsItems().size());
    }

    @Test
    public void testCalculateBasketPriceWithFourCocaColaEligibleForDiscount2ForPriceOf1() {
        Basket basket = new Basket();
        basket.put(cocacola, 4);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(2.00, receipt.getGrandTotal());
        assertEquals(-0.80, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(2.80, receipt.getProductSubTotal(), 0.1);

        assertEquals(1, receipt.getProductItems().size());
        assertEquals(1, receipt.getSavingsItems().size());
    }



    @Test
    public void testCalculateBasketPriceWithMixedItems2EligibleForDiscounts() {
        Basket basket = new Basket();
        basket.put(beans, 3);
        basket.put(cocacola, 2);
        basket.put(oranges, 0.2);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(2.40, receipt.getGrandTotal(), 0.1);
        assertEquals(-0.90, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(3.30, receipt.getProductSubTotal(), 0.1);

        assertEquals(3, receipt.getProductItems().size());
        assertEquals(2, receipt.getSavingsItems().size());
    }

    @Test
    public void testCalculateBasketPriceWithSingleAleItemsNotEligibleForDiscounts3InSet() {
        Basket basket = new Basket();
        basket.put(bassPaleAle, 1);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(2.50, receipt.getGrandTotal(), 0.1);
        assertEquals(0.00, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(2.50, receipt.getProductSubTotal(), 0.1);

        assertEquals(1, receipt.getProductItems().size());
        assertTrue(receipt.getSavingsItems().isEmpty());
    }

    @Test
    public void testCalculateBasketPriceWith2AleItemsNotEligibleForDiscounts3InSet() {
        Basket basket = new Basket();
        basket.put(bassPaleAle, 2);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(5.00, receipt.getGrandTotal(), 0.1);
        assertEquals(0.0, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(5.00, receipt.getProductSubTotal(), 0.1);

        assertEquals(1, receipt.getProductItems().size());
        assertTrue(receipt.getSavingsItems().isEmpty());
    }

    @Test
    public void testCalculateBasketPriceWith3AleItemsEligibleForDiscounts3InSet() {
        Basket basket = new Basket();
        basket.put(bassPaleAle, 3);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(6.00, receipt.getGrandTotal(), 0.1);
        assertEquals(-1.50, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(7.50, receipt.getProductSubTotal(), 0.1);

        assertEquals(1, receipt.getProductItems().size());
        assertEquals(1, receipt.getSavingsItems().size());
    }


    @Test
    public void testCalculateBasketPriceWith2MixedAleItemsNotEligibleForDiscounts3InSet() {
        Basket basket = new Basket();
        basket.put(bassPaleAle, 1);
        basket.put(timothyTailor, 1);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(6.00, receipt.getGrandTotal(), 0.1);
        assertEquals(0.00, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(6.00, receipt.getProductSubTotal(), 0.1);

        assertEquals(2, receipt.getProductItems().size());
        assertTrue(receipt.getSavingsItems().isEmpty());
    }

    @Test
    public void testCalculateBasketPriceWith3MixedAleItemsEligibleForDiscounts3InSet_I() {
        Basket basket = new Basket();
        basket.put(bassPaleAle, 1);
        basket.put(timothyTailor, 1);
        basket.put(greenKingIpa, 1);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(6.0, receipt.getGrandTotal(), 0.1);
        assertEquals(-3.00, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(9.00, receipt.getProductSubTotal(), 0.1);

        assertEquals(3, receipt.getProductItems().size());
        assertEquals(1, receipt.getSavingsItems().size());
    }

    @Test
    public void testCalculateBasketPriceWith4MixedAleItemsPartlyEligibleForDiscounts3InSet_II() {
        Basket basket = new Basket();
        basket.put(bassPaleAle, 2);
        basket.put(timothyTailor, 1);
        basket.put(greenKingIpa, 1);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(9.50, receipt.getGrandTotal(), 0.1);
        assertEquals(-2.00, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(11.50, receipt.getProductSubTotal(), 0.1);

        assertEquals(3, receipt.getProductItems().size());
        assertEquals(1, receipt.getSavingsItems().size());
    }

    @Test
    public void testCalculateBasketPriceWith6MixedAleItemsEligibleForDiscounts3InSet_II() {
        Basket basket = new Basket();
        basket.put(bassPaleAle, 2);
        basket.put(timothyTailor, 1);
        basket.put(greenKingIpa, 3);
        ReceiptResponse receipt = basketCalculator.calculateBasketPrice(basket);

        assertEquals(12.00, receipt.getGrandTotal(), 0.1);
        assertEquals(-5.50, receipt.getSavingsSubTotal(), 0.1);
        assertEquals(17.5, receipt.getProductSubTotal(), 0.1);

        assertEquals(3, receipt.getProductItems().size());
        assertEquals(2, receipt.getSavingsItems().size());
    }
}

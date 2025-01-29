package services;

import dto.request.Basket;
import dto.response.ReceiptPriceSavingItem;
import dto.response.ReceiptProductItem;
import dto.response.ReceiptResponse;
import entity.*;
import interfaces.BasketCalculator;

import java.util.*;

public class BasketCalculatorImpl implements BasketCalculator {
    private final Map<Long, List<PricingStructure>> uniProductPricing;
    private final Map<ProductCategory, PricingStructure> productCategoryPricing;
    private final Map<ProductCategory, Set<Long>> productCategoryProductSet;
    public BasketCalculatorImpl(Map<Long, List<PricingStructure>> uniProductPricing, Map<ProductCategory, PricingStructure> productCategoryPricing, Map<ProductCategory, Set<Long>> productCategoryProductSet){
        this.uniProductPricing = uniProductPricing;
        this.productCategoryPricing = productCategoryPricing;
        this.productCategoryProductSet = productCategoryProductSet;
    }

    @Override
    public ReceiptResponse calculateBasketPrice(Basket basket) {
        var receiptRes = ReceiptResponse
                .builder()
                .productItems(new ArrayList<>())
                .savingsItems(new ArrayList<>())
                .build();
        var productGroupCache = new ProductGroupCache();

        basket.entrySet().forEach(entry -> {
            var product = entry.getKey();
            var quantity = entry.getValue();

            if (productCategoryProductSet.containsKey(product.getCategory()) && productCategoryProductSet.get(product.getCategory()).contains(product.getId())){
                productGroupCache.compute(product.getCategory(), (k, v) -> {
                    if (v == null){
                        return new ArrayList<>(List.of(new ProductGroupCacheItems(product, quantity.intValue())));
                    } else {
                        v.add(new ProductGroupCacheItems(product, quantity.intValue()));
                        return v;
                    }
                });
            } else {
                calculateProductPriceAndAddToReceipt(product, quantity, receiptRes);
            }

            addProductItemToReceipt(receiptRes, product, quantity);
        });

        productGroupCache.forEach((category, productList) -> calculateCategoryProductPriceAndAddToReceipt(category, productList, receiptRes));
        return receiptRes;
    }

    private void calculateCategoryProductPriceAndAddToReceipt(ProductCategory productCategory, List<ProductGroupCacheItems> productCacheItem, ReceiptResponse receiptResponse) {
        var productQueue =  new PriorityQueue<Product>(Comparator.comparingDouble(Product::getPrice));
        var pricing = productCategoryPricing.get(productCategory);
        productCacheItem.forEach(x -> {
            for (int i = 0; i < x.getQuantity(); i++){
                productQueue.add(x.getProducts());
            }
        });

        var price = 0.0;
        var remainingQuantity = productQueue.size();

        var bundles = Math.floor(remainingQuantity / pricing.getQuantity());
        remainingQuantity = remainingQuantity % (int) pricing.getQuantity();

        price += bundles * pricing.getPrice();

        if (bundles > 0){
            var pricingQty = (int) pricing.getQuantity();
            for (int i = 0; i < bundles; i++){
                double originalPrice = 0.0;
                for (int j = 0; j < pricingQty; j++){
                    originalPrice += productQueue.poll().getPrice();
                }
                var savings = (originalPrice - pricing.getPrice()) * -1;
                var priceSavingItem = ReceiptPriceSavingItem.builder()
                        .name(pricing.getName())
                        .count(1)
                        .deduction(savings)
                        .build();
                receiptResponse.getSavingsItems().add(priceSavingItem);
                receiptResponse.setSavingsSubTotal(receiptResponse.getSavingsSubTotal() + savings);
            }
        }

        while (!productQueue.isEmpty()){
            var productPrice = productQueue.poll().getPrice();
            price += productPrice;
        }
        receiptResponse.setGrandTotal(receiptResponse.getGrandTotal() + price);
    }

    private void calculateProductPriceAndAddToReceipt(Product product, double quantity, ReceiptResponse receiptResponse) {
        var price = 0.0;

        if (uniProductPricing.containsKey(product.getId())){
            for (var pricing : uniProductPricing.get(product.getId())){
                var bundles = Math.floor(quantity / pricing.getQuantity());
                quantity = quantity % pricing.getQuantity();

                var newPrice = (bundles * pricing.getPrice()) + (pricing.getNewQuantity() * product.getPrice() * bundles);
                price += newPrice;

                if (bundles > 0){
                    var originalPrice = pricing.getQuantity() * product.getPrice() * bundles;
                    var savings = (originalPrice - newPrice) * -1;

                    var priceSavingItem = ReceiptPriceSavingItem.builder()
                            .name(pricing.getName())
                            .count((long)bundles)
                            .deduction(savings)
                            .build();
                    receiptResponse.getSavingsItems().add(priceSavingItem);
                    receiptResponse.setSavingsSubTotal(receiptResponse.getSavingsSubTotal() + savings);

                }
            }
        }

        price += quantity * product.getPrice();
        receiptResponse.setGrandTotal(receiptResponse.getGrandTotal() + price);
    }

    private void addProductItemToReceipt(ReceiptResponse receiptResponse, Product product, double quantity) {
        var productSubtotal = product.getPrice() * quantity;
        String productName = !product.isMeasuredPerKg() ? product.getName() : formatProductName(product, quantity);
        double productPrice = !product.isMeasuredPerKg() ? product.getPrice() : productSubtotal;
        int newQuantity = (int) Math.ceil(quantity);

        var receiptProductItem  = ReceiptProductItem
                    .builder()
                    .name(productName)
                    .quantity(newQuantity)
                    .price(productPrice)
                    .build();

        receiptResponse.getProductItems().add(receiptProductItem);
        receiptResponse.setProductSubTotal(receiptResponse.getProductSubTotal() + productSubtotal);
    }

    private String formatProductName(Product product, double quantity) {
        if (!product.isMeasuredPerKg()) {
            return product.getName();
        }
        return String.format("%s %.2f kg @ £ %.2f/kg", product.getName(), quantity, product.getPrice());
    }
}

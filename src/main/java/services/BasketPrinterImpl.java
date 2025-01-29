package services;

import dto.response.ReceiptResponse;
import interfaces.BasketPrinter;

public class BasketPrinterImpl implements BasketPrinter {
    private static final String DELIMETER = "--------------------------------------";
    @Override
    public void printPriceCalculationBreakDown(ReceiptResponse receiptResponse) {
        // Define table column headers
        System.out.printf("%-30s %-10s%n", "Product", "Price");
        System.out.println(DELIMETER);

        // Print products in the basket
        for (var productEntry : receiptResponse.getProductItems()) {
            for (int i = 0; i < productEntry.getQuantity(); i++) {
                System.out.printf("%-30s £ %-10.2f%n", productEntry.getName(), productEntry.getPrice());
            }
        }

        // Print Sub-total
        System.out.println(DELIMETER);
        System.out.printf("%-30s £ %-15.2f%n", "Sub-total", receiptResponse.getProductSubTotal());

        // Print Savings
        System.out.println("Savings");
        if (receiptResponse.getSavingsItems().isEmpty()) {
            System.out.printf("%-30s %s%n", "", "Nil");
        } else {
            for (var savingsEntry : receiptResponse.getSavingsItems()) {
                for (int i = 0; i < savingsEntry.getCount(); i++) {
                    System.out.printf("%-30s £ %-15.2f%n", savingsEntry.getName(), savingsEntry.getDeduction());
                }
            }
            System.out.printf("%-30s £ %-15.2f%n", "Total savings", receiptResponse.getSavingsSubTotal());
        }

        // Print Total to pay
        System.out.println(DELIMETER);
        System.out.printf("%-30s £ %-15.2f%n", "Total to pay", receiptResponse.getGrandTotal());
    }
}

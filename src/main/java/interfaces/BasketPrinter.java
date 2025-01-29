package interfaces;

import dto.response.ReceiptResponse;

public interface BasketPrinter {

    void printPriceCalculationBreakDown(ReceiptResponse breakdown);
}

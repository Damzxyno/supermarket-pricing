package interfaces;

import dto.request.Basket;
import dto.response.ReceiptResponse;


public interface BasketCalculator {
    ReceiptResponse calculateBasketPrice (Basket basket);
}

package dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptResponse {
    private List<ReceiptProductItem> productItems;
    private List<ReceiptPriceSavingItem> savingsItems;
    private double productSubTotal;
    private double savingsSubTotal;
    private double grandTotal;
}

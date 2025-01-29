package dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptProductItem {
    private String name;
    private double price;
    private int quantity;
}

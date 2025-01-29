package dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptPriceSavingItem {
    private String name;
    private long count;
    private double deduction;
}

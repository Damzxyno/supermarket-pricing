package entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PricingStructure {
    private String name;
    private double quantity;
    private double newQuantity;
    private double price;
}

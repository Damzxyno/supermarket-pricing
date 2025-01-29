# Supermarket Pricing Calculator

## Overview
Coding Exercise:
The task is to model a supermarket pricing calculator in software. This is inspired by PragDave’s Supermarket Kata.
You should write a program which works out how to price a shopping basket, allowing for different pricing structures including:
- Three tins of beans for the price of two
- Onions for 29p / kg
- Two cans of coca-cola for £1
- Any 3 ales from the set {…} for £6

## Features
- **Bulk Discounts:**
    - Three tins of beans for the price of two
    - Two cans of Coca-Cola for £1
    - Any three ales for £6
- **Weight-Based Pricing:**
    - Onions priced at 29p/kg
- **Flexible Pricing Structure:**
    - Supports product-specific and category-wide pricing rules

## Technologies Used
- **Java 11+**
- **JUnit 5** for unit testing
- **Maven** for dependency management

## Setup and Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/damzxyno/supermarket-pricing.git
   ```
2. Navigate to the project directory:
   ```sh
   cd supermarket-pricing
   ```
3. Build the project:
   ```sh
   mvn clean install
   ```
4. Run tests:
   ```sh
   mvn test
   ```

## Usage
To use the pricing calculator:
1. Create a basket and add products.
2. Use `BasketCalculatorImpl` to calculate the total price.
3. Optionally, print the receipt using `BasketPrinterImpl`.

Example:
```java
Basket basket = new Basket();
basket.put(beans, 6);
basket.put(cocacola, 2);
basket.put(oranges, 0.2);
        
BasketCalculator pc = new BasketCalculatorImpl(
        uniProductPricing,
        productCategoryPricing,
        productCategoryProductSet);
    
BasketPrinter bp = new BasketPrinterImpl();
ReceiptResponse receiptResponse = pc.calculateBasketPrice(basket);
bp.printPriceCalculationBreakDown(receiptResponse);
```

Sample Console Display:

<img src="src/main/resources/receipt.png" alt="BasketPrinter Console" width="300">



# Zakat Calculator (Java OOP & Swing GUI)

A Java Object-Oriented Programming (OOP) mini-project that calculates Zakat across multiple asset categories, available in both a **Swing Desktop GUI** and a **CLI** version.

## OOP Design

- **`Asset`** (`abstract class`): Defines the common contract (`calculateZakat()`, `isEligible()`, `getType()`, `getInfo()`) for all asset types.
- **Subclasses**:
  - `CashAsset` — Cash & bank deposits (2.5% rate, Nisab threshold).
  - `GoldAsset` — Gold bullion & jewelry (converts Thai Baht weight to grams, checks 85g Nisab).
  - `TradeAsset` — Business inventory/trade assets minus business debt (`getNetValue()`).
  - `AgricultureAsset` — Agricultural produce (checks 653 kg Nisab; 5% irrigated vs. 10% rain-fed).
- **`ZakatManager`**: Controller managing an array of `Asset` objects polymorphically (`addAsset`, `removeAsset`, totals, and reporting).
- **Interfaces**:
  - `ZakatCalculatorGUI.java` — Interactive Java Swing desktop UI.
  - `ZakatCalculatorApp.java` — Console/menu-driven CLI.

See [Code_Explanation.md](Code_Explanation.md) for a detailed class-by-class walkthrough (in Thai).

## How to Run

Compile all Java source files:
```bash
javac *.java
```

Run the **GUI** version:
```bash
java ZakatCalculatorGUI
```

Or run the **CLI** version:
```bash
java ZakatCalculatorApp
```

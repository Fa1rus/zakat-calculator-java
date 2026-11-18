// ทรัพย์สินประเภท เงินสด / เงินฝากธนาคาร
public class CashAsset extends Asset {

    private static final double ZAKAT_RATE = 0.025;   // อัตราซะกาต 2.5%
    private static final double NISAB = 370000.0;      // นิศอบเทียบเท่าทองคำ 85 กรัม (370,000 บาท)

    public CashAsset(String name, double value) {
        super(name, value);
    }

    @Override
    public boolean isEligible() {
        return value >= NISAB;
    }

    @Override
    public double calculateZakat() {
        if (isEligible()) {
            return value * ZAKAT_RATE;
        }
        return 0;
    }

    @Override
    public String getType() {
        return "เงินสด / เงินฝากธนาคาร";
    }
}

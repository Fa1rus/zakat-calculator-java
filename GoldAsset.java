// ทรัพย์สินประเภท ทองคำ / เงิน
public class GoldAsset extends Asset {

    private static final double ZAKAT_RATE = 0.025;     // อัตราซะกาต 2.5%
    private static final double NISAB_WEIGHT = 85.0;    // นิศอบทองคำ 85 กรัม
    private static final double FIXED_PRICE_PER_GRAM = 4352.941176; // ราคาทองคำรวม 370,000 บาท ต่อ 85 กรัม (370000.0 / 85.0)

    private double weightInBaht;
    private String goldType;
    private double weightInGrams;

    public GoldAsset(String name, double weightInBaht, String goldType) {
        super(name, 0); // จะคำนวณมูลค่าใหม่ด้านล่าง
        this.weightInBaht = weightInBaht;
        this.goldType = goldType;
        
        if (goldType.equals("ทองแท่ง")) {
            this.weightInGrams = weightInBaht * 15.244; // ทองแท่ง
        } else {
            this.weightInGrams = weightInBaht * 15.16; // ทองรูปพรรณ
        }
        
        this.value = this.weightInGrams * FIXED_PRICE_PER_GRAM;
    }

    public double getWeightInGrams() {
        return weightInGrams;
    }

    @Override
    public boolean isEligible() {
        return weightInGrams >= NISAB_WEIGHT;
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
        return "ทองคำ (" + goldType + ")";
    }
    
    @Override
    public String getInfo() {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        StringBuilder sb = new StringBuilder();
        sb.append("ประเภททรัพย์สิน : ").append(getType()).append("\n");
        sb.append("ชื่อรายการ      : ").append(name).append("\n");
        sb.append("น้ำหนัก         : ").append(df.format(weightInBaht)).append(" บาททอง (").append(df.format(weightInGrams)).append(" กรัม)\n");
        sb.append("มูลค่า          : ").append(df.format(value)).append(" บาท\n");
        
        if (isEligible()) {
            sb.append("สถานะ          : ครบเกณฑ์นิศอบ (ต้องจ่ายซะกาต)\n");
            sb.append("ยอดซะกาต       : ").append(df.format(calculateZakat())).append(" บาท\n");
        } else {
            sb.append("สถานะ          : ยังไม่ครบเกณฑ์นิศอบ (ไม่ต้องจ่ายซะกาต)\n");
        }
        sb.append("-------------------------------------------------\n");
        return sb.toString();
    }
}

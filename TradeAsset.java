// ทรัพย์สินประเภท สินค้า / ทรัพย์สินทางการค้า (ธุรกิจ)
public class TradeAsset extends Asset {

    private static final double ZAKAT_RATE = 0.025;  // อัตราซะกาต 2.5%
    private static final double NISAB = 370000.0;      // นิศอบเทียบเท่าทองคำ 85 กรัม (370,000 บาท)

    private double debt;  // หนี้สินที่เกี่ยวข้องกับกิจการ (หักออกก่อนคำนวณ)

    public TradeAsset(String name, double totalValue, double debt) {
        super(name, totalValue);
        this.debt = debt;
    }

    // มูลค่าสุทธิหลังหักหนี้สิน
    public double getNetValue() {
        double net = value - debt;
        return net > 0 ? net : 0;
    }

    @Override
    public boolean isEligible() {
        return getNetValue() >= NISAB;
    }

    @Override
    public double calculateZakat() {
        if (isEligible()) {
            return getNetValue() * ZAKAT_RATE;
        }
        return 0;
    }

    @Override
    public String getType() {
        return "สินค้า / ทรัพย์สินทางการค้า";
    }

    @Override
    public String getInfo() {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        StringBuilder sb = new StringBuilder();
        sb.append("ประเภททรัพย์สิน : ").append(getType()).append("\n");
        sb.append("ชื่อรายการ      : ").append(name).append("\n");
        sb.append("มูลค่ารวม       : ").append(df.format(value)).append(" บาท\n");
        sb.append("หนี้สิน         : ").append(df.format(debt)).append(" บาท\n");
        sb.append("มูลค่าสุทธิ      : ").append(df.format(getNetValue())).append(" บาท\n");
        
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

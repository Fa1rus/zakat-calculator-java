// ทรัพย์สินประเภท ผลผลิตทางการเกษตร
public class AgricultureAsset extends Asset {

    private static final double NISAB_WEIGHT = 653.0; // นิศอบ 5 วะสัก (ประมาณ กก.)

    private double weightKg;
    private boolean irrigatedByHuman; // true = ใช้แรงงาน/ต้นทุนในการให้น้ำ (5%), false = อาศัยน้ำฝนธรรมชาติ (10%)

    public AgricultureAsset(String name, double weightKg, double pricePerKg, boolean irrigatedByHuman) {
        super(name, weightKg * pricePerKg);
        this.weightKg = weightKg;
        this.irrigatedByHuman = irrigatedByHuman;
    }

    @Override
    public boolean isEligible() {
        return weightKg >= NISAB_WEIGHT;
    }

    @Override
    public double calculateZakat() {
        if (isEligible()) {
            double rate = irrigatedByHuman ? 0.05 : 0.10;
            return value * rate;
        }
        return 0;
    }

    @Override
    public String getType() {
        return "ผลผลิตทางการเกษตร";
    }
}

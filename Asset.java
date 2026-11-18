import java.text.DecimalFormat;

// คลาสแม่ (Abstract Class) แทนทรัพย์สินแต่ละประเภทที่ต้องคำนวณซะกาต
public abstract class Asset {

    protected String name;
    protected double value;

    public Asset(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    // เมธอดที่คลาสลูกแต่ละคลาสต้องเขียนขึ้นเอง (แสดงการ Overriding / Polymorphism)
    public abstract double calculateZakat();

    public abstract boolean isEligible();

    public abstract String getType();

    // เมธอดแสดงผลกลาง ใช้ร่วมกันทุกคลาสลูก
    public void displayInfo() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        System.out.println("ประเภททรัพย์สิน : " + getType());
        System.out.println("ชื่อรายการ      : " + name);
        System.out.println("มูลค่า          : " + df.format(value) + " บาท");

        if (isEligible()) {
            System.out.println("สถานะ          : ครบเกณฑ์นิศอบ (ต้องจ่ายซะกาต)");
            System.out.println("ยอดซะกาต       : " + df.format(calculateZakat()) + " บาท");
        } else {
            System.out.println("สถานะ          : ยังไม่ครบเกณฑ์นิศอบ (ไม่ต้องจ่ายซะกาต)");
        }
        System.out.println("-------------------------------------------------");
    }

    // เมธอดสำหรับดึงข้อมูลเป็น String (สำหรับ GUI)
    public String getInfo() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        StringBuilder sb = new StringBuilder();
        sb.append("ประเภททรัพย์สิน : ").append(getType()).append("\n");
        sb.append("ชื่อรายการ      : ").append(name).append("\n");
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

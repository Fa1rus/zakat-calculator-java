import java.util.Scanner;
import java.text.DecimalFormat;

// คลาสหลักของโปรแกรม ทำหน้าที่แสดงเมนูและรับข้อมูลจากผู้ใช้
public class ZakatCalculatorApp {

    static Scanner sc = new Scanner(System.in);
    static ZakatManager manager = new ZakatManager(50);
    static DecimalFormat df = new DecimalFormat("#,##0.00");

    public static void main(String[] args) {
        int choice;

        do {
            showMenu();
            choice = readInt("เลือกเมนู : ");

            switch (choice) {
                case 1:
                    addCash();
                    break;
                case 2:
                    addGold();
                    break;
                case 3:
                    addTrade();
                    break;
                case 4:
                    addAgriculture();
                    break;
                case 5:
                    manager.showAllAssets();
                    break;
                case 6:
                    calculateAndShowTotal();
                    break;
                case 0:
                    System.out.println("ขอบคุณที่ใช้โปรแกรมคำนวณซะกาตทรัพย์สิน");
                    break;
                default:
                    System.out.println("กรุณาเลือกหมายเลขเมนูที่ถูกต้อง");
            }
            System.out.println();

        } while (choice != 0);

        sc.close();
    }

    static void showMenu() {
        System.out.println("===================================================");
        System.out.println("        โปรแกรมคำนวณซะกาตทรัพย์สิน (Zakat Calculator)");
        System.out.println("===================================================");
        System.out.println("1. เพิ่มทรัพย์สิน : เงินสด / เงินฝากธนาคาร");
        System.out.println("2. เพิ่มทรัพย์สิน : ทองคำ / เงิน");
        System.out.println("3. เพิ่มทรัพย์สิน : สินค้า / ทรัพย์สินทางการค้า");
        System.out.println("4. เพิ่มทรัพย์สิน : ผลผลิตทางการเกษตร");
        System.out.println("5. แสดงรายการทรัพย์สินทั้งหมด");
        System.out.println("6. คำนวณยอดซะกาตทั้งหมด");
        System.out.println("0. ออกจากโปรแกรม");
        System.out.println("===================================================");
    }

    static void addCash() {
        System.out.print("ชื่อรายการ (เช่น เงินฝากออมทรัพย์) : ");
        String name = sc.nextLine();
        double value = readDouble("มูลค่าเงินสด/เงินฝาก (บาท) : ");
        manager.addAsset(new CashAsset(name, value));
        System.out.println("เพิ่มรายการเรียบร้อยแล้ว");
    }

    static void addGold() {
        System.out.print("ชื่อรายการ (เช่น ทองคำแท่ง) : ");
        String name = sc.nextLine();
        int type = readInt("ประเภททอง (1 = ทองแท่ง, 2 = ทองรูปพรรณ) : ");
        String goldType = (type == 1) ? "ทองแท่ง" : "ทองรูปพรรณ";
        double weight = readDouble("น้ำหนักทองคำ (บาททอง) : ");
        manager.addAsset(new GoldAsset(name, weight, goldType));
        System.out.println("เพิ่มรายการเรียบร้อยแล้ว");
    }

    static void addTrade() {
        System.out.print("ชื่อรายการ (เช่น สต๊อกสินค้าร้านค้า) : ");
        String name = sc.nextLine();
        double value = readDouble("มูลค่ารวมของสินค้า/ทรัพย์สินทางการค้า (บาท) : ");
        double debt = readDouble("หนี้สินที่เกี่ยวข้องกับกิจการ (บาท, ถ้าไม่มีใส่ 0) : ");
        manager.addAsset(new TradeAsset(name, value, debt));
        System.out.println("เพิ่มรายการเรียบร้อยแล้ว");
    }

    static void addAgriculture() {
        System.out.print("ชื่อรายการ (เช่น ข้าวเปลือก) : ");
        String name = sc.nextLine();
        double weight = readDouble("น้ำหนักผลผลิต (กิโลกรัม) : ");
        double price = readDouble("ราคาต่อกิโลกรัม (บาท) : ");
        int type = readInt("รูปแบบการให้น้ำ (1 = ใช้ต้นทุน/ชลประทาน , 2 = อาศัยน้ำฝนธรรมชาติ) : ");
        boolean irrigated = (type == 1);
        manager.addAsset(new AgricultureAsset(name, weight, price, irrigated));
        System.out.println("เพิ่มรายการเรียบร้อยแล้ว");
    }

    static void calculateAndShowTotal() {
        if (manager.getCount() == 0) {
            System.out.println("ยังไม่มีรายการทรัพย์สิน กรุณาเพิ่มรายการก่อน");
            return;
        }

        double totalValue = manager.calculateTotalValue();
        double totalZakat = manager.calculateTotalZakat();

        System.out.println("================ สรุปผลการคำนวณซะกาต ================");
        System.out.println("จำนวนรายการทรัพย์สินทั้งหมด   : " + manager.getCount() + " รายการ");
        System.out.println("จำนวนรายการที่ครบเกณฑ์นิศอบ  : " + manager.countEligibleAssets() + " รายการ");
        System.out.println("มูลค่าทรัพย์สินรวมทั้งหมด     : " + df.format(totalValue) + " บาท");
        System.out.println("ยอดซะกาตที่ต้องชำระทั้งหมด    : " + df.format(totalZakat) + " บาท");
        System.out.println("=====================================================");
    }

    // อ่านค่าจำนวนเต็มแบบปลอดภัย (กันผู้ใช้กรอกไม่ใช่ตัวเลข)
    static int readInt(String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextInt()) {
            System.out.println("กรุณากรอกตัวเลขจำนวนเต็มเท่านั้น");
            sc.next();
            System.out.print(prompt);
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    // อ่านค่าทศนิยมแบบปลอดภัย
    static double readDouble(String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextDouble()) {
            System.out.println("กรุณากรอกตัวเลขเท่านั้น");
            sc.next();
            System.out.print(prompt);
        }
        double val = sc.nextDouble();
        sc.nextLine();
        return val;
    }
}

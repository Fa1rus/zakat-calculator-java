// คลาสจัดการรายการทรัพย์สินทั้งหมดและคำนวณผลรวม
public class ZakatManager {

    private Asset[] assets;
    private int count;

    public ZakatManager(int capacity) {
        assets = new Asset[capacity];
        count = 0;
    }

    public boolean addAsset(Asset a) {
        if (count < assets.length) {
            assets[count] = a;
            count++;
            return true;
        }
        System.out.println("ไม่สามารถเพิ่มรายการได้ เนื่องจากรายการเต็มแล้ว");
        return false;
    }

    public int getCount() {
        return count;
    }

    public Asset[] getAssets() {
        return assets;
    }

    public boolean removeAsset(int index) {
        if (index >= 0 && index < count) {
            for (int i = index; i < count - 1; i++) {
                assets[i] = assets[i + 1];
            }
            assets[count - 1] = null;
            count--;
            return true;
        }
        return false;
    }

    public void clearAll() {
        for (int i = 0; i < count; i++) {
            assets[i] = null;
        }
        count = 0;
    }

    // แสดงรายการทรัพย์สินทั้งหมด (วนลูปเรียก displayInfo ของแต่ละคลาสลูก -> Polymorphism)
    public void showAllAssets() {
        if (count == 0) {
            System.out.println("ยังไม่มีรายการทรัพย์สินในระบบ");
            return;
        }
        for (int i = 0; i < count; i++) {
            System.out.println("รายการที่ " + (i + 1));
            assets[i].displayInfo();
        }
    }

    public double calculateTotalValue() {
        double total = 0;
        for (int i = 0; i < count; i++) {
            total += assets[i].getValue();
        }
        return total;
    }

    public double calculateTotalZakat() {
        double total = 0;
        for (int i = 0; i < count; i++) {
            total += assets[i].calculateZakat();
        }
        return total;
    }

    public int countEligibleAssets() {
        int c = 0;
        for (int i = 0; i < count; i++) {
            if (assets[i].isEligible()) {
                c++;
            }
        }
        return c;
    }
}

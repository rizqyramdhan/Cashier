
import java.util.Scanner;

public class tes {

    static Scanner input = new Scanner(System.in);

    static String nama;
    static int harga, jumlah;
    static int subtotal, total = 0;
    static int pilihan;

    public static void main(String[] args) {

        System.out.println("===== CASHIER SIMPLE =====");

        do {
            inputBarang();

            subtotal = hitungSubtotal();
            total += subtotal;

            System.out.print("\nTambah barang lagi? (1 = Ya / 0 = Tidak): ");
            pilihan = input.nextInt();
            input.nextLine();

        } while (pilihan == 1);

        cetakStruk();

        input.close();
    }

    // Fungsi input barang
    static void inputBarang() {

        System.out.print("\nNama Barang : ");
        nama = input.nextLine();

        System.out.print("Harga       : ");
        harga = input.nextInt();

        System.out.print("Jumlah      : ");
        jumlah = input.nextInt();
    }

    // Fungsi menghitung subtotal
    static int hitungSubtotal() {

        return harga * jumlah;
    }

    // Fungsi mencetak struk
    static void cetakStruk() {

        System.out.println("\n===== STRUK PEMBELIAN =====");

        System.out.println("Nama Barang : " + nama);
        System.out.println("Harga       : Rp" + harga);
        System.out.println("Jumlah      : " + jumlah);
        System.out.println("Subtotal    : Rp" + subtotal);

        System.out.println("---------------------------");
        System.out.println("TOTAL       : Rp" + total);

        System.out.println("===========================");
        System.out.println("Terima kasih!");
    }
}

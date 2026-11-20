import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Kumpulan fungsi sistem kasir (POS). Tanpa Scanner / System.out / JOptionPane,
 * jadi tinggal dipanggil dari GUI mana pun.
 *
 * Semua method yang gagal melempar KasirException; tampilkan e.getMessage() di GUI.
 * Semua uang memakai long (rupiah, tanpa desimal).
 */
public class Cashier {

    private final Map<String, Produk> produk = new LinkedHashMap<>();
    private final Map<String, ItemKeranjang> keranjang = new LinkedHashMap<>();
    private final List<Penjualan> riwayat = new ArrayList<>();

    private int pajakPersen = 11;      // PPN, bisa diubah lewat setPajakPersen
    private int diskonPersen = 0;      // diskon untuk transaksi berjalan
    private long diskonNominal = 0;    // diskon rupiah untuk transaksi berjalan
    private String kasirAktif = "Kasir";
    private int counterStruk = 0;

    // ===================== PENGATURAN =====================

    public void setKasir(String nama) {
        if (nama != null && !nama.trim().isEmpty()) kasirAktif = nama.trim();
    }

    public String getKasir() { return kasirAktif; }

    public void setPajakPersen(int persen) throws KasirException {
        if (persen < 0 || persen > 100) throw new KasirException("Pajak harus 0 - 100%.");
        pajakPersen = persen;
    }

    public int getPajakPersen() { return pajakPersen; }

    // ===================== PRODUK =====================

    public Produk tambahProduk(String kode, String nama, long harga, int stok) throws KasirException {
        String k = normalKode(kode);
        if (nama == null || nama.trim().isEmpty()) throw new KasirException("Nama produk tidak boleh kosong.");
        if (harga <= 0) throw new KasirException("Harga harus lebih dari 0.");
        if (stok < 0) throw new KasirException("Stok tidak boleh negatif.");
        if (produk.containsKey(k)) throw new KasirException("Kode produk sudah dipakai.");
        Produk p = new Produk(k, nama.trim(), harga, stok);
        produk.put(k, p);
        return p;
    }

    public void ubahProduk(String kode, String namaBaru, long hargaBaru) throws KasirException {
        Produk p = cariProduk(kode);
        if (namaBaru == null || namaBaru.trim().isEmpty()) throw new KasirException("Nama produk tidak boleh kosong.");
        if (hargaBaru <= 0) throw new KasirException("Harga harus lebih dari 0.");
        p.setNama(namaBaru.trim());
        p.setHarga(hargaBaru);
    }

    public void tambahStok(String kode, int jumlah) throws KasirException {
        if (jumlah <= 0) throw new KasirException("Jumlah stok harus lebih dari 0.");
        Produk p = cariProduk(kode);
        p.setStok(p.getStok() + jumlah);
    }

    public void hapusProduk(String kode) throws KasirException {
        Produk p = cariProduk(kode);
        produk.remove(p.getKode());
        keranjang.remove(p.getKode());
    }

    public Produk cariProduk(String kode) throws KasirException {
        Produk p = produk.get(normalKode(kode));
        if (p == null) throw new KasirException("Produk tidak ditemukan.");
        return p;
    }

    /** Cari berdasarkan potongan nama atau kode. */
    public List<Produk> cariNama(String kataKunci) {
        List<Produk> hasil = new ArrayList<>();
        String q = kataKunci == null ? "" : kataKunci.trim().toLowerCase();
        for (Produk p : produk.values()) {
            if (p.getNama().toLowerCase().contains(q) || p.getKode().toLowerCase().contains(q))
                hasil.add(p);
        }
        return hasil;
    }

    public List<Produk> daftarProduk() { return new ArrayList<>(produk.values()); }

    public List<Produk> stokMenipis(int batas) {
        List<Produk> hasil = new ArrayList<>();
        for (Produk p : produk.values()) if (p.getStok() <= batas) hasil.add(p);
        return hasil;
    }

    // ===================== KERANJANG =====================

    public void tambahKeKeranjang(String kode, int qty) throws KasirException {
        if (qty <= 0) throw new KasirException("Jumlah harus lebih dari 0.");
        Produk p = cariProduk(kode);
        ItemKeranjang item = keranjang.get(p.getKode());
        int total = qty + (item == null ? 0 : item.getQty());
        if (total > p.getStok())
            throw new KasirException("Stok " + p.getNama() + " tidak cukup (sisa " + p.getStok() + ").");
        if (item == null) keranjang.put(p.getKode(), new ItemKeranjang(p.getKode(), p.getNama(), p.getHarga(), qty));
        else item.setQty(total);
    }

    /** Ubah qty item di keranjang. qty 0 = hapus item. */
    public void ubahQty(String kode, int qty) throws KasirException {
        String k = normalKode(kode);
        ItemKeranjang item = keranjang.get(k);
        if (item == null) throw new KasirException("Item tidak ada di keranjang.");
        if (qty < 0) throw new KasirException("Jumlah tidak boleh negatif.");
        if (qty == 0) { keranjang.remove(k); return; }
        Produk p = cariProduk(k);
        if (qty > p.getStok())
            throw new KasirException("Stok " + p.getNama() + " tidak cukup (sisa " + p.getStok() + ").");
        item.setQty(qty);
    }

    public void hapusDariKeranjang(String kode) throws KasirException {
        if (keranjang.remove(normalKode(kode)) == null)
            throw new KasirException("Item tidak ada di keranjang.");
    }

    public void kosongkanKeranjang() {
        keranjang.clear();
        diskonPersen = 0;
        diskonNominal = 0;
    }

    public List<ItemKeranjang> getKeranjang() { return new ArrayList<>(keranjang.values()); }

    public boolean keranjangKosong() { return keranjang.isEmpty(); }

    // ===================== DISKON, PAJAK, TOTAL =====================

    public void setDiskonPersen(int persen) throws KasirException {
        if (persen < 0 || persen > 100) throw new KasirException("Diskon harus 0 - 100%.");
        diskonPersen = persen;
    }

    public void setDiskonNominal(long nominal) throws KasirException {
        if (nominal < 0) throw new KasirException("Diskon tidak boleh negatif.");
        diskonNominal = nominal;
    }

    public long hitungSubtotal() {
        long s = 0;
        for (ItemKeranjang i : keranjang.values()) s += i.getSubtotal();
        return s;
    }

    public long hitungDiskon() {
        long sub = hitungSubtotal();
        long d = Math.round(sub * diskonPersen / 100.0) + diskonNominal;
        return Math.min(d, sub);
    }

    public long hitungPajak() {
        return Math.round((hitungSubtotal() - hitungDiskon()) * pajakPersen / 100.0);
    }

    public long hitungTotal() {
        return hitungSubtotal() - hitungDiskon() + hitungPajak();
    }

    /** Kembalian sementara untuk ditampilkan saat kasir mengetik uang bayar (bisa negatif = kurang). */
    public long hitungKembalian(long uangBayar) {
        return uangBayar - hitungTotal();
    }

    // ===================== PEMBAYARAN =====================

    /** Selesaikan transaksi: kurangi stok, simpan riwayat, kosongkan keranjang. */
    public Penjualan bayar(long uangBayar) throws KasirException {
        if (keranjang.isEmpty()) throw new KasirException("Keranjang masih kosong.");
        long total = hitungTotal();
        if (uangBayar < total)
            throw new KasirException("Uang kurang " + rupiah(total - uangBayar) + ".");

        // cek stok ulang sebelum mengurangi
        for (ItemKeranjang i : keranjang.values()) {
            Produk p = produk.get(i.getKode());
            if (p == null || p.getStok() < i.getQty())
                throw new KasirException("Stok " + i.getNama() + " tidak mencukupi.");
        }
        for (ItemKeranjang i : keranjang.values()) {
            Produk p = produk.get(i.getKode());
            p.setStok(p.getStok() - i.getQty());
        }

        String noStruk = String.format("TRX%05d", ++counterStruk);
        Penjualan pj = new Penjualan(noStruk, kasirAktif, new ArrayList<>(keranjang.values()),
                hitungSubtotal(), hitungDiskon(), hitungPajak(), total, uangBayar);
        riwayat.add(pj);
        kosongkanKeranjang();
        return pj;
    }

    // ===================== LAPORAN & STRUK =====================

    public List<Penjualan> riwayatPenjualan() { return new ArrayList<>(riwayat); }

    public long totalPenjualan(LocalDate tanggal) {
        long t = 0;
        for (Penjualan p : riwayat)
            if (p.getWaktu().toLocalDate().equals(tanggal)) t += p.getTotal();
        return t;
    }

    public long totalPenjualanHariIni() { return totalPenjualan(LocalDate.now()); }

    public Penjualan cariStruk(String noStruk) throws KasirException {
        for (Penjualan p : riwayat)
            if (p.getNoStruk().equalsIgnoreCase(noStruk == null ? "" : noStruk.trim())) return p;
        throw new KasirException("Struk tidak ditemukan.");
    }

    /** Teks struk siap dicetak / ditaruh di JTextArea (pakai font monospaced). */
    public String buatStruk(Penjualan p) {
        StringBuilder sb = new StringBuilder();
        String garis = "--------------------------------\n";
        sb.append("          TOKO ANDA\n").append(garis);
        sb.append("No    : ").append(p.getNoStruk()).append('\n');
        sb.append("Waktu : ").append(p.getWaktuFormat()).append('\n');
        sb.append("Kasir : ").append(p.getKasir()).append('\n').append(garis);
        for (ItemKeranjang i : p.getItems()) {
            sb.append(i.getNama()).append('\n');
            sb.append(String.format("  %d x %s%s%n", i.getQty(), rupiah(i.getHarga()),
                    spasi(rupiah(i.getHarga()), i.getQty(), rupiah(i.getSubtotal())) + rupiah(i.getSubtotal())));
        }
        sb.append(garis);
        sb.append(baris("Subtotal", rupiah(p.getSubtotal())));
        if (p.getDiskon() > 0) sb.append(baris("Diskon", "-" + rupiah(p.getDiskon())));
        sb.append(baris("Pajak (" + pajakPersen + "%)", rupiah(p.getPajak())));
        sb.append(baris("TOTAL", rupiah(p.getTotal())));
        sb.append(baris("Bayar", rupiah(p.getBayar())));
        sb.append(baris("Kembali", rupiah(p.getKembalian())));
        sb.append(garis).append("      Terima kasih!\n");
        return sb.toString();
    }

    // ===================== TABEL UNTUK GUI =====================

    public Object[][] produkTabel() {
        return tabel(daftarProduk().stream().map(Produk::toRow).toArray(Object[]::new));
    }

    public Object[][] keranjangTabel() {
        return tabel(getKeranjang().stream().map(ItemKeranjang::toRow).toArray(Object[]::new));
    }

    public Object[][] riwayatTabel() {
        return tabel(riwayatPenjualan().stream().map(Penjualan::toRow).toArray(Object[]::new));
    }

    private static Object[][] tabel(Object[] rows) {
        Object[][] data = new Object[rows.length][];
        for (int i = 0; i < rows.length; i++) data[i] = (Object[]) rows[i];
        return data;
    }

    // ===================== HELPER =====================

    /** 15000 -> "Rp15.000" */
    public static String rupiah(long nilai) {
        return "Rp" + String.format("%,d", nilai).replace(',', '.');
    }

    /** Ubah teks field GUI ("15.000" / "15000") menjadi angka. */
    public static long parseAngka(String teks) throws KasirException {
        try {
            return Long.parseLong(teks.trim().replace(".", "").replace(",", ""));
        } catch (Exception e) {
            throw new KasirException("Angka tidak valid.");
        }
    }

    private static String normalKode(String kode) throws KasirException {
        if (kode == null || kode.trim().isEmpty()) throw new KasirException("Kode produk tidak boleh kosong.");
        return kode.trim().toUpperCase();
    }

    private static String baris(String kiri, String kanan) {
        int lebar = 32;
        int sp = Math.max(1, lebar - kiri.length() - kanan.length());
        return kiri + " ".repeat(sp) + kanan + "\n";
    }

    private static String spasi(String harga, int qty, String subtotal) {
        int panjang = ("  " + qty + " x " + harga).length();
        return " ".repeat(Math.max(1, 32 - panjang - subtotal.length()));
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 560e583e0aafb3cc063d9a10dd1198bd92a7c8e0

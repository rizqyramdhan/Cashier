import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class Penjualan {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final String noStruk;
    private final LocalDateTime waktu = LocalDateTime.now();
    private final String kasir;
    private final List<ItemKeranjang> items;
    private final long subtotal;
    private final long diskon;
    private final long pajak;
    private final long total;
    private final long bayar;
    private final long kembalian;

    Penjualan(String noStruk, String kasir, List<ItemKeranjang> items,
              long subtotal, long diskon, long pajak, long total, long bayar) {
        this.noStruk = noStruk;
        this.kasir = kasir;
        this.items = items;
        this.subtotal = subtotal;
        this.diskon = diskon;
        this.pajak = pajak;
        this.total = total;
        this.bayar = bayar;
        this.kembalian = bayar - total;
    }

    public String getNoStruk() { return noStruk; }
    public LocalDateTime getWaktu() { return waktu; }
    public String getWaktuFormat() { return waktu.format(FMT); }
    public String getKasir() { return kasir; }
    public List<ItemKeranjang> getItems() { return Collections.unmodifiableList(items); }
    public long getSubtotal() { return subtotal; }
    public long getDiskon() { return diskon; }
    public long getPajak() { return pajak; }
    public long getTotal() { return total; }
    public long getBayar() { return bayar; }
    public long getKembalian() { return kembalian; }

    public int getJumlahItem() {
        int n = 0;
        for (ItemKeranjang i : items) n += i.getQty();
        return n;
    }

    /** Satu baris untuk JTable: {noStruk, waktu, kasir, jumlahItem, total}. */
    public Object[] toRow() {
        return new Object[]{noStruk, getWaktuFormat(), kasir, getJumlahItem(), total};
    }
}

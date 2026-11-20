public class ItemKeranjang {
    private final String kode;
    private final String nama;
    private final long harga;   // harga saat dimasukkan ke keranjang
    private int qty;

    ItemKeranjang(String kode, String nama, long harga, int qty) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.qty = qty;
    }

    public String getKode() { return kode; }
    public String getNama() { return nama; }
    public long getHarga() { return harga; }
    public int getQty() { return qty; }
    public long getSubtotal() { return harga * qty; }

    void setQty(int qty) { this.qty = qty; }

    /** Satu baris untuk JTable: {kode, nama, harga, qty, subtotal}. */
    public Object[] toRow() {
        return new Object[]{kode, nama, harga, qty, getSubtotal()};
    }
}

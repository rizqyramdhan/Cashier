public class Produk {
    private final String kode;
    private String nama;
    private long harga;
    private int stok;

    Produk(String kode, String nama, long harga, int stok) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }

    public String getKode() { return kode; }
    public String getNama() { return nama; }
    public long getHarga() { return harga; }
    public int getStok() { return stok; }

    void setNama(String nama) { this.nama = nama; }
    void setHarga(long harga) { this.harga = harga; }
    void setStok(int stok) { this.stok = stok; }

    /** Satu baris untuk JTable: {kode, nama, harga, stok}. */
    public Object[] toRow() {
        return new Object[]{kode, nama, harga, stok};
    }
}

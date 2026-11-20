/**
 * Dilempar saat operasi kasir gagal (stok kurang, uang kurang, kode tidak ada, dll).
 * Pesan siap ditampilkan langsung ke pengguna lewat getMessage().
 */
public class KasirException extends Exception {
    public KasirException(String pesan) {
        super(pesan);
    }
}

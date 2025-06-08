# 📱 EventApp – Aplikasi Event Modern dengan Kotlin

Aplikasi Android yang menampilkan daftar event terkini dan terlengkap. Dibangun dengan arsitektur modern, fitur lengkap, dan UI yang ramah pengguna. Cocok sebagai showcase untuk posisi **Mobile Developer (Android - Kotlin)**.

---

## 📸 Cuplikan Tampilan

<p align="center">
  <img src="https://github.com/user-attachments/assets/8d9c365f-a6bc-47a1-9353-9d55093f1658" width="30%" />
  <img src="https://github.com/user-attachments/assets/5ef55bf8-52ab-4389-9211-74f0977685b3" width="30%" />
  <img src="https://github.com/user-attachments/assets/a3ee6a12-deed-4479-b177-3dd108887ff6" width="30%" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/fec3a5a7-488a-4652-bc8f-2a96b088abbe" width="30%" />
  <img src="https://github.com/user-attachments/assets/8da93911-79d9-4878-8df1-4164e7af8b84" width="30%" />
</p>

---

## ✨ Fitur Unggulan

### 🧭 Navigasi & Home
- Bottom Navigation dengan 3 menu utama: Home, Aktif, Selesai
- Halaman **Home dinamis**:
  - Carousel Event Aktif
  - List horizontal Event Selesai
- Responsif terhadap perubahan orientasi (rotation-safe)

### 📅 List & Detail Event
- Menampilkan list event dari API:
  - Gambar, nama acara
- Halaman detail event menampilkan:
  - Gambar
  - Nama acara
  - Penyelenggara acara
  - Waktu acara
  - Sisa kuota
  - Deskripsi acara
  - Tombol untuk membuka link acara

### 🔍 Fitur Pencarian
- Pencarian real-time menggunakan **SearchView**
- Query ke endpoint API secara langsung
- Hasil diperbarui secara dinamis saat mengetik

### ❤️ Fitur Favorit
- Tambah dan hapus event favorit
- Data disimpan secara lokal menggunakan Room Database
- Halaman khusus menampilkan daftar favorit
- Bisa membuka detail dari event favorit

### 🌙 Tema Gelap & Terang
- Mendukung pengaturan tema (light/dark)
- Penyimpanan preferensi tema dengan **DataStore**
- Tema tetap tersimpan walau aplikasi ditutup
- Komponen UI tetap terbaca jelas di kedua mode

### ⏰ Notifikasi Harian
- Notifikasi event aktif terdekat setiap hari
- Menggunakan **WorkManager** (interval 1 hari)
- Menu pengaturan untuk mengaktifkan/mematikan notifikasi
- Hanya tampil jika pengaturan diaktifkan

---

## 🛠️ Teknologi yang Digunakan

| Kategori             | Teknologi / Tools                                  |
|----------------------|----------------------------------------------------|
| Bahasa Pemrograman   | Kotlin                                             |
| Arsitektur           | MVVM (Model-View-ViewModel)                        |
| Networking           | Retrofit, Gson, Coroutine                          |
| Local Storage        | Room Database                                      |
| State Management     | LiveData, ViewModel                                |
| Dependency Mgmt      | Manual Dependency Injection (siap migrasi ke Hilt) |
| Persistent Storage   | DataStore Preferences                              |
| Background Task      | WorkManager                                        |
| UI                   | Material Components, RecyclerView, ViewPager2, SearchView |
| Navigasi             | Fragment + BottomNavigationView                    |
| UX Improvement       | Shimmer Loading, Error Message, Snackbar           |
| Orientasi Support    | ViewModel survives rotation                        |

---

## 🚀 Cara Menjalankan Proyek Ini

1. Clone repository ini:
   ```bash
   git clone https://github.com/taufiqurrohman132/Apk-DicodingEvent.git

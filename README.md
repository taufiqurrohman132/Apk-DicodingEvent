# Dicoding Event App

Aplikasi Android yang menyajikan berbagai event dari API Dicoding dengan fitur lengkap mulai dari navigasi, favorit, pencarian, tema, hingga pengingat harian. Dibangun dengan pendekatan arsitektur modern (MVVM + Repository + Dependency Injection) menggunakan Kotlin.

---

## Cuplikan Layar

| Fitur | Cuplikan |
|------|----------|
| Home | ![home](./screenshots/home.png) |

---

## Daftar Fitur

- Navigasi dengan Bottom Navigation (Event Aktif, Event Selesai, Favorit)
- Menampilkan daftar event dari API (gambar, nama, penyelenggara, waktu, kuota, deskripsi, link)
- Halaman detail event lengkap
- Fitur tambah/hapus favorit dengan penyimpanan lokal (Room)
- Halaman daftar event favorit
- Fitur ganti tema terang/gelap (dengan DataStore)
- Fitur pencarian event berdasarkan kata kunci
- Halaman Home dengan carousel event aktif dan selesai (opsional)
- Notifikasi pengingat harian event terdekat (opsional, dengan WorkManager)
- Arsitektur MVVM + Repository + ViewModel + LiveData
- Dukungan rotasi layar tanpa reload data
- Indikator loading saat fetch API
- Error handling saat koneksi gagal atau data kosong
- Peringatan kode (warning) minimal saat diinspeksi (kurang dari 10)
---

## Teknologi & Arsitektur

| Komponen | Teknologi |
|---------|-----------|
| Bahasa | Kotlin |
| Arsitektur | MVVM |
| UI | XML + Material 3 |
| API | Retrofit |
| Database | Room |
| Theme Storage | DataStore (Preferences) |
| Dependency Injection | Manual |
| Background Task | WorkManager |
| Lifecycle | ViewModel + LiveData |
| Image Loader | Glide |
| Navigasi | Navigation Component |

---

## Kualitas dan Error Handling

- Indikator loading di semua halaman API
- Menangani kondisi tanpa internet atau data gagal ditampilkan
- Menampilkan pesan error yang sesuai
- Data tetap tersedia saat rotasi layar
- Kurang dari 10 warning pada Code → Inspect Code

---

## Cara Menjalankan

1. Clone repo:
 ```bash
 git clone https://github.com/username/event-app.git

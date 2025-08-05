# Dicoding Event App

Aplikasi Android yang menyajikan berbagai event dari API Dicoding dengan fitur lengkap mulai dari navigasi, favorit, pencarian, tema, hingga pengingat harian. Dibangun dengan pendekatan arsitektur modern (MVVM + Repository + Dependency Injection) menggunakan Kotlin.

---

## Cuplikan Layar

| Fitur | Cuplikan |
|------|----------|
| Home | ![home](./screenshots/home.png) |

---

## Daftar Fitur

<table>
  <tr>
    <td valign="top" width="50%">
      <ul>
        <li>Bottom Navigation (Aktif, Selesai, Favorit)</li>
        <li>List event dari API</li>
        <li>Halaman detail event</li>
        <li>Tambah/hapus favorit (Room)</li>
        <li>Halaman daftar favorit</li>
        <li>Tema terang/gelap (DataStore)</li>
        <li>Pencarian event</li>
      </ul>
    </td>
    <td valign="top" width="50%">
      <ul>
        <li>Home dengan carousel event (opsional)</li>
        <li>Notifikasi harian event terdekat (opsional)</li>
        <li>Arsitektur MVVM + ViewModel</li>
        <li>Repository Pattern</li>
        <li>Data tetap saat rotasi layar</li>
        <li>Indikator loading API</li>
        <li>Error handling koneksi/data</li>
        <li>Inspect Code warning < 10</li>
      </ul>
    </td>
  </tr>
</table>
         
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

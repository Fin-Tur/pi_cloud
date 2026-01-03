# Pi Cloud

A lightweight, modern **cloud storage system** with server-side **encryption** and **compression**, optimized for devices like the **Raspberry Pi**.  
The file processing is powered by native **C++ algorithms from the FileInSight project**, integrated as a DLL for maximum efficiency and data security.

---

## Features

- **User System** User completely own files, and can restrict acces via other Users
- **File uploads & downloads** via a clean, modern web interface  
- **Server-side encryption & compression** using FileInSight algorithms (C++ -> DLL via JNA)  
- **File management** – upload, download, delete, view metadata  
- **Smart file handling** with MIME type detection and custom icons  
- **Responsive UI** – pure HTML/CSS/JavaScript (no framework needed)  
- **Spring Boot + JPA backend** for persistent file management  

---

---

## Tech Stack

| Component | Technology |
|------------|-------------|
| Backend | Spring Boot (REST, JPA) |
| Frontend | HTML5, CSS3, Vanilla JS |
| Database | H2 / SQLite / PostgreSQL (configurable) |
| Native Module | FileInSight C++ DLL (encryption & compression) |
| Bridge | Java Native Access (JNA) |
| Target Platform | Raspberry Pi / Linux / Windows |

---

## Server-Side Encryption & Compression

- **AES-based file encryption**
- **FileInSight compression (LZ77/TLSH-inspired)**
- **On-the-fly processing** Encrypted and Compressed files will be downloaded via temporary link, so serverside files will stay encrypted the whole time
- **Byte-Stream Security Check** Files will be checked before adding them to disk
- **Shannon Entropy** Files with lower entropy then a treshhold will be compressed while uploading
- **Cross-platform compatibility** (Requires FileInSight Lib-Build)

**Benefits:**  
- No dependency on Java's crypto libraries  
- Higher performance via native execution  
- Secure key management outside the JVM  
- Configurable Settings: File size, Storage path, forbidden types, key hashing-iterations (encryption), Compression level 

---

---

## Installation & Setup

### 🔹 Requirements

- Java 17+  
- Maven or Gradle  
- (Optional) Node.js / Vite for frontend development  
- Compiled `FileInSight.dll` (or `.so` on Linux)  
- Raspberry Pi OS / Linux x64  
---

## Frontend Features

- Dynamic file listing using 
- File preview before upload  
- MIME-type-based file icons  
- Responsive design with dark mode support  
- Smooth button animations for uploads/downloads  

---

## Deployment (Raspberry Pi)

1. Copy `libFISext.so` to `/opt/pi-cloud/linux-aarch64/`
2. Edit application.properties and copy to '/opt/pi-cloud' 
3. Copy .jar to /opt/pi-cloud
4. Run `pi-cloud.jar`  
5. Configure autostart using `systemd`  

---

## License

MIT License – 2025 © Fin-Tur

## Screenshots 

**Login**

<img width="407" height="564" alt="Screenshot 2025-11-16 152217" src="https://github.com/user-attachments/assets/ee629249-fcd6-4716-84fd-0984498fdc51" />

**Uploading Files**
<img width="1685" height="1086" alt="uploading" src="https://github.com/user-attachments/assets/976bb43b-1eb6-44c2-aa02-00b446a9afe3" />

**Actions**

<img width="459" height="437" alt="Screenshot 2025-11-16 152103" src="https://github.com/user-attachments/assets/9a2ce1ec-e10f-4c22-9234-142886337a2c" />

**Encryption**

<img width="458" height="356" alt="encryption" src="https://github.com/user-attachments/assets/3792ded1-4674-4010-b1c9-8cbe4cccfa7f" />

**Directory**

<img width="429" height="438" alt="Screenshot 2025-11-16 152144" src="https://github.com/user-attachments/assets/8a8524da-6be6-406e-a185-0a54c51af898" />






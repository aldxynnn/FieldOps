# FieldOps Backend

REST API untuk aplikasi FieldOps.

## Requirement

- Node.js 22.5+

Backend memakai SQLite melalui `node:sqlite`, sehingga tidak perlu PostgreSQL/MySQL untuk menjalankan versi portfolio ini.

## Jalankan

PowerShell:

```powershell
cd backend
$env:JWT_SECRET="ganti-dengan-secret-random-yang-panjang"
$env:ADMIN_PASSWORD="ganti-password-admin"
npm start
```

Server: `http://localhost:8080`

Health check: `http://localhost:8080/api/health`

Android Emulator: gunakan base URL `http://10.0.2.2:8080/api/`

Physical device: gunakan alamat IP komputer pada jaringan yang sama, misalnya `http://192.168.1.10:8080/api/`.

## Default development login

- Email: `admin@fieldops.local`
- Password: `ChangeMe123!`

Ganti password tersebut sebelum penggunaan nyata.

## Endpoint utama

- `POST /api/auth/login`
- `GET /api/me`
- `GET /api/work-orders`
- `GET /api/work-orders/:id`
- `PATCH /api/work-orders/:id/status`
- `GET /api/customers`
- `GET /api/customers/:id`
- `GET /api/activity-history`
- `GET /api/notifications`
- `PATCH /api/notifications/:id/read`
- `POST /api/work-orders/:id/notes`
- `GET /api/work-orders/:id/notes`
- `POST /api/work-orders/:id/location`
- `GET /api/work-orders/:id/location`

Database otomatis dibuat di `backend/data/fieldops.sqlite`.

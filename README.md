# Time Attendance Project

## 1. Project Structure
```
time-attendance-project/
├── backend/          # Spring Boot backend
├── frontend/         # React frontend
├── .gitignore
└── README.md
```

---

## 2. Prerequisites

### Required Software
- **Java JDK 17+**
- **Maven 3.9+**
- **Node.js LTS (v18+)**
- **Git**
- **VS Code** (recommended)
- **MySQL Workbench** (to create database)

---

## 3. Database Setup (MySQL)

เพื่อนต้องสร้าง database เองเหมือนคุณ:

1. เปิด MySQL Workbench → Connect to local MySQL server
2. สร้าง database ใหม่:
```sql
CREATE DATABASE time_attendance;
```
3. สร้าง user ใหม่ (optional):
```sql
CREATE USER 'time_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON time_attendance.* TO 'time_user'@'localhost';
FLUSH PRIVILEGES;
```
4. อัปเดต `backend/src/main/resources/application.properties` หรือ `application.yml` ให้ตรงกับ database ของเพื่อน:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/time_attendance?useSSL=false&serverTimezone=UTC
spring.datasource.username=time_user
spring.datasource.password=password123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 4. Setup Project

### 4.1 Clone repository
```bash
git clone https://github.com/USERNAME/time-attendance-project.git
cd time-attendance-project
```

### 4.2 Install dependencies

#### Backend
```bash
cd backend
mvn clean install
```

#### Frontend
```bash
cd frontend
npm install
```

---

## 5. Running the Project

### 5.1 Development Mode (frontend + backend separate)
```bash
# Terminal 1: run backend
cd backend
mvn spring-boot:run

# Terminal 2: run frontend
cd frontend
npm start
```
- Backend: http://localhost:8080
- Frontend: http://localhost:3000

---

### 5.2 Production Mode (combine frontend + backend)
```bash
# Build frontend
cd frontend
npm run build

# Copy build folder to backend static
cp -r build/* ../backend/src/main/resources/static/

# Build backend jar
cd ../backend
mvn clean package

# Run jar
java -jar target/backend-0.0.1-SNAPSHOT.jar
```
- เปิด http://localhost:8080 → หน้า React + backend ทำงานพร้อมกัน

---

## 6. Notes
- Frontend dev server ใช้ `proxy` ใน `frontend/package.json` → เรียก API backend อัตโนมัติ
- ถ้าเจอ CORS → ตรวจสอบ backend CorsConfig
- หาก Maven / Java / Node ยังใช้ไม่ได้ → ตรวจสอบ Path / Environment Variables

---

## 7. Optional
- สามารถใช้ VS Code multi-root workspace เปิดทั้ง backend + frontend พร้อมกัน
- อยากให้ backend build frontend อัตโนมัติ → ใช้ `frontend-maven-plugin` ใน `backend/pom.xml`


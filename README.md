# CampusCare+ – Smart Campus Complaint & Grievance Management System
### With Student Credit and Reward Mechanism
**Built with Core Java (Java 21 LTS), Java Swing, and JDBC/MySQL**

---

## 🌟 Project Overview
**CampusCare+** is an innovative, enterprise-grade college campus complaint and grievance resolution platform engineered purely using **Core Java**, **Java Swing**, and **JDBC/MySQL**. It bridges the communication gap between students, campus administrators, and maintenance departments while introducing a non-monetary **CampusCare Credit Wallet** to gamify and reward civic participation on campus.

The system addresses real collegiate challenges:
- Classroom & Laboratory maintenance (projectors, lab equipment, desks)
- Hostel and residential living issues (washroom leakage, plumbing)
- Central Library facilities & reading room resources
- Canteen food quality and cafeteria hygiene
- Transportation and campus bus schedules
- IT Infrastructure, Wi-Fi connectivity, and network drops
- Water and electrical emergency hazards (sparks, exposed wiring)
- Confidential reporting of ragging, bullying, or harassment

---

## 🚀 Key Innovations

1. **Smart Complaint Priority**: Natural language keyword heuristics automatically scan descriptions for hazard keywords ("spark", "fire", "harassment", "leakage") and dynamically compute urgency (`NORMAL`, `HIGH`, `URGENT`) with SLA targets (24 hours to 7 days).
2. **Duplicate Complaint Detection**: Computes token overlap (Jaccard Index) and Levenshtein string edit distance across unresolved tickets to prevent spam and duplicate credit exploitation.
3. **Automated Escalation Daemon**: A background multithreaded daemon thread continuously monitors SLA deadlines; overdue complaints are automatically elevated to `ESCALATED`, assigned top priority, and flagged to the Chief Grievance Officer.
4. **Anonymous & Confidential Reporting**: For sensitive issues (anti-ragging), student identities are masked to `"Anonymous Student"` across public and staff views while preserving audit integrity.
5. **Campus Heatmap**: Java Stream-based aggregation calculates complaint densities across campus landmarks and displays both graphical and text-based intensity bars (`[████████░░░░] 65%`).
6. **CampusCare Credit Wallet**: Students earn participation credits (+5 for valid complaints, +3 for details, +2 for feedback, +5 bonus for safety hazards). Features an anti-abuse mechanism and a live campus-wide honor leaderboard!
7. **Transparent Audit Timeline**: Every state transition (`SUBMITTED` -> `ASSIGNED` -> `IN_PROGRESS` -> `RESOLVED` -> `CLOSED`) logs timestamp, actor, and remarks for complete transparency.

---

## 🔐 Default Demo Accounts

The login interface features quick 1-click preset accounts for testing and demonstration:

| Portal | Role | Username / ID | Password | Notes |
| :--- | :--- | :--- | :--- | :--- |
| **Student** | Student | `22CS0101` or `aarav.mehta@student.campus.edu` | `password123` | Aarav Mehta (CSE, Sem 5) |
| **Student** | Student | `22EC0204` or `diya.nair@student.campus.edu` | `password123` | Diya Nair (ECE, Sem 5) |
| **Admin** | Campus Administrator | `admin` or `arvind.sharma@campus.edu` | `admin123` | Prof. Arvind Sharma (Chief Grievance Officer) |
| **Staff** | Electrical Maintenance | `EMP-ELEC-01` or `suresh.elec@campus.edu` | `staff123` | Suresh Patel (Electrical Staff) |
| **Staff** | Hostel & Estate | `EMP-HOSTEL-01` or `ramesh.hostel@campus.edu` | `staff123` | Ramesh Kumar (Hostel Staff) |
| **Staff** | IT & Wi-Fi Support | `EMP-IT-01` or `pooja.it@campus.edu` | `staff123` | Pooja Verma (IT Helpdesk) |

*Students can also register brand-new accounts directly from the login page!*

---

## 🛠️ System Requirements & Prerequisites

- **Java Development Kit (JDK)**: Java 17+ or Java 21 LTS (Oracle OpenJDK or Eclipse Temurin)
- **Database (Optional / Dual Mode)**: MySQL 8.x
  - *Note: CampusCare+ contains a Dual-Mode DAO Architecture. If MySQL is not running, it automatically operates in resilient In-Memory Persistent Mode with pre-populated realistic campus seed data!*
- **Operating System**: macOS, Windows 10/11, or Linux
- **IDE**: IntelliJ IDEA, Eclipse, or Visual Studio Code

---

## 📦 Project Structure

```
CampusCarePlus/
├── pom.xml                               # Maven project configuration (Java 21, MySQL JDBC)
├── build_and_run.sh                      # Shell script: compile sources & run automated tests
├── run_gui.sh                            # Shell script: launch Java Swing graphical application
├── run_tests.sh                          # Shell script: fast automated test suite runner
├── README.md                             # Quickstart & operational guide
├── sql/
│   ├── schema.sql                        # Complete MySQL DDL (10 relational tables)
│   └── seed_data.sql                     # Realistic sample seed data
├── docs/
│   ├── ARCHITECTURE.md                   # Layered architecture, diagrams, and data flows
│   ├── CORE_JAVA_MAPPING.md              # Mapping of all 18 Core Java concepts to code
│   ├── VIVA_QUESTIONS_AND_ANSWERS.md     # 25+ comprehensive exam & viva Q&A
│   └── PROJECT_REPORT.md                 # Complete academic project report
├── src/
│   └── com/campuscare/
│       ├── Main.java                     # Application entry point
│       ├── model/                        # Domain models (User, Student, Admin, Staff, Complaint, etc.)
│       │   └── enums/                    # Enums (UserRole, ComplaintCategory, Status, Priority, etc.)
│       ├── dao/                          # Generic DAO interfaces and Factory
│       │   ├── jdbc/                     # Native JDBC implementations (PreparedStatement)
│       │   └── memory/                   # In-memory thread-safe store & DAO implementations
│       ├── service/                      # Business logic, smart priority, duplicate detection, daemon
│       ├── controller/                   # Front controller (AppController)
│       ├── exception/                    # Custom exception hierarchy
│       ├── util/                         # Security, TextSimilarity, IdGenerator, UITheme, DateTime
│       └── view/                         # Swing interfaces (Login, Student, Admin, Staff, components)
└── test/
    ├── TestRunner.java                   # Automated regression test harness
    ├── ModelTestSuite.java               # OOP & Model unit tests
    ├── DuplicateDetectionTest.java       # Text similarity & duplicate algorithm tests
    ├── SmartPriorityTest.java            # Keyword heuristics & SLA tests
    ├── CreditWalletTest.java             # Wallet rules & anti-abuse tests
    └── ServiceTestSuite.java             # End-to-end integration lifecycle test
```

---

## ⚡ Execution Instructions

### 1. Build and Run Automated Tests
Open your terminal, navigate to the `CampusCarePlus` directory, and run:
```bash
chmod +x build_and_run.sh run_gui.sh run_tests.sh
./build_and_run.sh
```

### 2. Launch the Graphical User Interface (Swing)
```bash
./run_gui.sh
```
Or directly via Java:
```bash
java -cp bin com.campuscare.Main
```

### 3. Setting Up MySQL (Optional)
If you wish to use an active MySQL instance:
1. Start your local MySQL service on port `3306`.
2. Execute the database scripts:
   ```bash
   mysql -u root -p < sql/schema.sql
   mysql -u root -p < sql/seed_data.sql
   ```
3. Run the application with the MySQL JDBC Connector JAR on the classpath.

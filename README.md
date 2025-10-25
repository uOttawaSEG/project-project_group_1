SEG2105 project_group_1

Members:
- Bingqian Yang
- Nour Boukhtouta
- Dragos Daiciulescu
- Jennate Ryadi
- Darren Qu
- Zachary Djerdjouri

Admin login credentials :
Email: admin@otams.ca
Password: Admin#123



# OTAMS – Deliverable 2 Part 1
## Database Setup & Integration Summary

### New Files
| File | Purpose |
|------|----------|
| `model/RequestStatus.java` | Enum defining registration request status (`PENDING`, `APPROVED`, `REJECTED`). |
| `model/UserRole.java` | Enum defining user roles (`ADMIN`, `STUDENT`, `TUTOR`). |
| `model/RegistrationRequestEntity.java` | Entity for registration requests stored before admin approval. |
| `model/RegistrationRequestListItem.java` | Lightweight DTO for displaying requests in the Admin UI. |
| `data/RegistrationRequestDao.java` | DAO interface for inserting, querying, and updating registration requests. |
| `androidTest/DbPart1EndToEndTest.java` | End-to-end test verifying database insert/update/query functions. |

---

### Modified Files
| File | Update Summary |
|------|----------------|
| `util/Converters.java` | Added type converters for `UserRole` and `RequestStatus` enums. |
| `data/AppDatabase.java` | Added `RegistrationRequestEntity`; upgraded DB version to v2; added `RegistrationRequestDao`; implemented migration. |
| `repository/UserRepository.java` | Added methods to create registration requests, query their status, and update status (for admin approval). |
| `App.java` | Registered Room migration with `.addMigrations(AppDatabase.MIGRATION_1_2)`. |

---

### Test Summary
**Test Class:** `DbPart1EndToEndTest`  
**Validates:**
- Insertion of student/tutor registration requests
- Status updates to APPROVED / REJECTED
- Correct query results from DAO

**Expected Logcat Output (tag = `OTAMS-DBTEST`):**
package com.example.otams;

import com.example.otams.data.*;
import com.example.otams.model.*;
import com.example.otams.repository.UserRepository;
import com.example.otams.util.PasswordHasher;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Local unit tests for UserRepository 
 */
public class UserRepositoryTest {

    

    static class FakeUserDao implements UserDao {
        Map<String, UserEntity> table = new HashMap<>();

        @Override
        public void insert(UserEntity user) {
            table.put(user.email, user);
        }

        @Override
        public UserEntity findByEmail(String email) {
            return table.get(email);
        }

        @Override
        public boolean emailExists(String email) {
            return table.containsKey(email);
        }

        @Override
        public List<UserEntity> getAllUsers() {
            return new ArrayList<>(table.values());
        }
    }

    static class FakeRegistrationRequestDao implements RegistrationRequestDao {
        Map<String, RegistrationRequestEntity> byEmail = new HashMap<>();
        Map<Long, RegistrationRequestEntity> byId = new HashMap<>();
        long nextId = 1;

        @Override
        public long insert(RegistrationRequestEntity req) {
            req.id = nextId++;
            byEmail.put(req.email, req);
            byId.put(req.id, req);
            return req.id;
        }

        @Override
        public RegistrationRequestEntity findByEmail(String email) {
            return byEmail.get(email);
        }

        @Override
        public RequestStatus getStatusByEmail(String email) {
            RegistrationRequestEntity r = byEmail.get(email);
            return (r == null ? null : r.status);
        }

        @Override
        public void updateStatus(long id, RequestStatus newStatus, String adminId, long ts) {
            RegistrationRequestEntity r = byId.get(id);
            if (r != null) {
                r.status = newStatus;
                r.reviewedByAdminUserId = adminId;
                r.reviewedAtEpochMs = ts;
            }
        }

        @Override
        public List<RegistrationRequestEntity> getPendingRequests() {
            return byEmail.values().stream()
                    .filter(r -> r.status == RequestStatus.PENDING)
                    .toList();
        }

        @Override
        public List<RegistrationRequestEntity> getRejectedRequests() {
            return byEmail.values().stream()
                    .filter(r -> r.status == RequestStatus.REJECTED)
                    .toList();
        }
    }


    static class FakeStudentDao implements StudentDao {
        @Override public void insert(StudentEntity s) { }
        @Override public StudentEntity findByEmail(String email) { return null; }
        @Override public List<StudentEntity> getAllStudents() { return null; }
    }

    static class FakeTutorDao implements TutorDao {
        @Override public void insert(TutorEntity t) { }
        @Override public List<TutorEntity> getAllTutors() { return null; }
        @Override public TutorEntity findByEmail(String email) { return null; }
        @Override public String getTutorEmail(String userId) { return null; }
        @Override public void updateAverageRating(String userId, double avg) { }
        @Override public TutorEntity findByUserId(String userId) { return null; }
    }

    static class FakeAppDatabase extends AppDatabase {
        final FakeUserDao userDao = new FakeUserDao();
        final FakeStudentDao studentDao = new FakeStudentDao();
        final FakeTutorDao tutorDao = new FakeTutorDao();
        final FakeRegistrationRequestDao regDao = new FakeRegistrationRequestDao();

        @Override public UserDao userDao() { return userDao; }
        @Override public StudentDao studentDao() { return studentDao; }
        @Override public TutorDao tutorDao() { return tutorDao; }
        @Override public RegistrationRequestDao registrationRequestDao() { return regDao; }

        @Override public TutorAvailabilityDao tutorAvailabilityDao() { 
        return null;
        }
    }

    // ----------------------------------------------------------------

    private UserRepository repo;
    private FakeAppDatabase fakeDb;

    @Before
    public void setup() {
        fakeDb = new FakeAppDatabase();
        repo = new UserRepository(fakeDb);
    }

    // ================================================================
    // TEST 1 Student registration request should succeed
    // ================================================================
    @Test
    public void testCreateStudentRegistrationRequest_success() {
        UserRepository.Result<Long> result =
                repo.createStudentRegistrationRequest(
                        "Alice", "Smith", "alice@example.com",
                        "pass123", "123", "Biology");

        assertTrue(result.success);
        assertNotNull(result.data);

        RegistrationRequestEntity saved =
                fakeDb.regDao.findByEmail("alice@example.com");

        assertEquals(RequestStatus.PENDING, saved.status);
        assertNotEquals("pass123", saved.passwordHash); // hashed
        assertEquals("Biology", saved.programOfStudy);
    }

    // ================================================================
    // TEST 2 Tutor registration must fail if no courses
    // ================================================================
    @Test
    public void testCreateTutorRegistrationRequest_missingCourses() {
        UserRepository.Result<Long> result =
                repo.createTutorRegistrationRequest(
                        "Bob", "Jones", "bob@example.com",
                        "mypwd", "555", "PhD", Collections.emptyList());

        assertFalse(result.success);
        assertEquals("VALIDATION", result.code);
    }

    // ================================================================
    // TEST 3 verifyPassword should return true for correct password
    // ================================================================
    @Test
    public void testVerifyPassword_correct() {
        UserEntity u = new UserEntity();
        u.id = UUID.randomUUID().toString();
        u.email = "me@example.com";
        u.passwordHash = PasswordHasher.hash("secret123");
        fakeDb.userDao.insert(u);

        boolean ok = repo.verifyPassword("me@example.com", "secret123");
        assertTrue(ok);
    }

    // ================================================================
    // TEST 4 markRequestApproved should update status + admin fields
    // ================================================================
    @Test
    public void testMarkRequestApproved() {
        // Insert a request manually
        RegistrationRequestEntity r = new RegistrationRequestEntity();
        r.email = "pending@example.com";
        r.firstName = "Test";
        r.lastName = "User";
        r.passwordHash = "x";
        r.role = UserRole.STUDENT;
        r.createdAtEpochMs = System.currentTimeMillis();
        long id = fakeDb.regDao.insert(r);

        repo.markRequestApproved(id, "admin123");

        RegistrationRequestEntity updated =
                fakeDb.regDao.findByEmail("pending@example.com");

        assertEquals(RequestStatus.APPROVED, updated.status);
        assertEquals("admin123", updated.reviewedByAdminUserId);
        assertNotNull(updated.reviewedAtEpochMs);
    }
}

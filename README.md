SEG2105 project_group_1

Members:
- Bingqian Yang
- Nour Boukhtouta
- Dragos Daiciulescu
- Jennate Ryadi
- Darren Qu
- Zachary Djerdjouri

Bing:
The project’s default branch is now feature/part-a-room, which includes the full Part A – Data & Account Infrastructure (Room database, DAOs, entities, repository, and admin seeding).
Team members should pull this branch before continuing work on Parts B, C, and D using:
git checkout feature/part-a-room
git pull origin feature/part-a-room

The main components of my part :
App.java – Initializes the Room database at app startup and automatically seeds the default admin account (admin@otams.ca / Admin#123).
AppDatabase.java – Defines the Room database and connects all entity and DAO classes.
Entities (UserEntity, StudentEntity, TutorEntity) – Represent the data tables for different user roles.
DAOs (UserDao, StudentDao, TutorDao) – Provide methods to insert, query, and manage users in the database.
UserRepository.java – Acts as the single data access point for the UI layer. Includes core functions:
    registerStudent() / registerTutor()
    emailExists() (duplicate email check)
    verifyPassword() (login verification)
PasswordHasher.java – Handles secure password hashing and verification using SHA-256.
Converters.java – Supports complex data types (e.g., lists) for Room database storage.

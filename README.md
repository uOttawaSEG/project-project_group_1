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



Deliverable 3 Part A- Bing

Implemented the Tutor Availability feature.
Tutors can now add, view, and delete their available time slots.
All data is stored in the Room database (version 4).

New Files Added
TutorAvailabilityEntity.java	Defines the tutor availability table (date, start, end, autoApprove).
TutorAvailabilityDao.java	Handles insert, delete, and overlap checks.
TutorAvailabilityActivity.java	Page where tutors manage their slots.
TutorAvailabilityAdapter.java	RecyclerView adapter to display and delete slots.
activity_tutor_availability.xml	Layout for the availability screen.
item_tutor_availability.xml	Layout for a single slot row.

Modified Files
AppDatabase.java	Added TutorAvailabilityEntity, DAO, and MIGRATION_3_4.
App.java	Registered new migration.
MainActivity3.java	Added “Manage Availability” button to open the new page.
activity_main3.xml	Added the button layout.
AndroidManifest.xml	Registered TutorAvailabilityActivity and fixed android:name=".util.App".

Features Implemented
Add new time slot (with date/time pickers).
Validate future date, 30-minute intervals, and non-overlapping times.
View existing slots (sorted by date/time).
Delete existing slots.
Back button to return to Tutor home.

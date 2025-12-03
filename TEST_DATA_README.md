# Test Data Documentation

This document provides complete information about the test database structure and how to populate it.

---

## 🚀 Quick Start

To reset and populate the database with test data:

```bash
# Step 1: Delete the existing database
rm group5Quiz.db

# Step 2: Populate with test data
mvn compile exec:java -Dexec.mainClass="com.example.util.PopulateTestData"

# Step 3: Run the application
mvn javafx:run
```

**Note:** On Windows Command Prompt, use `del group5Quiz.db` instead of `rm`.

---

## 📊 Database Structure Overview

The test database contains:

- **18 Users** (1 admin, 4 teachers, 13 students)
- **8 Groups** (organized by teachers)
- **22 Enrollments** (student-group relationships)
- **12 Quizzes** (assigned to groups)
- **18 Questions** (all worth **1 point each**)

---

## 👥 User Accounts

### 🔐 Admin Account (1)

| ID | Name | Email | Password | Role |
|----|------|-------|----------|------|
| 1 | Admin User | admin@quiz.com | admin123 | admin |

**Permissions:** Full system access - manage users, groups, quizzes, and enrollments.

---

### 👨‍🏫 Teacher Accounts (4)

| ID | Name | Email | Password | Role | Groups |
|----|------|-------|----------|------|--------|
| 2 | Alice Teacher | alice.teacher@quiz.com | teacher123 | teacher | 6 groups |
| 10 | saeka ono | saeka@gmail.com | password | teacher | 1 group |
| 12 | simonida jekic | simonida@gmail.com | password | teacher | 0 groups |
| 18 | John Doe | jdoe@gmail.com | password | teacher | 1 group |

**Permissions:** Create/manage quizzes, view student results for their groups.

---

### 🎓 Student Accounts (13)

| ID | Name | Email | Password | Groups Enrolled |
|----|------|-------|----------|-----------------|
| 4 | Charlie Student | charlie.student@quiz.com | student123 | Morning Section A |
| 5 | Diana Student | diana.student@quiz.com | student123 | Morning Section A, Advanced Programming, best group ever |
| 6 | Eve Student | eve.student@quiz.com | student123 | Morning Section A, Advanced Programming |
| 7 | Frank Student | frank.student@quiz.com | student123 | Afternoon Section B, GROUPB |
| 8 | Grace Student | grace.student@quiz.com | student123 | Afternoon Section B, groupA |
| 9 | Henry Student | henry.student@quiz.com | student123 | Advanced Programming, GROUPB, groupA |
| 11 | sofia avetisian | sofia@gmail.com | password | best group ever, Intro to programming |
| 13 | nathan ayoub | nathan@gmail.com | password | best group ever |
| 14 | silvia lopez | silvia@gmail.com | password | best group ever, Intro to programming |
| 15 | oskar ozclok | oskar | password | best group ever |
| 16 | paula martinez | paula@gmail.com | password | best group ever |
| 17 | Simonida Jekic | sjekic@gmail.com | password | Intro to programming |
| 19 | Simonida Jekic | jekic@gmail.com | password | Intro to programming |

**Permissions:** Take assigned quizzes, view their results and analytics.

---

## 📚 Groups Structure

| ID | Group Name | Teacher | Student Count | Quizzes |
|----|------------|---------|---------------|---------|
| 1 | Morning Section A | Alice Teacher | 3 | Java Basics Quiz |
| 2 | Afternoon Section B | Alice Teacher | 2 | Object-Oriented Programming |
| 3 | Advanced Programming | Alice Teacher | 3 | Data Structures |
| 4 | Beginners Class | Alice Teacher | 0 | Introduction to Programming |
| 6 | GROUPB | Alice Teacher | 2 | quiz1 |
| 7 | groupA | Alice Teacher | 2 | quiz1 |
| 8 | best group ever | saeka ono | 6 | quiz1000 |
| 9 | Intro to programming | John Doe | 4 | Data structures in java |

**Note:** Group IDs 1-4, 6-9 exist (ID 5 was deleted in the original database).

---

## 📝 Quizzes & Questions

### Quiz 1: Java Basics Quiz
- **Group:** Morning Section A
- **Teacher:** Alice Teacher
- **Questions:** 3 (each worth 1 point)
  1. What is the correct way to declare a variable in Java? → A
  2. Which keyword is used to create a class in Java? → B
  3. What is the entry point of a Java application? → B

### Quiz 2: Object-Oriented Programming
- **Group:** Afternoon Section B
- **Teacher:** Alice Teacher
- **Questions:** 2 (each worth 1 point)
  1. Which of these is a principle of OOP? → B
  2. What is inheritance in OOP? → B

### Quiz 3: Data Structures
- **Group:** Advanced Programming
- **Teacher:** Alice Teacher
- **Questions:** 2 (each worth 1 point)
  1. What is the time complexity of accessing an element in an array? → C
  2. Which data structure uses LIFO principle? → B

### Quiz 4: Introduction to Programming
- **Group:** Beginners Class
- **Teacher:** Alice Teacher
- **Questions:** 3 (each worth 1 point)
  1. What does CPU stand for? → A
  2. What is a variable? → B
  3. Which symbol is used for comments in Java? → D

### Quiz 9: quiz1
- **Group:** GROUPB
- **Teacher:** Alice Teacher
- **Questions:** 2 (each worth 1 point)
  1. how old are u → B
  2. how many siblings? → A

### Quiz 10: quiz1
- **Group:** groupA
- **Teacher:** Alice Teacher
- **Questions:** 3 (each worth 1 point)
  1. amazing weather → A
  2. how old are u → A
  3. 1 → A

### Quiz 11: quiz1000
- **Group:** best group ever
- **Teacher:** saeka ono
- **Questions:** 2 (each worth 1 point)
  1. favourite food → A
  2. how many fingers do we have → D

### Quiz 12: Data structures in java
- **Group:** Intro to programming
- **Teacher:** John Doe
- **Questions:** 1 (worth 1 point)
  1. question1 → A

---

## 🎯 Scoring System

**All questions are worth exactly 1 point each.**

### How Scores are Calculated:

1. **Absolute Score** = Number of correct answers
2. **Total Possible Score** = Number of questions × 1 point
3. **Percentage** = (Absolute Score / Total Possible Score) × 100

### Example:
- Quiz with 3 questions (3 points total)
- Student answers 2 correctly
- Score: 2/3 pts = 66.7%

### Grading Scale:
- 🟢 **80-100%**: Excellent (Green)
- 🟡 **60-79%**: Passing (Yellow)
- 🔴 **0-59%**: Needs Improvement (Red)

---

## 🔄 Database Schema

### Tables Created:

1. **people** - User accounts (admin, teacher, student)
2. **groups** - Course/class groups
3. **enrollment** - Student-group relationships (many-to-many)
4. **quiz** - Quiz metadata (name, description, group assignment)
5. **mcq** - Multiple choice questions (4 options, correct answer, score)
6. **quizQuestion** - Quiz-question linkage (many-to-many)
7. **scores** - Quiz attempt records (student, quiz, attempt number, score)
8. **mcqStudentAnswer** - Detailed answer tracking for analytics

---

## 🧪 Testing Scenarios

### Scenario 1: Test Admin Features
```bash
Login: admin@quiz.com / admin123
```
- View all users, groups, and quizzes
- Create new users (teachers/students)
- Create/manage groups
- Enroll students in groups
- View student averages per group

### Scenario 2: Test Teacher Features
```bash
Login: alice.teacher@quiz.com / teacher123
```
- View "Morning Section A" group (has 3 students)
- Create a new quiz for the group
- Add questions (default 1 point each)
- View student list with group averages
- View student attempts on specific quizzes

### Scenario 3: Test Student Features
```bash
Login: diana.student@quiz.com / student123
```
- View assigned quizzes from 3 groups
- Take "Java Basics Quiz" (3 questions)
- View score as percentage (e.g., 2/3 = 66.7%)
- Check analytics chart showing score progress
- Filter quizzes by group

### Scenario 4: Test Multi-Group Student
```bash
Login: henry.student@quiz.com / student123
```
- Enrolled in 3 different groups
- View quizzes from all groups
- Filter quizzes by specific group
- View average score across all groups

---

## 🛠️ Maintenance Commands

### View Current Database Contents
```bash
mvn compile exec:java -Dexec.mainClass="com.example.util.QueryCurrentDB"
```

### Check Question Scores
```bash
sqlite3 group5Quiz.db "SELECT id, question, assigned_score FROM mcq;"
```

### Count Records
```bash
sqlite3 group5Quiz.db "SELECT
  (SELECT COUNT(*) FROM people) as users,
  (SELECT COUNT(*) FROM groups) as groups,
  (SELECT COUNT(*) FROM enrollment) as enrollments,
  (SELECT COUNT(*) FROM quiz) as quizzes,
  (SELECT COUNT(*) FROM mcq) as questions;"
```

---

## 📋 Data Integrity Notes

### User IDs
- Continuous sequence with gaps (ID 3 missing - Bob Teacher was removed)
- Teachers: IDs 2, 10, 12, 18
- Students: IDs 4-9, 11, 13-17, 19

### Group IDs
- Gap at ID 5 (deleted group)
- Active groups: 1, 2, 3, 4, 6, 7, 8, 9

### Quiz IDs
- Gap at ID 5-8 (deleted or never created)
- Active quizzes: 1, 2, 3, 4, 9, 10, 11, 12

### Question IDs
- Continuous sequence 1-18
- All linked to their respective quizzes

---

## 🔍 Verifying the Database

After populating, verify the data:

```bash
# Check users
sqlite3 group5Quiz.db "SELECT COUNT(*), role FROM people GROUP BY role;"

# Check groups
sqlite3 group5Quiz.db "SELECT g.name, COUNT(e.student_id) as students
FROM groups g LEFT JOIN enrollment e ON g.id = e.group_id
GROUP BY g.id;"

# Check question scores (should all be 1)
sqlite3 group5Quiz.db "SELECT DISTINCT assigned_score FROM mcq;"

# Check quiz-question linkage
sqlite3 group5Quiz.db "SELECT q.quiz_name, COUNT(qq.question_id) as questions
FROM quiz q LEFT JOIN quizQuestion qq ON q.id = qq.quiz_id
GROUP BY q.id;"
```

**Expected Results:**
- All users: 18 total (1 admin, 4 teachers, 13 students)
- All groups: 8 active groups
- All question scores: Only value should be `1`
- Quiz questions: Totals should match the quiz structure above

---

## ⚠️ Important Notes

1. **Scores are Percentages**: All UI displays show percentages, not absolute scores
2. **Best Attempts Only**: Averages are calculated from best attempts, not all attempts
3. **Group Averages**: Only include quizzes the student has attempted
4. **No Score Records**: Fresh database has no attempt history (students must take quizzes)
5. **Password Security**: Test passwords are simple - change for production use
6. **Question Scoring**: All questions worth 1 point for consistency

---

## 🐛 Troubleshooting

### Database Won't Delete
**Windows CMD:**
```cmd
del /F group5Quiz.db
```

**Windows PowerShell:**
```powershell
Remove-Item -Force group5Quiz.db
```

**Git Bash / Linux / Mac:**
```bash
rm group5Quiz.db
```

### Compilation Errors
```bash
mvn clean compile
```

### Application Won't Start
```bash
# Check database exists
ls group5Quiz.db

# Verify tables created
sqlite3 group5Quiz.db ".tables"

# Should show: enrollment groups mcq mcqStudentAnswer people quiz quizQuestion scores
```

### Wrong Scores Showing
- Verify all questions are 1 point:
  ```bash
  sqlite3 group5Quiz.db "SELECT DISTINCT assigned_score FROM mcq;"
  ```
- Should only return: `1`
- If not, re-run population script

---

## 📞 Support

If you encounter issues:

1. Ensure Java 18+ is installed: `java -version`
2. Ensure Maven is installed: `mvn -version`
3. Check database file exists: `ls group5Quiz.db`
4. Re-run population script if data seems incorrect
5. Check console output for error messages

---

## 📜 Change Log

### Version 2.0 (Latest - December 2025)
- ✅ All questions now worth 1 point (was 5, 10, 15, 20)
- ✅ Added 4 teachers (Alice, saeka, simonida, John)
- ✅ Added 13 students
- ✅ Added 8 groups
- ✅ Added 12 quizzes
- ✅ All scores displayed as percentages
- ✅ Analytics fixed for proper x-axis alignment
- ✅ Teacher dashboard: Added student list tab with filtered results
- ✅ Student dashboard: Added attempts and grade columns
- ✅ Admin dashboard: Enrollment management improvements

### Version 1.0 (Original)
- Basic test data with Alice and Bob teachers
- 8 students (Charlie through Jack)
- 4 groups
- 4 quizzes
- 10 questions with varied scoring (5-20 points)

---

**Generated:** December 2025
**Database Version:** 2.0
**Application:** QuizPlatform v1.0-SNAPSHOT
**Total Lines of Code:** 4,272 lines across 43 Java files

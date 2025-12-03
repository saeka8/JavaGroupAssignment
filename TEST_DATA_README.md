# Test Data for Quiz Platform

## How to Populate Test Data

Run this command to populate the database with sample data:

```bash
mvn compile exec:java -Dexec.mainClass="com.example.util.PopulateTestData"
```

This will create users, groups, quizzes, and questions for testing.

## Test Login Credentials

### Admin Account
- **Email**: `admin@quiz.com`
- **Password**: `admin123`
- **Capabilities**:
  - Create/manage groups
  - Assign teachers to groups
  - Enroll students in groups
  - View all quizzes
  - Manage users

### Teacher Accounts

#### Teacher 1: Alice
- **Email**: `alice.teacher@quiz.com`
- **Password**: `teacher123`
- **Groups Assigned**:
  - Morning Section A (3 students: Charlie, Diana, Eve)
  - Afternoon Section B (2 students: Frank, Grace)
- **Quizzes Created**:
  - Java Basics Quiz (Morning Section A) - 3 questions
  - Object-Oriented Programming (Afternoon Section B) - 2 questions

#### Teacher 2: Bob
- **Email**: `bob.teacher@quiz.com`
- **Password**: `teacher123`
- **Groups Assigned**:
  - Advanced Programming (3 students: Diana, Eve, Henry)
  - Beginners Class (2 students: Ivy, Jack)
- **Quizzes Created**:
  - Data Structures (Advanced Programming) - 2 questions
  - Introduction to Programming (Beginners Class) - 3 questions

### Student Accounts

All student passwords are: `student123`

1. **Charlie** - `charlie.student@quiz.com`
   - Enrolled in: Morning Section A

2. **Diana** - `diana.student@quiz.com`
   - Enrolled in: Morning Section A, Advanced Programming

3. **Eve** - `eve.student@quiz.com`
   - Enrolled in: Morning Section A, Advanced Programming

4. **Frank** - `frank.student@quiz.com`
   - Enrolled in: Afternoon Section B

5. **Grace** - `grace.student@quiz.com`
   - Enrolled in: Afternoon Section B

6. **Henry** - `henry.student@quiz.com`
   - Enrolled in: Advanced Programming

7. **Ivy** - `ivy.student@quiz.com`
   - Enrolled in: Beginners Class

8. **Jack** - `jack.student@quiz.com`
   - Enrolled in: Beginners Class

## Testing Workflow

### As Admin
1. Login with `admin@quiz.com` / `admin123`
2. Navigate to "Group Management" tab
3. You should see 4 existing groups with their student counts
4. Try creating a new group:
   - Click "+ Create Group"
   - Enter group name
   - Select a teacher
   - Check multiple students from the list
   - Click "Create"
5. Try enrolling/removing students from existing groups

### As Teacher (Alice)
1. Login with `alice.teacher@quiz.com` / `teacher123`
2. You should see "My Groups" section showing:
   - Morning Section A (3 students, 1 quiz)
   - Afternoon Section B (2 students, 1 quiz)
3. Select "Morning Section A"
4. You should see "Java Basics Quiz" with 3 questions
5. Try creating a new quiz:
   - Click "+ New Quiz"
   - Enter quiz details
   - Quiz will be created for the selected group
6. Select a quiz and click "❓ Add Questions"
7. Add multiple choice questions

### As Teacher (Bob)
1. Login with `bob.teacher@quiz.com` / `teacher123`
2. You should see "My Groups" section showing:
   - Advanced Programming (3 students, 1 quiz)
   - Beginners Class (2 students, 1 quiz)
3. Select a group to manage its quizzes
4. Test quiz and question creation

### As Student (Diana)
1. Login with `diana.student@quiz.com` / `student123`
2. Diana is enrolled in TWO groups:
   - Morning Section A (Alice's class)
   - Advanced Programming (Bob's class)
3. Should see quizzes from both groups:
   - Java Basics Quiz
   - Data Structures
4. Take a quiz and see results

## Database Structure

The test data creates:
- **1 Admin**
- **2 Teachers** (Alice and Bob)
- **8 Students**
- **4 Groups** (2 per teacher)
- **4 Quizzes** (1 per group)
- **10 Questions** (distributed across quizzes)

## Notes

- Multiple students can be enrolled in multiple groups
- Each group belongs to exactly one teacher
- Quizzes are assigned to groups, not individual students
- Students automatically get access to quizzes from their enrolled groups

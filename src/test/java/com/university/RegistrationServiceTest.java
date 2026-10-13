package com.university;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    @Test
    void shouldRegisterStudentForValidCourse() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectInactiveCourse() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                false,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }

    @Test
    void shouldRejectMissingPrerequisite() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS401",
                "Advanced Algorithms",
                4,
                30,
                true,
                "CS301"
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS401"));
    }

    @Test
    void shouldAllowRegistrationWhenPrerequisiteIsCompleted() {

        Student student = new Student("S101", "Anita");
        student.addCompletedPrerequisite("CS301");

        Course course = new Course(
                "CS401",
                "Advanced Algorithms",
                4,
                30,
                true,
                "CS301"
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS401"));
    }

    @Test
    void shouldRejectDuplicateRegistration() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));

        boolean secondAttempt = service.register(student, course);

        assertFalse(secondAttempt);
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCreditLimitIsExceeded() {

        Student student = new Student("S101", "Anita");

        // Simulate 20 existing credits
        student.addRegistration("CS101", 4);
        student.addRegistration("CS102", 4);
        student.addRegistration("CS201", 4);
        student.addRegistration("CS202", 4);
        student.addRegistration("CS203", 4);

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }

       @Test
    void registerShouldRejectStudentWhenCourseIsFull() {

        Student student = new Student("S101", "Alice");

        Course course = new Course(
                "CS101",
                "Java Programming",
                3,
                2,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        // Fill the course to its capacity of 2
        course.incrementEnrollment();
        course.incrementEnrollment();

        int enrollmentBefore = course.getCurrentEnrollment();
        int creditsBefore = student.getTotalCredits();

        boolean result = service.register(student, course);

        assertFalse(result,
                "Registration should fail when the course is full");

        assertEquals(enrollmentBefore, course.getCurrentEnrollment(),
                "Enrollment count should remain unchanged");

        assertFalse(student.isRegistered("CS101"),
                "Student should not be registered");

        assertEquals(creditsBefore, student.getTotalCredits(),
                "Student's total credits should remain unchanged");

        assertTrue(student.getRegisteredCourses().isEmpty(),
                "Student's registered course list should remain empty");
    }

    @Test
    void registerShouldAcceptStudentWhenExactlyOneSeatRemains() {

        Student student = new Student("S102", "Bob");

        Course course = new Course(
                "CS102",
                "Database Systems",
                4,
                2,
                true,
                null
        );

        RegistrationService service = new RegistrationService();
        // One student is already enrolled; one seat remains
        course.incrementEnrollment();

        int enrollmentBefore = course.getCurrentEnrollment();
        int creditsBefore = student.getTotalCredits();

        boolean result = service.register(student, course);

        assertTrue(result,
                "Registration should succeed when one seat remains");

        assertEquals(enrollmentBefore + 1,
                course.getCurrentEnrollment(),
                "Enrollment count should increase by one");

        assertTrue(student.isRegistered("CS102"),
                "Student should be registered for the course");

        assertTrue(student.getRegisteredCourses().contains("CS102"),
                "Registered course should appear in the student's course list");

        assertEquals(creditsBefore + 4, student.getTotalCredits(),
                "Student's credits should increase by the course credits");

        assertEquals(2, course.getCurrentEnrollment(),
                "Course should reach capacity after registration");
    }

   
}
package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class StudentService {
    private final String url = "https://hccs-advancejava.s3.amazonaws.com/student_course.json";
    private List<Student> students; // Declare the list of students

    public List<Student> fetchStudents() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        students = Arrays.asList(objectMapper.readValue(json, Student[].class)); // Store fetched students in the list

        return students;
    }

    public Student findStudentByName(String firstName) {
        return students.stream()
                .filter(student -> student.getFirstName().equalsIgnoreCase(firstName))
                .findFirst()
                .orElse(null);
    }

    public Course findCourseByNumber(String courseNumber) {
        return students.stream()
                .flatMap(student -> student.getCourses().stream())
                .filter(course -> course.getCourseNumber().equalsIgnoreCase(courseNumber))
                .findFirst()
                .orElse(null);
    }

    public double calculateGPA(Student student) {
        double totalPoints = 0;
        int totalCredits = 0;

        for (Course course : student.getCourses()) {
            int points = convertGradeToPoints(course.getGrade());
            totalPoints += points * course.getCreditHours();
            totalCredits += course.getCreditHours();
        }
        return totalCredits > 0 ? totalPoints / totalCredits : 0;
    }

    private int convertGradeToPoints(String grade) {
        switch (grade.toUpperCase()) {
            case "A": return 4;
            case "B": return 3;
            case "C": return 2;
            case "D": return 1;
            default: return 0; // F or invalid grades
        }
    }
}

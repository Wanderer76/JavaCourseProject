package com.company;

import java.util.ArrayList;
import java.util.List;

public class Student extends Person {
    private List<Course> courses;
    private final String group;

    public Student(String name, String surname, String group) {
        super(name, surname);
        courses = new ArrayList<>();
        this.group = group;
    }

    public Student(Person p, String group, List<Course> courses) {
        super(p.getName(), p.getSurname());
        this.courses = courses;
        this.group = group;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        for(var course : courses)
            builder.append('\t').append(course.toString());
        return super.toString() +'\n' + builder.toString();
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course courses) {
        this.courses.add(courses);
    }

    public void addSubjects(List<Course> courses) {
        this.courses.addAll(courses);
    }

    public String getGroup() {
        return group;
    }
}

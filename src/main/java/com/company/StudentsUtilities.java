package com.company;

import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.List;

public class StudentsUtilities {

    public static List<Student> createStudentsWithVkData(List<JsonObject> vkData) throws SQLException, ClassNotFoundException {
        List<Student> students = CSV.parseStudentsFromCSV("java-rtf.csv");
        for (var student : students) {
            var filtered = vkData.stream().filter(a -> a.get("first_name").getAsString().equals(student.getName()) && a.get("last_name").getAsString().equals(student.getSurname())).findFirst();
            if (filtered.isPresent()) {
                var value = filtered.get();

                if (value.has("bdate")) {
                    var birthdate = value.get("bdate").getAsString();
                    if (birthdate != null && !birthdate.equals("")) {
                        student.setBirthdate(birthdate);
                    }
                }

                if (value.has("city")) {
                    var city = value.get("city").getAsJsonObject().get("title").getAsString();
                    if (!city.equals("")) {
                        student.setCity(city);
                    }
                }

                var photo = value.get("photo_max").getAsString();
                if (!photo.equals("")) {
                    student.setPhoto(photo);
                }
                student.setVkId(value.get("id").getAsInt());
            }
        }
        return students;
    }
}

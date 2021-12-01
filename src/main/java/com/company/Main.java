package com.company;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.sql.SQLException;
import java.util.List;


public class Main {

    public static void main(String[] args) throws ClientException, ApiException, SQLException, ClassNotFoundException {
        var s = new VkApi().find_users();
        var students = StudentsUtilities.createStudentsWithVkData(s);
        int index = 1;
        for (var i : students) {
            var output = index + ") " + i.getVkId() + ": " + i.getName() + " " + i.getSurname() + " " + i.getBirthdate() + " " + i.getCity() + i.getCourses();
            System.out.println(i.toString());
            index++;
        }

       //List<Student> students = CSV.parseStudentsFromCSV("java-rtf.csv");


    }
}


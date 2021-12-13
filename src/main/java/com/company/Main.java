package com.company;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.sql.SQLException;
import java.util.List;


public class Main {

    public static void main(String[] args) throws ClientException, ApiException, SQLException, ClassNotFoundException {
        var students = StudentsUtilities.createStudentsWithVkData(new VkApi().find_users());
       // SqLite.Conn();

        int index = 1;
        for (var i : students) {
         //   SqLite.WriteDB(i);
            var output = index + ") " + i.getVkId() + ": " + i.getName() + " " + i.getSurname() + " " + i.getBirthdate() + " " + i.getCity() + i.getCourses();
            System.out.println(output);
            index++;
        }
       // SqLite.CloseDB();
       //List<Student> students = CSV.parseStudentsFromCSV("java-rtf.csv");
    }
}


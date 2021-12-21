package com.company;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;


/*
График
[]Количество решенных задач в теме
[]Количество правильных ответов в теме

ВК
[x]Статистику городов студентов записанных на курс
[x]Статистику возрастов студентов записанных на курс
[x]Половая структура по курсу

[x]Количество решенных задач в теме по полу по географии
[x]Количество правильных ответов в теме по полу по географии
*/

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, ClientException, ApiException {

       // var students = StudentsUtilities.createStudentsWithVkData(new VkApi().find_users(), "java-rtf.csv");
        //SqLite.connect();
        var chart = new Charts("title");

        //fillDb(students);

        // var count = SqLite.getCountOfSolevdTasks(students.get(3),students.get(3).getCourses().get(0).getThemes().get(1));
        // System.out.println(count);

       /* for(var i : students){
            System.out.println(SqLite.getCountOfSolevdTasksInTheme(i,"Массивы и управляющие конструкции"));
        }*/
/*
        var temp =SqLite.getSolvedAnswersInTheme("Массивы и управляющие конструкции");
        for(var i : temp.keySet()){
            System.out.println(i+" "+temp.get(i));
        }
*/



        chart.setVisible(true);

    }


    private static void fillDb(List<Student> students) throws SQLException {
        SqLite.cleanDb();
        int index = 1;
        for (var i : students) {
            SqLite.writeDB(i);

            System.out.println("Добавлено "+index+" записей");
            //var output = index + ") " + i.getVkId() + ": " + i.getName() + " " + i.getSurname() + " " + i.getBirthdate() + " " + i.getCity() + i.getCourses();
            //System.out.println(output);
            index++;
        }

        System.out.println(SqLite.getCities().size());
        var a = SqLite.getGenders();
        for(var i : a.keySet()){
            System.out.println(i + " "+ a.get(i));
        }
    }
}


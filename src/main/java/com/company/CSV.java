package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class CSV {

    private static final List<Integer> tasksCount = Arrays.asList(1,7,9,9,11,8,13,16,7,10,11,3,2,1,1);

    private static List<List<String>> readCSV(String filename)
    {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    public static List<Student> parseStudentsFromCSV(String filename) throws SQLException, ClassNotFoundException {
        List<List<String>> result = readCSV(filename);

        List<Student> students = new ArrayList<>();
        List<String> themes = new ArrayList<>();
        List<String> tasksName = new ArrayList<>(result.get(1));
        for(String theme: result.get(0))
        {
            if(theme.length() >1)
                themes.add(theme);
        }

        for(var i : result.subList(3,result.size()))
        {
            String[] fio = i.get(0).split(" ");
            var person =new Person(fio[1],fio[0]);
            var courses = new ArrayList<Course>();
            var studentTasks = new ArrayList<Theme>();

            int startIndex = 2;
            for(var j =0; j<themes.size();j++) {
                var tasks = new ArrayList<Task>();
                List<String> s = tasksName.subList(startIndex,startIndex + tasksCount.get(j));
                int temp = startIndex;
                for(var k : s) {
                    tasks.add(new Task(k,Integer.parseInt(i.get(temp))));
                    temp++;
                }
                studentTasks.add(new Theme(themes.get(j),tasks,tasks.get(0).getScore(),Integer.parseInt(result.get(2).get(startIndex))));
                startIndex = startIndex+tasksCount.get(j);
            }

            var course = new Course("Java",studentTasks,Integer.parseInt(result.get(2).get(2)),i.get(1));
            courses.add(course);
            students.add(new Student(person,i.get(1),courses));
        }
        return students;
    }
}

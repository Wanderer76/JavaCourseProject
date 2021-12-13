package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqLite {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:db.s3db");
        statmt = conn.createStatement();
        System.out.println("База Подключена!");
    }

    // --------Создание таблицы--------
    public static void CreateDB() throws ClassNotFoundException, SQLException
    {

        statmt.execute("CREATE TABLE IF NOT EXISTS course\n" +
                "(\n" +
                "    id          integer not null\n" +
                "        primary key autoincrement,\n" +
                "    courseName  text    not null,\n" +
                "    maxScore    integer not null,\n" +
                "    courseGroup text    not null,\n" +
                "    studentId   integer not null\n" +
                ");");
        statmt.execute("CREATE TABLE IF NOT EXISTS person\n" +
                "(\n" +
                "    personId  integer not null\n" +
                "        primary key autoincrement,\n" +
                "    name      text    not null,\n" +
                "    surname   text    not null,\n" +
                "    city      text    not null,\n" +
                "    birthdate text    not null,\n" +
                "    image     text    not null,\n" +
                "    vkId      integer not null\n" +
                ");");
        statmt.execute("CREATE TABLE IF NOT EXISTS student\n" +
                "(\n" +
                "    student_id integer not null\n" +
                "        references person,\n" +
                "    course_id  integer not null\n" +
                "        references course\n" +
                ");");
        statmt.execute("CREATE TABLE IF NOT EXISTS task\n" +
                "(\n" +
                "    id         integer not null\n" +
                "        primary key autoincrement,\n" +
                "    task_name  text    not null,\n" +
                "    score      integer default 0 not null,\n" +
                "    theme_name text    not null\n" +
                "        references theme (theme_name)\n" +
                ");");
        statmt.execute("CREATE TABLE IF NOT EXISTS theme\n" +
                "(\n" +
                "    theme_name      text    not null,\n" +
                "    studentMaxPoint integer not null,\n" +
                "    maxPoint        integer not null,\n" +
                "    course_id       integer not null\n" +
                "        references course,\n" +
                "    themeId         integer not null\n" +
                "        primary key autoincrement\n" +
                ");");

        System.out.println("Таблица создана или уже существует.");
    }

    // --------Заполнение таблицы--------
    public static boolean WriteDB(Student student) throws SQLException
    {
        var courses = student.getCourses();


        insertPerson(new String[]{student.getName(),student.getSurname(),student.getCity(),student.getBirthdate(),student.getPhoto(),String.valueOf(student.getVkId())});
        var student_id = statmt.executeQuery("SELECT * FROM person WHERE name='"+student.getName()+"' and vkId="+student.getVkId()+";").getInt("personId");
        var coursesIds = insertCourses(courses,student_id);

        for(var id : coursesIds)
        {
            var query = "INSERT INTO student (student_id, course_id)\n" +
                    "values (" + student_id+ ", "+ id+");";
            try {
                statmt.execute(query);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        }

        //return statmt.execute("INSERT INTO 'users' ('name', 'surname') VALUES ('"+ name + "', '" +surname +"');");
        //statmt.execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Vasya', 321789); ");
        //statmt.execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Masha', 456123); ");
        //System.out.println("Таблица заполнена");
        return false;
    }

    private static void insertPerson(String[] person) throws SQLException {
        var builder = new StringBuilder();
        for (var i : person) {
            builder.append("'").append(i).append("'").append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        var query = "INSERT INTO person (name, surname, city, birthdate, image, vkId)\n" +
                "values (" + builder.toString() + ");";
        try {
            statmt.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<Integer> insertCourses(List<Course> courses, int  student_id) throws SQLException {
        var result = new ArrayList<Integer>();
        for(var course : courses){
            //---Записываем основные данные курса---------------------

            var courseName = course.getName();//pk
            var maxScore = course.getMaxScore();//yes
            var group = course.getGroup();//yes

            var courseQuery = "INSERT INTO course (courseName, maxScore, courseGroup, studentId)\n" +
                    "values ('"+ courseName+"', " + maxScore+", '"+group+"', "+student_id+ ");";
            try {
                statmt.execute(courseQuery);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            //------------------------
            var courseId = statmt.executeQuery("SELECT * FROM course WHERE courseName='" + courseName+"' and studentId="+ student_id+";").getInt("id");
            result.add(courseId);
            for(var theme : course.getThemes()){
                var themeName = theme.getName();
                var studentMaxPoint = theme.getStudentMaxPoint();
                var maxPoint = theme.getMaxPoint();

                var themeQuery = "INSERT INTO theme (theme_name, studentMaxPoint, maxPoint, course_id)\n" +
                        "values ('" +themeName+"', "+studentMaxPoint+ ", "+ maxPoint+", "+courseId+");";

                try {
                    statmt.execute(themeQuery);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }

                for(var task: theme.getTasks()){
                    var taskScore = task.getScore();
                    var taskName = task.getName();

                    var taskQuery = "INSERT INTO task (task_name, score, theme_name)\n" +
                            "values ('"+taskName+"', "+taskScore+", '"+ themeName +"'"+ ");";

                    try {
                        statmt.execute(taskQuery);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }

                }

            }

        }
        return result;
    }


    // -------- Вывод таблицы--------
    public static void ReadDB() throws ClassNotFoundException, SQLException
    {
        /*resSet = statmt.executeQuery("SELECT * FROM users");

        while(resSet.next())
        {
            int id = resSet.getInt("id");
            String  name = resSet.getString("name");
            String  surname = resSet.getString("surname");
            System.out.println( "ID = " + id );
            System.out.println( "name = " + name );
            System.out.println( "phone = " + surname );
            System.out.println();
        }

        System.out.println("Таблица выведена");*/
    }

    // --------Закрытие--------
    public static void CloseDB() throws ClassNotFoundException, SQLException
    {
        conn.close();
        statmt.close();
        resSet.close();

        System.out.println("Соединения закрыты");
    }
}

package com.company;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class SqLite {
    private static Connection conn;
    private static Statement statmt;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void connect() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:db.s3db");
        statmt = conn.createStatement();
        System.out.println("База Подключена!");
    }

    // --------Создание таблицы--------
    public static void createDB() throws SQLException {

        statmt.execute("CREATE TABLE IF NOT EXISTS course\n" +
                "(\n" +
                "    id          integer not null\n" +
                "        primary key autoincrement,\n" +
                "    courseName  text    not null,\n" +
                "    maxScore    integer not null,\n" +
                "    courseGroup text    not null,\n" +
                "    studentId   integer not null\n" +
                ");");
        statmt.execute("CREATE TABLE IF NOT EXISTS person(\n" +
                "    personId  integer not null\n" +
                "        primary key autoincrement,\n" +
                "    name      text    not null,\n" +
                "    surname   text    not null,\n" +
                "    city      text    not null,\n" +
                "    birthdate date,\n" +
                "    image     text    not null,\n" +
                "    vkId      integer not null,\n" +
                "    gender    integer default 0 not null\n" +
                ");");

        statmt.execute("CREATE TABLE IF NOT EXISTS student(\n" +
                "    student_id integer not null\n" +
                "        references person,\n" +
                "    course_id  integer not null\n" +
                "        references course\n" +
                ");");
        statmt.execute("CREATE TABLE IF NOT EXISTS task(\n" +
                "    id        integer not null\n" +
                "        primary key autoincrement,\n" +
                "    task_name text    not null,\n" +
                "    score     integer default 0 not null,\n" +
                "    theme_id  integer not null\n" +
                "        references theme,\n" +
                "    max_score integer default 0 not null\n" +
                ");");
        statmt.execute("CREATE TABLE IF NOT EXISTS theme(\n" +
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
    public static boolean writeDB(Student student) throws SQLException {
        var courses = student.getCourses();


        insertPerson(new String[]{student.getName(), student.getSurname(), student.getCity(), student.getBirthdate(), student.getPhoto(), String.valueOf(student.getVkId()), String.valueOf(student.getGender())});
        var student_id = statmt.executeQuery("SELECT * FROM person WHERE name='" + student.getName() + "' and surname= '" + student.getSurname() + "' and vkId=" + student.getVkId() + ";").getInt("personId");
        var coursesIds = insertCourses(courses, student_id);

        for (var id : coursesIds) {
            var query = "INSERT INTO student (student_id, course_id)\n" +
                    "values (" + student_id + ", " + id + ");";
            try {
                statmt.execute(query);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        }

        return false;
    }

    private static void insertPerson(String[] person) {
        var builder = new StringBuilder();
        for (var i : person) {
            builder.append("'").append(i).append("'").append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        var query = "INSERT INTO person (name, surname, city, birthdate, image, vkId, gender)\n" +
                "values (" + builder.toString() + ");";
        try {
            statmt.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<Integer> insertCourses(List<Course> courses, int student_id) throws SQLException {
        var result = new ArrayList<Integer>();
        for (var course : courses) {
            //---Записываем основные данные курса---------------------

            var courseName = course.getName();//pk
            var maxScore = course.getMaxScore();//yes
            var group = course.getGroup();//yes

            var courseQuery = "INSERT INTO course (courseName, maxScore, courseGroup, studentId)\n" +
                    "values ('" + courseName + "', " + maxScore + ", '" + group + "', " + student_id + ");";
            try {
                statmt.execute(courseQuery);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            //------------------------
            var courseId = statmt.executeQuery("SELECT * FROM course WHERE courseName='" + courseName + "' and studentId=" + student_id + ";").getInt("id");
            result.add(courseId);
            for (var theme : course.getThemes()) {
                var themeName = theme.getName();
                var studentMaxPoint = theme.getStudentMaxPoint();
                var maxPoint = theme.getMaxPoint();

                var themeQuery = "INSERT INTO theme (theme_name, studentMaxPoint, maxPoint, course_id)\n" +
                        "values ('" + themeName + "', " + studentMaxPoint + ", " + maxPoint + ", " + courseId + ");";

                try {
                    statmt.execute(themeQuery);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                var themeId = statmt.executeQuery("SELECT * FROM theme WHERE theme_name='" + themeName + "' and course_id =" + courseId + ";").getInt("themeId");

                for (var task : theme.getTasks()) {
                    var taskScore = task.getScore();
                    var taskName = task.getName();

                    var taskQuery = "INSERT INTO task (task_name, score, theme_id, max_score)\n" +
                            "values ('" + taskName + "', " + taskScore + ", '" + themeId + "', " + task.getMaxScore() + ");";

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

    public static void cleanDb() {
        try {
            statmt.execute("delete from course");
            statmt.execute("delete from person");
            statmt.execute("delete from student");
            statmt.execute("delete from task");
            statmt.execute("delete from theme");
            System.out.println("База данных очищена");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // -------- Вывод таблицы--------
    public static List<String> getCities() {
        var result = new ArrayList<String>();
        try {
            var querySet = statmt.executeQuery("select city from person where city != 'None';");
            while (querySet.next()) {
                result.add(querySet.getString("city"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static Map<String,Integer> getPeoplesCities() {
        var result = new HashMap<String,Integer>();
        try {
            var querySet = statmt.executeQuery("select person.city, count(*) as 'count' from person\n" +
                    "where city != 'None' group by person.city;");

            while (querySet.next()) {
                result.put(querySet.getString("city"),querySet.getInt("count"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<Integer, Integer> getAges() {
        var result = new HashMap<Integer, Integer>();
        try {
            var querySet = statmt.executeQuery("select birthdate from person where birthdate is not 'null';\n");
            while (querySet.next()) {
                var birthdate = (querySet.getString("birthdate"));
                var today = LocalDate.now();
                Period diff = Period.between(LocalDate.parse(birthdate, DateTimeFormatter.ISO_LOCAL_DATE), today);
                var age = diff.getYears();
                if (result.containsKey(age)) {
                    result.put(age, result.get(age) + 1);
                } else
                    result.put(age, 1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Integer> getGenders() {
        var result = new HashMap<String, Integer>();
        try {
            var maleQuerySet = statmt.executeQuery("select count(*) as 'male' from person where gender= 'Male';");
            result.put("male", maleQuerySet.getInt("male"));
            var femaleQuerySet = statmt.executeQuery("select count(*) as 'female' from person where gender = 'Female';");
            result.put("female", femaleQuerySet.getInt("female"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getCountOfSolevdTasksInTheme(Student student, Theme theme) {
        var count = 0;
        var query = "select count(*) as 'count' from person\n" +
                "left join student on person.personId = student.student_id\n" +
                "left join course on course.id = student.course_id\n" +
                "left join theme  on course.id = theme.course_id and theme.theme_name like '%" + theme.getName() + "%'\n" +
                "join task on task.theme_id = theme.themeId\n" +
                "where person.name = '" + student.getName() + "' and person.surname='" + student.getSurname() + "' and task.max_score = task.score and length(task_name)>2;\n";
        try {
            count = statmt.executeQuery(query).getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static int getCountOfSolevdTasksInTheme(Student student, String themeName) {
        var count = 0;
        var query = "select count(*) as 'count' from person\n" +
                "left join student on person.personId = student.student_id\n" +
                "left join course on course.id = student.course_id\n" +
                "left join theme  on course.id = theme.course_id and theme.theme_name like '%" + themeName + "%'\n" +
                "join task on task.theme_id = theme.themeId\n" +
                "where person.name = '" + student.getName() + "' and person.surname='" + student.getSurname() + "' and task.max_score = task.score " +
                "and (task_name not like '%Контрольный вопрос%' and task_name not like '%Контрольные вопросы%' ) and length(task_name)>2;";
        try {
            count = statmt.executeQuery(query).getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public static List<TasksStats> getCountOfSolvedTasksInTheme(String name, String surname ,String themeName) {
        var result = new ArrayList<TasksStats>();
        /*var query = "select count(*) as 'count' from person\n" +
                "left join student on person.personId = student.student_id\n" +
                "left join course on course.id = student.course_id\n" +
                "left join theme  on course.id = theme.course_id and theme.theme_name like '%" + themeName + "%'\n" +
                "join task on task.theme_id = theme.themeId\n" +
                "where person.name = '" + name + "' and person.surname='" + surname + "' and task.max_score = task.score " +
                "and (task_name not like '%Контрольный вопрос%' and task_name not like '%Контрольные вопросы%' ) and length(task_name)>2;";*/
        var query = "select task.task_name as task_name, task.max_score as max, task.score as score from person\n" +
                "    left join student on person.personId = student.student_id\n" +
                "    left join course on course.id = student.course_id\n" +
                "    left join theme  on course.id = theme.course_id and theme.theme_name like '%"+themeName+"%'\n" +
                "    join task on task.theme_id = theme.themeId\n" +
                "    where person.name = '"+name+"' and person.surname='"+surname+"'\n" +
                "    and (task_name not like '%Контрольный вопрос%' and task_name not like '%Контрольные вопросы%')\n" +
                "    and length(task_name)>2\n" +
                "    group by task.max_score,task.score;";
        try {
            var resultSet = statmt.executeQuery(query);
            while (resultSet.next()) {
             var count = new TasksStats();
                count.max = resultSet.getInt("max");
                count.score = resultSet.getInt("score");
                count.name = resultSet.getString("task_name");
                result.add(count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getCountOfSolvedAnswersInTheme(String name, String surname ,String themeName) {
        var count = 0;
        var query = "select count(*) as 'count' from person\n" +
                "left join student on person.personId = student.student_id\n" +
                "left join course on course.id = student.course_id\n" +
                "left join theme  on course.id = theme.course_id and theme.theme_name like '%" + themeName + "%'\n" +
                "join task on task.theme_id = theme.themeId\n" +
                "where person.name = '" + name + "' and person.surname='" + surname + "' and task.max_score = task.score " +
                "and (task_name like '%Контрольный вопрос%' or task_name like '%Контрольные вопросы%' ) and length(task_name)>2;";
        try {
            count = statmt.executeQuery(query).getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static Map<String, Integer> getSolvedAnswersInTheme(String themeName) {
        var query = "select person.name, person.surname, count(person.surname) as 'count' from person\n" +
                "left join student on person.personId = student.student_id\n" +
                "join course on course.id = student.course_id\n" +
                "left join theme  on course.id = theme.course_id and theme.theme_name like '%" + themeName + "%'\n" +
                "join task on task.theme_id = theme.themeId\n" +
                "where task.max_score = task.score and length(task_name)>2 and task_name like '%Контрольный вопрос%'\n" +
                "group by person.name, person.surname;";

        var result = new HashMap<String, Integer>();

        try {
            var resultSet = statmt.executeQuery(query);

            while (resultSet.next()) {
                var surname = resultSet.getString("surname");
                var name = resultSet.getString("name");
                var count = resultSet.getInt("count");
                result.put(surname + " " + name, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<SolvedTasksByCityGender> getSolvedAnswerByGender(String themeName) {
        var query = "select person.gender, person.city,count(task.task_name) as 'count' from person\n" +
                "left join student on person.personId = student.student_id\n" +
                "join course on course.id = student.course_id\n" +
                "left join theme  on course.id = theme.course_id and theme.theme_name like '%" + themeName + "%'\n" +
                "join task on task.theme_id = theme.themeId\n" +
                "where task.max_score = task.score and length(task_name)>2 and (task_name like '%Контрольный вопрос%' or task_name like '%Контрольные вопросы%') and city!='None'\n" +
                "group by person.gender, person.city;";

        var result = new ArrayList<SolvedTasksByCityGender>();

        try {
            var resultSet = statmt.executeQuery(query);

            while (resultSet.next()) {
                var gender = resultSet.getString("gender");
                var city = resultSet.getString("city");
                var count = resultSet.getInt("count");
                result.add(new SolvedTasksByCityGender(gender, city, count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static List<String> getThemes(){
        var query = "select distinct theme_name from theme order by theme_name";
        var result = new ArrayList<String>();
        try {
            var resultSet = statmt.executeQuery(query);
            while (resultSet.next()){
                result.add(resultSet.getString("theme_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.subList(0,result.size()-2);
    }

    public static Map<String,Integer> getSolvedAnswerByCity(String themeName) {
        var query = "select person.city,count(task.task_name) as 'count' from person\n" +
                "left join student on person.personId = student.student_id\n" +
                "join course on course.id = student.course_id\n" +
                "left join theme  on course.id = theme.course_id and theme.theme_name like '%" + themeName + "%'\n" +
                "join task on task.theme_id = theme.themeId\n" +
                "where task.max_score = task.score and length(task_name)>2 and (task_name like '%Контрольный вопрос%' or task_name like '%Контрольные вопросы%') and city!='None'\n" +
                "group by person.city;";

        var result = new HashMap<String,Integer>();

        try {
            var resultSet = statmt.executeQuery(query);

            while (resultSet.next()) {
                var city = resultSet.getString("city");
                var count = resultSet.getInt("count");
                result.put( city, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<SolvedTasksByCityGender> getSolvedTasksByGender(String themeName) {
        var query = "select person.gender, person.city,count(task.task_name) as 'count' from person\n" +
                "left join student on person.personId = student.student_id\n" +
                "join course on course.id = student.course_id\n" +
                "left join theme  on course.id = theme.course_id and theme.theme_name like '%" + themeName + "%'\n" +
                "join task on task.theme_id = theme.themeId\n" +
                "where task.max_score = task.score and task_name not like '%Контрольный вопрос%' and task_name not like '%Контрольные вопросы%' and length(task_name)>2 and city!='None'\n" +
                "group by person.gender, person.city;";

        var result = new ArrayList<SolvedTasksByCityGender>();

        try {
            var resultSet = statmt.executeQuery(query);

            while (resultSet.next()) {
                var gender = resultSet.getString("gender");
                var city = resultSet.getString("city");
                var count = resultSet.getInt("count");
                result.add(new SolvedTasksByCityGender(gender, city, count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // --------Закрытие--------
    public static void closeDB() throws SQLException {
        statmt.close();
        conn.close();
        System.out.println("Соединения закрыты");
    }
}

class SolvedTasksByCityGender {
    private final String gender;
    private final String city;
    private final int count;

    SolvedTasksByCityGender(String gender, String city, int count) {
        this.gender = gender;
        this.city = city;
        this.count = count;
    }

    public String getCity() {
        return city;
    }

    public String getGender() {
        return gender;
    }

    public int getCount() {
        return count;
    }

}
class TasksStats{
    public String name;
    public int max;
    public int score;
}

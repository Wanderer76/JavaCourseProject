package com.company;

import java.sql.*;

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

        statmt.execute("CREATE if not exists TABLE [person] (\n" +
                "[personId] integer  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "[name] varchar(25)  NOT NULL,\n" +
                "[surname] varchar(25)  NOT NULL,\n" +
                "[city] varchar(25)  NOT NULL,\n" +
                "[birthdate] varchar(25)  NOT NULL,\n" +
                "[image] varchar(100)  NOT NULL,\n" +
                "[vkId] integer  NOT NULL\n" +
                ")");
        statmt.execute("CREATE if not exists [student] (\n" +
                "[id] integer  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "[personId_id] integer  NOT NULL,\n" +
                "[studentId_id] bigint  NOT NULL\n" +
                ")");
        statmt.execute("CREATE if not exists TABLE [course] (\n" +
                "[id] integer  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "[courseName] varchar(100)  NOT NULL,\n" +
                "[maxScore] integer  NOT NULL,\n" +
                "[courseGroup] varchar(100)  NOT NULL,\n" +
                "[studentId] integer  NOT NULL,\n" +
                "[themes_id] integer  NOT NULL\n" +
                ")");
        statmt.execute("CREATE if not exists TABLE [theme] (\n" +
                "[theme_name] varchar(25)  NOT NULL,\n" +
                "[studentMaxPoint] integer  NOT NULL,\n" +
                "[maxPoint] integer  NOT NULL,\n" +
                "[courseName] varchar(50)  NOT NULL,\n" +
                "[themeId] integer  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "[tasks_id] bigint  NOT NULL\n" +
                ")");
        statmt.execute("CREATE if not exists TABLE [task] (\n" +
                "[id] integer  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "[task_name] varchar(25)  NOT NULL,\n" +
                "[score] integer  NOT NULL\n" +
                ")");

        System.out.println("Таблица создана или уже существует.");
    }

    // --------Заполнение таблицы--------
    public static boolean WriteDB(String name,String surname) throws SQLException
    {
        //return statmt.execute("INSERT INTO 'users' ('name', 'surname') VALUES ('"+ name + "', '" +surname +"');");
        //statmt.execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Vasya', 321789); ");
        //statmt.execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Masha', 456123); ");
        //System.out.println("Таблица заполнена");
        return false;
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

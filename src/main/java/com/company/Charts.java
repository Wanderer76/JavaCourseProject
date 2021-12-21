package com.company;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.HistogramDataset;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.Callable;

public class Charts extends JFrame {

    private JPanel graphicsPanel;
    private JPanel mainPanel;
    private JTextField textArea;

    public void setPieChart(String title, PieDataset dataset) {
        JFreeChart pieChart = ChartFactory.createPieChart(title, dataset);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSimpleLabels(true);
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);
        pieChart.setPadding(new RectangleInsets(4, 8, 2, 2));
        var panel = new ChartPanel(pieChart);
        graphicsPanel.add(panel);
    }

    public void setHistogramChart(String title, HistogramDataset dataset) {
        JFreeChart pieChart = ChartFactory.createHistogram(title, "человек", "количество решенных", dataset);
        var panel = new ChartPanel(pieChart);
        graphicsPanel.add(panel);
    }



    public void setBarChart(String title, String xLabel, String yLabel, CategoryDataset dataset) {
        var barChart = ChartFactory.createBarChart(title,
                xLabel,
                yLabel,
                dataset);
        var panel = new ChartPanel(barChart);
        graphicsPanel.add(panel);
    }


    public Charts(String title) {
        super(title);
        try {
            SqLite.connect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        graphicsPanel = new JPanel();
        mainPanel = new JPanel();

        textArea = new JTextField();
        textArea.setToolTipText("Введите текст");

        var buttonlayout = new GridLayout(6, 1, 20, 30);
        var mainlayout = new GridLayout(1, 2, 50, 10);
        var graphicsLayout = new BoxLayout(graphicsPanel, BoxLayout.Y_AXIS);

        var buttonPanel = new JPanel();
        buttonPanel.setLayout(buttonlayout);


        var showPeopleByCity = new JButton("людей из городов");
        var showPeopleByAge = new JButton("люди по возрасту");
        var showPeopleByGender = new JButton("люди по полy");
        var showTasks = new JButton("Количество решенных задач в теме по полу по географии");
        var showAnswers = new JButton("Количество правильных ответов в теме по полу по географии");
        var showWrongToRight = new JButton("Количество решенных в теме у человека");

        showPeopleByCity.addActionListener(new ActionByButton(this::showPeopleByCity));
        showPeopleByAge.addActionListener(new ActionByButton(this::showPeopleByAge));
        showPeopleByGender.addActionListener(new ActionByButton(this::showPeopleByGender));
        showTasks.addActionListener(new ActionByButton(()-> showTasks(textArea.getText())));
        showAnswers.addActionListener(new ActionByButton(()->showAnswers(textArea.getText())));
        showWrongToRight.addActionListener(new ActionByButton(()->showWrongToRight(textArea.getText())));

        buttonPanel.add(showPeopleByCity);
        buttonPanel.add(showPeopleByAge);
        buttonPanel.add(showPeopleByGender);
        buttonPanel.add(showTasks);
        buttonPanel.add(showAnswers);
        buttonPanel.add(showWrongToRight);

        mainPanel.setLayout(mainlayout);
        graphicsPanel.setLayout(graphicsLayout);
        graphicsPanel.add(textArea);
        mainPanel.add(buttonPanel);
        mainPanel.add(graphicsPanel);
        add(mainPanel);
        setSize(1280, 720);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                var size = e.getComponent().getSize();
                buttonPanel.setPreferredSize(new Dimension(100, size.height));
            }
        });

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    SqLite.closeDB();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                super.windowClosed(e);
                System.exit(0);
            }
        });

    }



    private Void showPeopleByCity() {

        var answersByCity = SqLite.getPeoplesCities();
        var answersByCityDataset = new DefaultPieDataset<String>();
        for (var i : answersByCity.keySet()) {
            answersByCityDataset.setValue(i, answersByCity.get(i));
        }
        setPieChart("% людей из городов",answersByCityDataset);
        return null;
    }

    private Void showPeopleByAge() {
        Map<Integer, Integer> years = SqLite.getAges();
        var yearsDataset = new DefaultPieDataset<String>();
        for (var i : years.keySet()) {
            yearsDataset.setValue(i+" лет",years.get(i));
        }
        setPieChart("люди по возрасту",yearsDataset);

        return null;
    }

    private Void showPeopleByGender() {
        var genders = SqLite.getGenders();
        var genderDataset = new DefaultPieDataset<String>();
        for (var i : genders.keySet()) {
            if (i.equals("male"))
                genderDataset.setValue("Мужчины", genders.get(i));
            else
                genderDataset.setValue("Женщины", genders.get(i));
        }
        setPieChart("люди по полу", genderDataset);
        return null;
    }
    private Void showTasks(String theme) {
        if (theme.isEmpty())
            return null;
        var themeGenderGeographic = SqLite.getSolvedTasksByGender(theme);
        var themeGenderGeographicDataset = new DefaultCategoryDataset();
        for (var i : themeGenderGeographic) {
            themeGenderGeographicDataset.addValue(i.getCount(), i.getCity(), i.getGender());
        }
        setBarChart("Количество решенных задач в теме по полу по географии", "", "количество в теме", themeGenderGeographicDataset);
        return null;
    }
    private Void showAnswers(String theme) {
        if (theme.isEmpty())
            return null;
        var themeGenderAnswersGeographic = SqLite.getSolvedAnswerByGender(theme);
        var themeGenderAnswersGeographicDataset = new DefaultCategoryDataset();
        for (var i : themeGenderAnswersGeographic) {
            themeGenderAnswersGeographicDataset.addValue(i.getCount(),i.getCity(),i.getGender());
        }
        setBarChart("Количество правильных ответов в теме по полу по географии","","количество решений в теме",themeGenderAnswersGeographicDataset);
        return null;
    }
    private Void showWrongToRight(String name) {
        if(name.isEmpty())
            return null;
        var themes = SqLite.getThemes();
        var themesDataset = new DefaultCategoryDataset();
        try {
            var fio = name.split(" ");
            for (var theme : themes) {
                var answersInTheme = SqLite.getCountOfSolvedTasksInTheme(fio[1], fio[0], theme);
                for (var stats : answersInTheme) {
                    themesDataset.addValue(stats.score, "Полученно", theme);
                    themesDataset.addValue(stats.max, "Максимум", theme);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        setBarChart("правильные ответы к неправильным",name,"баллы",themesDataset);
        return null;
    }

    class ActionByButton implements ActionListener{

        private Callable<Void> func;


        public ActionByButton(Callable<Void> func){
            this.func = func;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(graphicsPanel.getComponents().length>1)
                graphicsPanel.remove(1);
            try {
                func.call();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            graphicsPanel.repaint();
            graphicsPanel.updateUI();
        }
    }


}

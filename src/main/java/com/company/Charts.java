package com.company;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
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

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class Charts extends JFrame {

    private JPanel graphicsPanel;
    private JPanel mainPanel;
    private JTextField textArea;
    private JProgressBar progressBar;


    public Charts(String title) {
        super(title);
        try {
            SqLite.connect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        mainPanel = new JPanel();
        this.add(mainPanel);
        this.setSize(480, 640);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                super.windowClosed(e);
                exit();
            }
        });
    }

    public void start() {
        var layout = new FlowLayout(FlowLayout.CENTER, 0, getHeight() / 2 - getHeight() / 2);
        mainPanel.removeAll();
        layout.setHgap(200);
        layout.setVgap(100);
        mainPanel.setLayout(layout);
        var loadNew = new JButton("загрузить новую ведомость");
        var loadOld = new JButton("продолжить со старой ведомостью");
        var exit = new JButton("выход");
        exit.addActionListener(e -> exit());
        loadNew.addActionListener(e -> SetNewFile());
        loadOld.addActionListener(e -> setGraphsView());

        mainPanel.add(loadNew);
        mainPanel.add(loadOld);
        mainPanel.add(exit);
        mainPanel.setSize(this.getWidth() / 2, this.getHeight() / 2);
    }

    private void setPieChart(String title, PieDataset dataset) {
        JFreeChart pieChart = ChartFactory.createPieChart(title, dataset);
        var plot = (PiePlot) pieChart.getPlot();
        plot.setSimpleLabels(true);
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);
        pieChart.setPadding(new RectangleInsets(4, 8, 2, 2));
        var panel = new ChartPanel(pieChart);
        graphicsPanel.add(panel);
    }

    private void setBarChart(String title, String xLabel, String yLabel, CategoryDataset dataset) {
        var barChart = ChartFactory.createBarChart(title,
                xLabel,
                yLabel,
                dataset);
        var panel = new ChartPanel(barChart);
        graphicsPanel.add(panel);
    }

    private void createGraphsView() {
        graphicsPanel = new JPanel();
        textArea = new JTextField();
        textArea.setToolTipText("Введите текст");

        textArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textArea.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        var buttonPanel = new JPanel();

        var controlLayout = new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS);
        var mainlayout = new GridLayout(1, 2, 50, 20);
        var graphicsLayout = new BoxLayout(graphicsPanel, BoxLayout.Y_AXIS);
        buttonPanel.setLayout(controlLayout);

        var buttonGroup = new ButtonGroup();

        var showPeopleByCity = new JRadioButton("% людей из городов");
        var showPeopleByAge = new JRadioButton("% люди по возрасту");
        var showPeopleByGender = new JRadioButton("% людей по полy");
        var showTasks = new JRadioButton("Количество решенных задач в теме по полу по географии");
        var showAnswers = new JRadioButton("Количество правильных ответов в теме по полу по географии");
        var showWrongToRight = new JRadioButton("Количество решенных в теме у человека");

        showPeopleByCity.setActionCommand("showPeopleByCity");
        showPeopleByAge.setActionCommand("showPeopleByAge");
        showPeopleByGender.setActionCommand("showPeopleByGender");
        showTasks.setActionCommand("showTasks");
        showAnswers.setActionCommand("showAnswers");
        showWrongToRight.setActionCommand("showWrongToRight");

        showPeopleByCity.addActionListener(e -> textArea.setEnabled(false));
        showPeopleByAge.addActionListener(e -> textArea.setEnabled(false));
        showPeopleByGender.addActionListener(e -> textArea.setEnabled(false));
        showTasks.addActionListener(e -> {
            textArea.setEnabled(true);
            textArea.setText("Введите название темы");
        });
        showAnswers.addActionListener(e -> {
            textArea.setEnabled(true);
            textArea.setText("Введите название темы");
        });
        showWrongToRight.addActionListener(e -> {
            textArea.setEnabled(true);
            textArea.setText("Введите фамилию и имя студента");
        });

        buttonGroup.add(showPeopleByCity);
        buttonGroup.add(showPeopleByAge);
        buttonGroup.add(showPeopleByGender);
        buttonGroup.add(showTasks);
        buttonGroup.add(showAnswers);
        buttonGroup.add(showWrongToRight);

        buttonPanel.add(showPeopleByCity);
        buttonPanel.add(showPeopleByAge);
        buttonPanel.add(showPeopleByGender);
        buttonPanel.add(showTasks);
        buttonPanel.add(showAnswers);
        buttonPanel.add(showWrongToRight);


        var acceptButton = new JButton("Показать график");
        textArea.setSize(200, 200);

        acceptButton.addActionListener(e -> drawGraph(buttonGroup.getSelection().getActionCommand(), textArea.getText()));

        mainPanel.setLayout(mainlayout);
        graphicsPanel.setLayout(graphicsLayout);
        buttonPanel.add(textArea);
        buttonPanel.add(acceptButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(graphicsPanel);
        this.add(mainPanel);
    }

    private void drawGraph(String actionCommand, String text) {
        graphicsPanel.removeAll();
        try {
            switch (actionCommand) {
                case "showPeopleByCity":
                    showPeopleByCity();
                    break;
                case "showPeopleByAge":
                    showPeopleByAge();
                    break;
                case "showPeopleByGender":
                    showPeopleByGender();
                    break;
                case "showTasks":
                    showTasks(text);
                    break;
                case "showAnswers":
                    showAnswers(text);
                    break;
                case "showWrongToRight":
                    showWrongToRight(text);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        graphicsPanel.updateUI();
    }

    private void SetNewFile() {
        var file = chooseFileBySelector();
        if (file != null) {
            if (file.getName().split("\\.")[1].equals("csv")) {
                showLoadingState();
                loadDataFromFile(file);
            }
        } else
            start();
    }

    private void loadDataFromFile(File file) {
        new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                try {

                    var data = StudentsUtilities.createStudentsWithVkData(new VkApi().find_users(), file.getAbsolutePath());
                    progressBar.setMinimum(1);
                    progressBar.setMaximum(data.size());
                    progressBar.setStringPainted(true);
                    SqLite.cleanDb();
                    int index = 1;
                    for (var i : data) {
                        SqLite.writeDB(i);
                        progressBar.setValue(index);
                        index++;
                    }
                } catch (ClientException | ApiException | SQLException ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            public void done() {
                setGraphsView();
            }
        }.execute();
    }

    private void setGraphsView() {
        mainPanel.removeAll();
        createGraphsView();
        mainPanel.updateUI();
    }

    private void showLoadingState() {
        mainPanel.removeAll();
        mainPanel.add(new JLabel("Идет загрузка студентов"));
        progressBar = new JProgressBar();
        mainPanel.add(progressBar);
        mainPanel.updateUI();
    }

    private void exit() {
        try {
            SqLite.closeDB();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    private File chooseFileBySelector() {
        var jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile();
        }
        return null;
    }

    private void showPeopleByCity() {
        var answersByCity = SqLite.getPeoplesCities();
        var answersByCityDataset = new DefaultPieDataset<String>();
        for (var i : answersByCity.keySet()) {
            answersByCityDataset.setValue(i, answersByCity.get(i));
        }
        setPieChart("% людей из городов", answersByCityDataset);
    }

    private void showPeopleByAge() {
        var years = SqLite.getAges();
        var yearsDataset = new DefaultPieDataset<String>();
        for (var i : years.keySet()) {
            yearsDataset.setValue(i + " лет", years.get(i));
        }
        setPieChart("% людей по возрасту", yearsDataset);
    }

    private void showPeopleByGender() {
        var genders = SqLite.getGenders();
        var genderDataset = new DefaultPieDataset<String>();
        for (var i : genders.keySet()) {
            if (i.equals("male"))
                genderDataset.setValue("Мужчины", genders.get(i));
            else
                genderDataset.setValue("Женщины", genders.get(i));
        }
        setPieChart("% людей по полу", genderDataset);
    }

    private void showTasks(String theme) {
        if (theme.isEmpty())
            return;
        var themeGenderGeographic = SqLite.getSolvedTasksByGender(theme);
        var themeGenderGeographicDataset = new DefaultCategoryDataset();
        for (var i : themeGenderGeographic) {
            themeGenderGeographicDataset.addValue(i.getCount(), i.getCity(), i.getGender());
        }
        setBarChart("Количество решенных задач в теме по полу по географии", "", "количество в теме", themeGenderGeographicDataset);
    }

    private void showAnswers(String theme) {
        if (theme.isEmpty())
            return;
        var themeGenderAnswersGeographic = SqLite.getSolvedAnswerByGender(theme);
        var themeGenderAnswersGeographicDataset = new DefaultCategoryDataset();
        for (var i : themeGenderAnswersGeographic) {
            themeGenderAnswersGeographicDataset.addValue(i.getCount(), i.getCity(), i.getGender());
        }
        setBarChart("Количество правильных ответов в теме по полу по географии", "", "количество решений в теме", themeGenderAnswersGeographicDataset);
    }

    private void showWrongToRight(String name) {
        if (name.isEmpty())
            return;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        setBarChart("правильные ответы к неправильным", name, "баллы", themesDataset);
    }

}

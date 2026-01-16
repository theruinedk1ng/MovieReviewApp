import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;

public class MainWindow {

    private DefaultListModel<Display> movieListModel;
    private static final String FILE_NAME = "movies.txt";
    private final DefaultListModel<Display> allMovies = new DefaultListModel<>();

    private void loadMoviesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", 5);
                if (data.length < 5) continue;

                Display movie = new Display(
                    data[0],
                    data[1],
                    Integer.parseInt(data[2]),
                    data[3],
                    Integer.parseInt(data[4])
                );

                allMovies.addElement(movie);
                movieListModel.addElement(movie);
            }
        } catch (IOException ignored) {
        }
    }

    @SuppressWarnings("UnnecessaryReturnStatement")
    public MainWindow() {

        JFrame frame = new JFrame("Movie Review App");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addMovie = new JButton("Add new movie");
        JButton edit = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton addReview = new JButton("Add a review");

        buttonPanel.add(addMovie);
        buttonPanel.add(edit);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addReview);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        movieListModel = new DefaultListModel<>();
        loadMoviesFromFile();
        JList<Display> movieList = new JList<>(movieListModel);
        frame.add(new JScrollPane(movieList), BorderLayout.CENTER);


        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); // left-aligned, 5px gaps

        JLabel searchLabel = new JLabel("Enter text to search:");
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 25));

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

        private void filter() {
            String text = searchField.getText().toLowerCase();
            movieListModel.clear();

            for (int i = 0; i < allMovies.size(); i++) {
                Display movie = allMovies.getElementAt(i);

                if (movie.title.toLowerCase().contains(text) ||
                    movie.director.toLowerCase().contains(text)) {
                    movieListModel.addElement(movie);
                }
            }
        }

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            filter();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            filter();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            filter();
        }
        });


        addReview.addActionListener(e -> {
            Display selectedMovie = movieList.getSelectedValue();

            if (selectedMovie == null) {
                JOptionPane.showMessageDialog(frame, "Please select a movie first!");
                return;
            }

            new SecondWindow(frame, selectedMovie);
        });


        addMovie.addActionListener(e -> {

            // We only return if we have an empty movie title, since the user might not know all the details about the movie

            String title = JOptionPane.showInputDialog(frame, "Enter a movie title:");
            if (title == null || title.isEmpty()) return;

            String director = JOptionPane.showInputDialog(frame, "Enter a movie director:");

            String release = JOptionPane.showInputDialog(frame, "Enter movie release year:");

            String actors = JOptionPane.showInputDialog(frame, "Enter movie actors:");

            String gradeInput = JOptionPane.showInputDialog(frame, "Enter a grade:");

            try {
                int year = 0;
                try {
                    year = Integer.parseInt(release);
                } catch (NumberFormatException ignored) {
                    // the user did not provide a year
                }
                int grade = 0;
                try {
                    grade = Integer.parseInt(gradeInput);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid grade!", "Error!", JOptionPane.ERROR_MESSAGE);
                }

                Display movie = new Display(title, director, year, actors, grade);

                allMovies.addElement(movie);
                movieListModel.addElement(movie);


                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                    writer.write(movie.toFileString());
                    writer.newLine();
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error saving movie!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        });


        edit.addActionListener(e -> {
            int index = movieList.getSelectedIndex();

            if (index == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a movie to edit!");
                return;
            }

            Display movie = movieListModel.getElementAt(index);

            String newDirector = JOptionPane.showInputDialog(frame, "Enter a movie director:");
            
            String newRelease = JOptionPane.showInputDialog(frame, "Enter movie release year:");
            
            String newActors = JOptionPane.showInputDialog(frame, "Enter movie actors:");
            
            String newGrade = JOptionPane.showInputDialog(frame, "Enter a grade:");

            if (newDirector != null && !newDirector.isEmpty()) movie.director = newDirector;
            if (newActors != null && !newActors.isEmpty()) movie.actors = newActors;
            
            if (newRelease != null && !newRelease.trim().isEmpty()) {
                try {
                    movie.release = Integer.parseInt(newRelease.trim());
                } catch (NumberFormatException ignored) {
                    // keep old value or default 0
                }
            }

            if (newGrade != null && !newGrade.trim().isEmpty()) {
                try {
                    movie.grade = Integer.parseInt(newGrade.trim());
                } catch (NumberFormatException ignored) {
                    // keep old value or default 0
                }
            }
            
            // update the list
            movieList.repaint();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (int i = 0; i < movieListModel.size(); i++) {
                    writer.write(movieListModel.getElementAt(i).toFileString());
                    writer.newLine();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error saving changes!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int index = movieList.getSelectedIndex();

            if (index == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a movie to remove!");
                return;
            }

            Display movie = movieListModel.getElementAt(index);

            movieListModel.remove(index);
            allMovies.removeElement(movie);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (int i = 0; i < allMovies.size(); i++) {
                    writer.write(allMovies.getElementAt(i).toFileString());
                    writer.newLine();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error saving changes!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
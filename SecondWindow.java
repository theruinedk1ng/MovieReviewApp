import java.awt.*;
import javax.swing.*;

public class SecondWindow extends JDialog {

    private final DefaultListModel<String> reviewModel;

    @SuppressWarnings("UnnecessaryReturnStatement")
    public SecondWindow(JFrame parent, Display movie) {
        super(parent, "Reviews – " + movie.toString(), true);

        ReviewStorage.loadReviews(movie);

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        reviewModel = new DefaultListModel<>();
        movie.getReviews().forEach(reviewModel::addElement);

        JList<String> reviewList = new JList<>(reviewModel);

        JTextField reviewField = new JTextField();
        JButton addButton = new JButton("Add Review");
        JButton deleteReview = new JButton("Delete");

        deleteReview.addActionListener(e -> {
            int index = reviewList.getSelectedIndex();

            if (index == -1) {
                JOptionPane.showMessageDialog(this, "Please select a review to remove!");
                return;
            }

            movie.getReviews().remove(index);
            reviewModel.remove(index);

            ReviewStorage.saveReviews(movie);
        });

        addButton.addActionListener(e -> {
            String text = reviewField.getText().trim();
            if (text.isEmpty()) return;

            movie.addReview(text);
            reviewModel.addElement(text);
            reviewField.setText("");

            ReviewStorage.saveReviews(movie);
        });


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteReview);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(reviewField, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(new JScrollPane(reviewList), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}

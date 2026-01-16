import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewStorage {

    private static final String FILE_NAME = "reviews.txt";

    public static void loadReviews(Display movie) {
        movie.getReviews().clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length != 2) continue;

                if (parts[0].equals(movie.title)) {
                    movie.addReview(parts[1]);
                }
            }
        } catch (IOException ignored) {
        }
    }

    public static void saveReviews(Display movie) {
        List<String> allLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(movie.title + "|")) {
                    allLines.add(line);
                }
            }
        } catch (IOException ignored) {
        }

        // Add current movie reviews
        for (String review : movie.getReviews()) {
            allLines.add(movie.title + "|" + review.replace("\n", " "));
        }

        // Rewrite file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : allLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

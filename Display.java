import java.util.ArrayList;
import java.util.List;

public class Display {

    String title;
    String director;
    int release;
    String actors;
    int grade;

    private final List<String> reviews = new ArrayList<>();

    public Display(String t,  String d, int r, String a, int g) {
        this.title = t;
        this.director = d;
        this.release = r;
        this.actors = a;
        this.grade = g;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void addReview(String review) {
        reviews.add(review);
    }

    // we make a separate method for saving it inside the file so that when we display it
    // we use the split function to read it out of the file
    public String toFileString() {
        return title + "," + director + "," + release + "," + actors + "," + grade;
    }

    // this method is to display the content of the file in the UI
    @Override
    public String toString() {
        String year = release == 0 ? "N/A" : String.valueOf(release);
        String directorString = "".equals(director) ? "N/A" : director;
        String actorsString = "".equals(actors) ? "N/A" : actors;
        return "Movie: " + title + " Director: " + directorString + " Release year: " + year + " Actors: " +
               actorsString + " Grade: " + grade;
    }
}
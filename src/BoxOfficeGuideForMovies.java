import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Клас, який представляє керівництво касовими зборами для фільмів.
 */
public class BoxOfficeGuideForMovies {
    /** Мапа, що зберігає дані про фільми за їхніми назвами. */
    private final Map<String, MovieData> movieMap;

    /**
     * Конструктор, який створює новий об'єкт BoxOfficeGuideForMovies.
     */
    public BoxOfficeGuideForMovies() {
        this.movieMap = new LinkedHashMap<>();
    }

    /**
     * Внутрішній запис, що представляє дані про фільм.
     */
    private record MovieData(String title, String director, String genre, int yearReleased, double boxOfficeEarnings) {
    }

    /**
     * Додає новий фільм до керівництва касовими зборами.
     * 
     * @param title             назва фільму
     * @param director          режисер фільму
     * @param genre             жанр фільму
     * @param yearReleased      рік виходу фільму
     * @param boxOfficeEarnings касові збори фільму
     */
    public void addMovie(String title, String director, String genre, int yearReleased, double boxOfficeEarnings) {
        if (movieMap.containsKey(title)) {
            throw new IllegalArgumentException("Movie with title '" + title + "' already exists");
        }
        movieMap.put(title, new MovieData(title, director, genre, yearReleased, boxOfficeEarnings));
    }

    /**
     * Видаляє фільм з керівництва касовими зборами за його назвою.
     * 
     * @param title назва фільму для видалення
     */
    public void removeMovie(String title) {
        if (!movieMap.containsKey(title)) {
            throw new IllegalArgumentException("Movie with title '" + title + "' not found");
        }
        movieMap.remove(title);
    }

    /**
     * Знаходить фільм за його назвою.
     * 
     * @param title назва фільму для пошуку
     * @return об'єкт MovieData, якщо фільм знайдено, інакше null
     */
    public MovieData findMovieByTitle(String title) {
        return movieMap.get(title);
    }

    /**
     * Повертає список всіх фільмів, відсортованих за касовими зборами.
     * 
     * @return список фільмів, відсортований за касовими зборами
     */
    public List<MovieData> getAllMoviesSortedByBoxOfficeEarnings() {
        List<MovieData> sortedMovies = new ArrayList<>(movieMap.values());
        sortedMovies.sort(Comparator.comparingDouble(MovieData::boxOfficeEarnings).reversed());
        return sortedMovies;
    }

    /**
     * Виводить інформацію про фільм за його назвою.
     * 
     * @param title назва фільму для виводу інформації
     */
    public void printMovieInfoByTitle(String title) {
        MovieData movie = movieMap.get(title);
        if (movie != null) {
            System.out.println("Title: " + movie.title);
            System.out.println("Director: " + movie.director);
            System.out.println("Genre: " + movie.genre);
            System.out.println("Year Released: " + movie.yearReleased);
            System.out.println("Box Office Earnings: " + movie.boxOfficeEarnings);
        } else {
            System.out.println("Movie with title '" + title + "' not found");
        }
    }

    /**
     * Виводить інформацію про всі фільми.
     */
    public void printAllMovies() {
        for (MovieData movieData : movieMap.values()) {
            System.out.println("Title: " + movieData.title + ", Director: " + movieData.director +
                    ", Genre: " + movieData.genre + ", Year Released: " + movieData.yearReleased +
                    ", Box Office Earnings: " + movieData.boxOfficeEarnings);
        }
    }

    /**
     * Зберігає дані про всі фільми у файл.
     * 
     * @param filename ім'я файлу, в який будуть збережені дані про фільми
     */
    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (MovieData movie : movieMap.values()) {
                writer.write(movie.title() + "," + movie.director() + "," + movie.genre() + "," +
                        movie.yearReleased() + "," + movie.boxOfficeEarnings() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void saveToJsonFile(String filename) {
        JSONObject jsonObject = new JSONObject();
        JSONArray moviesArray = new JSONArray();

        for (MovieData movie : movieMap.values()) {
            JSONObject movieObject = new JSONObject();
            movieObject.put("title", movie.title());
            movieObject.put("director", movie.director());
            movieObject.put("genre", movie.genre());
            movieObject.put("yearReleased", movie.yearReleased());
            movieObject.put("boxOfficeEarnings", movie.boxOfficeEarnings());
            moviesArray.add(movieObject);
        }

        jsonObject.put("movies", moviesArray);

        try (FileWriter file = new FileWriter(filename)) {
            file.write(jsonObject.toJSONString());
            System.out.println("JSON file created: " + jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromJsonFile(String filename) {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(filename)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray moviesArray = (JSONArray) jsonObject.get("movies");

            for (Object obj : moviesArray) {
                JSONObject movieObject = (JSONObject) obj;
                String title = (String) movieObject.get("title");
                String director = (String) movieObject.get("director");
                String genre = (String) movieObject.get("genre");
                int yearReleased = ((Long) movieObject.get("yearReleased")).intValue();
                double boxOfficeEarnings = (double) movieObject.get("boxOfficeEarnings");

                addMovie(title, director, genre, yearReleased, boxOfficeEarnings);
            }

            System.out.println("JSON file loaded: " + jsonObject);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Завантажує дані про фільми з файлу.
     * 
     * @param filename ім'я файлу, з якого будуть завантажені дані про фільми
     */
    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String title = parts[0];
                    String director = parts[1];
                    String genre = parts[2];
                    int yearReleased = Integer.parseInt(parts[3]);
                    double boxOfficeEarnings = Double.parseDouble(parts[4]);
                    addMovie(title, director, genre, yearReleased, boxOfficeEarnings);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Знаходить фільм у файлі за його назвою.
     * 
     * @param filename ім'я файлу, в якому буде виконуватися пошук
     * @param title    назва фільму для пошуку
     * @return об'єкт MovieData, якщо фільм знайдено, інакше null
     */
    public MovieData findMovieByTitleFromFile(String filename, String title) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && parts[0].equals(title)) {
                    return new MovieData(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]),
                            Double.parseDouble(parts[4]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Додає новий фільм до файлу з фільмами.
     * 
     * @param filename          ім'я файлу, в який буде доданий новий фільм
     * @param title             назва фільму
     * @param director          режисер фільму
     * @param genre             жанр фільму
     * @param yearReleased      рік виходу фільму
     * @param boxOfficeEarnings касові збори фільму
     */
    public void addMovieToFile(String filename, String title, String director, String genre, int yearReleased,
            double boxOfficeEarnings) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(title + "," + director + "," + genre + "," + yearReleased + "," + boxOfficeEarnings + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Видаляє фільм з файлу з фільмами за його назвою.
     * 
     * @param filename ім'я файлу, з якого буде видалений фільм
     * @param title    назва фільму для видалення
     */
    public void removeMovieFromFile(String filename, String title) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && !parts[0].equals(title)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Точка входу в програму.
     * 
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        BoxOfficeGuideForMovies boxOfficeGuide = new BoxOfficeGuideForMovies();
        boxOfficeGuide.addMovie("Movie 1", "Director 1", "Genre 1", 2020, 1000000);
        boxOfficeGuide.addMovie("Movie 2", "Director 2", "Genre 2", 2021, 2500000);
        boxOfficeGuide.addMovie("Movie 3", "Director 3", "Genre 3", 2022, 1500000);
        boxOfficeGuide.addMovie("Movie 4", "Director 4", "Genre 4", 2023, 2000000);
        boxOfficeGuide.addMovie("Movie 5", "Director 5", "Genre 5", 2024, 3000000);

        // boxOfficeGuide.saveToFile("movies.txt");
        boxOfficeGuide.saveToJsonFile("movies.json");

        boxOfficeGuide = new BoxOfficeGuideForMovies();

        // boxOfficeGuide.loadFromFile("movies.txt");
        boxOfficeGuide.loadFromJsonFile("movies.json");

        String searchTitle = "Movie 2";
        MovieData foundMovie = boxOfficeGuide.findMovieByTitle(searchTitle);
        if (foundMovie != null) {
            System.out.println("Found movie: " + foundMovie.title());
        } else {
            System.out.println("Movie with title '" + searchTitle + "' not found");
        }

        String movieToRemove = "Movie 3";
        boxOfficeGuide.removeMovie(movieToRemove);
        System.out.println("Removed movie: " + movieToRemove);

        List<MovieData> sortedMovies = boxOfficeGuide.getAllMoviesSortedByBoxOfficeEarnings();
        System.out.println("All movies sorted by box office earnings:");
        for (MovieData movie : sortedMovies) {
            System.out.println(movie.title() + ": " + movie.boxOfficeEarnings());
        }

        System.out.println("\nPress Enter to exit...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
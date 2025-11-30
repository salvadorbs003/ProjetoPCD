package GameState;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;

import Quizz.Quiz;


public class QuizLoader {
   public static Quiz load(int index) {
        Gson gson = new Gson();

        try (InputStream is = QuizLoader.class.getClassLoader()
                .getResourceAsStream("resources/lista_perguntas.json")) {

            if (is == null) {
                throw new FileNotFoundException("lista_perguntas.json not found in classpath");
            }

            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

            QuizData data = gson.fromJson(reader, QuizData.class);

            if (data == null || data.quizzes == null || data.quizzes.size() <= index) {
                System.out.println("sike there none");
                return null;
            }

            return data.quizzes.get(index);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static class QuizData {
        List<Quiz> quizzes;
    }
}
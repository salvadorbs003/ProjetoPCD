package GameState;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.google.gson.Gson;
import Quizz.Quiz;
import Quizz.Pergunta;

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
                System.out.println("Quiz not found.");
                return null;
            }

            Quiz quiz = data.quizzes.get(index);

            // --- FIX: Assign sequential IDs (1, 2, 3...) ---
            if (quiz != null && quiz.getPerguntas() != null) {
                List<Pergunta> questions = quiz.getPerguntas();
                for (int i = 0; i < questions.size(); i++) {
                    questions.get(i).setId(i + 1); 
                }
            }
            // -----------------------------------------------

            return quiz;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class QuizData {
        List<Quiz> quizzes;
    }
}
package GameState;

import com.google.gson.Gson;
import GUI.Pergunta;
import java.io.FileReader;
import java.util.List;


public class QuizLoader {
	
	public static List<Pergunta> load(String filePath) {
        try {
            Gson gson = new Gson();
            QuizData data = gson.fromJson(new FileReader(filePath), QuizData.class);
            return data.quizzes.get(0).questions;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private static class QuizData {
        List<Quiz> quizzes;
    }

    private static class Quiz {
        String name;
        List<Pergunta> questions;
    }

}
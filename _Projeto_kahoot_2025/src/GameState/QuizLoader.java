package GameState;

import com.google.gson.Gson;
import Quizz.Quiz;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class QuizLoader {
    private static final String[] QUIZ_PATHS = {
        "_Projeto_kahoot_2025/src/lista_perguntas.json ",
        "ProjetoPCD/_Projeto_kahoot_2025/src/lista_perguntas.json"
    };
	
	public static Quiz load(int index) {
	        Gson gson = new Gson();
	        try(Reader reader = abrirReader()) {
	            QuizData data = gson.fromJson(reader, QuizData.class);
	            if (data == null || data.quizzes == null || data.quizzes.size() <= index) {
	                return null; // evita NullPointer/IOB quando json não tem esse quiz
	            }
	            return data.quizzes.get(index);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
    
    private static Reader abrirReader() throws IOException {
        IOException lastError = null;
        for (String relativePath : QUIZ_PATHS) {
            Path candidate = Path.of(System.getProperty("user.dir")).resolve(relativePath).normalize();
            if (Files.exists(candidate)) {
                return Files.newBufferedReader(candidate);
            }
            lastError = new FileNotFoundException("Não encontrado: " + candidate);
        }
        throw lastError != null ? lastError : new FileNotFoundException("lista_perguntas.json não localizada");
    }

    private static class QuizData {
        List<Quiz> quizzes;
    }
}

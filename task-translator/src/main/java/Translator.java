import java.util.List;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

public class Translator {

    private static final String TRANSLATOR_ENDPOINT = "http://api.funtranslations.com/translate/";

    private final String apiKey;

    public Translator(String apiKey) {
        this.apiKey = apiKey;
    }

    public void translate(List<TranslationTask> tasks) {
        for (TranslationTask task : tasks) {
            translate(task);
        }
    }

    private void translate(TranslationTask task) {
        String translation;
        System.out.printf("Translating task %s\n", task);
        if ("languages".equals(task.targetLanguage)) {
            task.translatedContent = "[translator] Available languages: yoda, pirate, minion, sindarin, sith, " +
                    "oldenglish, shakespeare, klingon, jive, leetspeak";
        } else {
            try {
                String call = String.format("%s%s.json", TRANSLATOR_ENDPOINT, task.targetLanguage);
                HttpResponse<JsonNode> response = Unirest.post(call)
                        .header("X-Funtranslations-Api-Secret", this.apiKey)
                        .field("text", task.content)
                        .asJson();

                if (response.getStatus() == 404) {
                    System.out.printf("Unknown target language %s\n", task.targetLanguage);
                    translation = String.format("%s [unknown target language '%s']", task.content, task.targetLanguage);
                } else if (response.getStatus() == 429) {
                    System.out.println("Rate limit reached");
                    translation = task.content + " [translation rate limit reached]";
                } else {
                    System.out.println(response.getBody().getObject());
                    JSONObject obj = response.getBody().getObject();
                    translation = obj.getJSONObject("contents").getString("translated");
                    System.out.println("Translation successful");
                }
                task.translatedContent = translation;
            } catch (Exception e) {
                System.err.println("Error while translating task");
                e.printStackTrace();
                task.translatedContent = task.content + " [translation failed]";
            }
        }
    }
}

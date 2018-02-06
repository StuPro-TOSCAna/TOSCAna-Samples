import java.net.URLEncoder;
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
        try {
            String encodedContent = URLEncoder.encode(task.content, "UTF-8");
            String call = String.format("%s/%s.json", TRANSLATOR_ENDPOINT, task.targetLanguage.name);
            HttpResponse<JsonNode> response = Unirest.post(call)
                    .header("X-Funtranslations-Api-Secret", this.apiKey)
                    .field("text", encodedContent)
                    .asJson();

            JSONObject obj = response.getBody().getObject();
            String translation = obj.getJSONObject("contents").getString("translated");
            task.content = translation;
        } catch (Exception e) {
            System.err.println("Error while translating task");
            e.printStackTrace();
            task.content = task.content + " [translation failed]";
        }
    }
}

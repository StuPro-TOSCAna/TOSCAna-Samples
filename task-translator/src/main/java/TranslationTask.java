public class TranslationTask extends Task {

    public final String targetLanguage;
    public String translatedContent;

    public TranslationTask(int id, String content, String targetLanguage) {
        super(id, content);
        this.targetLanguage = targetLanguage;
    }

    @Override
    public String toString() {
        return String.format("[TranslationTask: id='%s', content='%s', translation='%s', targetLanguage='%s']",
                id, content, translatedContent, targetLanguage);
    }
}

public class TranslationTask extends Task {

    public final TargetLanguage targetLanguage;
    private String translatedContent;

    public TranslationTask(Task task, TargetLanguage targetLanguage) {
        super(task.id, task.content.split(" ", 2)[1]);
        this.targetLanguage = targetLanguage;
    }
}

import java.util.List;

public class Updater implements Runnable {

    private final int updateIntervalMS;
    private final Repository repository;
    private final Translator translator;

    public Updater(Repository repository, Translator translator, int updateInterval) {
        this.repository = repository;
        this.translator = translator;
        this.updateIntervalMS = updateInterval * 1000;
    }

    public void run() {
        System.out.println("Starting update loop");
        while (!Thread.interrupted()) {
            try {
                List<TranslationTask> tasks = repository.getTranslationTasks();
                translator.translate(tasks);
                repository.updateTasks(tasks);
                Thread.sleep(updateIntervalMS);
            } catch (InterruptedException e) {
            }
        }
    }
}

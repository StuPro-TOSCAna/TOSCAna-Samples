import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Repository {

    private int lastId = -1;

    private Connection conn;

    public Repository(String host, String port, String dbName, String user, String password) {
        connect(host, port, dbName, user, password);
    }

    private void connect(String host, String port, String dbName, String user, String password) {
        try {
            Properties connectionProps = new Properties();
            connectionProps.put("user", user);
            connectionProps.put("password", password);
            String connURI = String.format("jdbc:mysql://%s:%s/%s", host, port, dbName);
            this.conn = DriverManager.getConnection(connURI, connectionProps);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database. Trying again in 1 min");
            e.printStackTrace();
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            connect(host, port, dbName, user, password);
        }
    }

    public List<TranslationTask> getTranslationTasks() {
        List<Task> tasks = getNewTasks();
        return getTranslationTasks(tasks);
    }

    private List<Task> getNewTasks() {
        List<Task> tasks = new ArrayList<>();
        String queryUnseenTasks = String.format("select id, task from tasks where id > '%s'", lastId);
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(queryUnseenTasks);
            while (result.next()) {
                int id = result.getInt("id");
                String content = result.getString("task");
                Task task = new Task(id, content);
                tasks.add(task);
                if (id > this.lastId) {
                    this.lastId = id;
                }
            }
            statement.close();
        } catch (SQLException e) {
            System.err.println("failed to read new tasks from db");
            e.printStackTrace();
        }
        return tasks;
    }

    private List<TranslationTask> getTranslationTasks(List<Task> tasks) {
        List<TranslationTask> translationTasks = new ArrayList<>();
        for (Task task : tasks) {
            TargetLanguage targetLanguage;
            if (task.content.startsWith("\\yoda ")) {
                targetLanguage = TargetLanguage.YODA;
            } else if (task.content.startsWith("\\pirate ")) {
                targetLanguage = TargetLanguage.PIRATE;
            } else {
                break;
            }
            translationTasks.add(new TranslationTask(task, targetLanguage));
        }
        return translationTasks;
    }

    public void updateTasks(List<TranslationTask> translationTasks) {
        for (TranslationTask task : translationTasks) {
            try {
                Statement statement = conn.createStatement();
                String query = String.format("update tasks set task = '%s' where id = %s", task.content, task.id);
                statement.executeQuery(query);
                statement.close();
            } catch (SQLException e) {
                System.err.println("failed to update tasks");
                e.printStackTrace();
            }
        }
    }
}

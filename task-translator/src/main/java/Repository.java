import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            connect(host, port, dbName, user, password);
        }
    }

    public List<TranslationTask> getTranslationTasks() {
        List<Task> tasks = getNewTasks();
        List<TranslationTask> translationTasks = getTranslationTasks(tasks);
        System.out.printf("Found %s new tasks; %s need translation\n", tasks.size(), translationTasks.size());
        return translationTasks;
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
            if (task.content.startsWith("/")) {
                String[] tokens = task.content.substring(1).split(" ", 2);
                if (tokens.length >= 1) {
                    String targetLanguage = tokens[0];
                    String strippedContent = tokens.length > 1 ? tokens[1] : "";
                    translationTasks.add(new TranslationTask(task.id, strippedContent, targetLanguage));
                }
            }
        }
        return translationTasks;
    }

    public void updateTasks(List<TranslationTask> translationTasks) {
        for (TranslationTask task : translationTasks) {
            System.out.printf("Writing task %s to db\n", task);
            try {
                String query = "update tasks set task = ? where id = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, task.translatedContent);
                statement.setInt(2, task.id);
                statement.executeUpdate();
                statement.close();
                System.out.println("Successfully updated db");
            } catch (SQLException e) {
                System.err.printf("failed to update task %s\n", task);
                e.printStackTrace();
            }
        }
    }
}

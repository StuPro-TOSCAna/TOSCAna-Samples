public class Main {

    public static final String ENV_DB_HOST = "db_host";
    public static final String ENV_DB_PORT = "db_port";
    public static final String ENV_DB_USER = "db_user";
    public static final String ENV_DB_PASSWORD = "db_password";
    public static final String ENV_DB_NAME = "db_name";

    public static final String ENV_API_KEY = "api_key";
    public static final String ENV_INTERVAL = "update_interval";

    public static void main(String[] args) {
        String apiKey = System.getenv(ENV_API_KEY);
        String intervalString = System.getenv(ENV_INTERVAL);

        int interval;
        try {
            interval = Integer.parseInt(intervalString);
        } catch (NumberFormatException e) {
            interval = 10;
        }

        String host = getEnvSecure(Main.ENV_DB_HOST);
        String port = getEnvSecure(Main.ENV_DB_PORT);
        String dbName = getEnvSecure(Main.ENV_DB_NAME);
        String user = getEnvSecure(Main.ENV_DB_USER);
        String password = getEnvSecure(Main.ENV_DB_PASSWORD);

        Repository repository = new Repository(host, port, dbName, user, password);
        Translator translator = new Translator(apiKey);
        Updater updater = new Updater(repository, translator, interval);
        new Thread(updater).start();
    }

    private static String getEnvSecure(String key){
        String value = System.getenv(key);
        if (value == null || value.isEmpty()){
            System.err.println(String.format("Environment variable '%s' not set, aborting", key));
            System.exit(1);
        }
        return value;


    }
}

public class Enviroment {

    public static String namespaceName;
    public static String eventHubName;
    public static String sasKeyName;
    public static String sasKey;

    public Enviroment(String Env) {
        if (Env.toUpperCase() == "DEV") {
            namespaceName = "event";
            eventHubName = "EventHubs-35252_name_FWEfew34";
            sasKeyName = "iot-hub";
            sasKey = "fRi+lAnqnzaf447I_sasKey_324235";
        } else if (Env.toUpperCase() == "QA") {
            namespaceName = "event";
            eventHubName = "eventhubs-fh6hx_name_6f6q";
            sasKeyName = "iot-hub";
            sasKey = "rhGrHubGmgqsT8KT_sasKey_RUUwdPqJ1QcX+I=";
        }
    }
}
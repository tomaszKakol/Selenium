import java.io.*;
import java.util.Date;
import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {
    private static IQueueClient queueClient;
    public static void main(String[] args) throws Exception{

        Enviroment Env = new Enviroment("QA");// or other, ex. DEV

        // create connection to event hub
        try {
            queueClient = new QueueClient(new ConnectionStringBuilder(
                    Env.eventHubName, Env.namespaceName, Env.sasKeyName, Env.sasKey), null);
        }
        catch (Exception e){
            System.out.print("Unable to connect with IoT Event hub:" + e);
        }

        // create event message
        Events machine = new Events();
        // send message
        queueClient.send(new Message(machine.MotorStart()));

        // close connection
        queueClient.close();
    }
}
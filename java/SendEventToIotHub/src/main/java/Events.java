import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import org.json.simple.JSONObject;
import sun.plugin2.message.Message;

import java.util.Date;

public class Events {

    final String deviceId = "Id_00001";

    public static void Events(){
    }


    public String MotorStart(){
      Event Message = new Event();
      Message.deviceid(deviceId);
      Message.category("machine");
      Message.details("{EN} Start {PL} Start");
      Message.eventKey("MOTOR_START");
      Message.level("notice");
      Message.msgType("event");

      return String.valueOf(Message.CreateJsonMessage());
    }

    public String MotorWarningStatusStart(){
        Event Message = new Event();
        Statuses[4][0] = GetDate();

        Message.deviceid(deviceId);
        Message.category("machine");
        Message.data("V1.RPM", "RPM",-1,"#upper", "obr/min",-5, "RESTART");
        Message.state(GetEventId(Statuses[4][0]),"start", GetDuration(Statuses[4][0],Statuses[4][1]), true, "warning", "RESTART");
        Message.details("DETAILS_THR_WARN");
        Message.eventKey("QA mock event V1.RPM: Warning Hi ");
        Message.level("warning");
        Message.status("RESTART");
        Message.msgType("event");

        return String.valueOf(Message.CreateJsonMessage());
    }

    // helper functions and variables
    long[][] Statuses = new long[12][2];
    // Array schema:
    // Event start(eventId) // Event end //

    private long GetDate(){
        return new Date().getTime();
    }

    private int GetEventId(long timestmap){
        return (int)(timestmap/1000);
    }

    private int GetDuration(long start, long stop){
        return (int) (stop-start);
    }
}

import org.json.simple.JSONObject;

import java.util.Date;

public class Event {

    JSONObject EventMsg = new JSONObject();
    JSONObject state = new JSONObject();
    JSONObject data = new JSONObject();

    private static long GenerateTimestamp(){
        return new Date().getTime();
    }

    public Event() {
        EventMsg.put("timestamp", GenerateTimestamp());//
    }

    public void category(String cat){
        EventMsg.put("category", cat);
    }
    public void details(String details){
        EventMsg.put("details", details);
    }
    public void eventKey(String Key){
        EventMsg.put("eventKey", Key);
    }
    public void level(String lvl){
        EventMsg.put("level", lvl);
    }
    public void msgType(String type){
        EventMsg.put("msgType", type);
    }
    public void deviceid(String id){
        EventMsg.put("deviceid", id);
    }
    public void status(String status){
        EventMsg.put("status", status);
    }


    public void data(String channel, String channelName, int thr, String thrType, String unit){
        dataChannel(channel);
        dataChName(channelName);
        dataThr(thr);
        dataThrType(thrType);
        dataUnit(unit);
        EventMsg.put("data", data);
    }

    public void data(String channel, String channelName, int thr, String thrType, String unit, int extremum){
        dataExtremum(extremum);
        data(channel, channelName, thr, thrType, unit);
    }

    public void data(String channel, String channelName, int thr, String thrType, String unit, int extremum, String status){
        dataStatus(status);
        data(channel, channelName, thr, thrType, unit, extremum);
    }

    public void state(int EventId, String EventStage, int EventDuration, boolean confirmReq, String level, String status){
        stateId(EventId);
        stateStage(EventStage);
        stateDuration(EventDuration);
        stateConfirmReq(confirmReq);
        stateLevel(level);
        stateStatus(status);
        EventMsg.put("state", state);
    }


    private void stateId(int id){
        state.put("id", id);
    }
    private void stateStage(String stag){
        state.put("stage", stag);
    }
    private void stateDuration(int dur){
        state.put("duration", dur);
    }
    private void stateConfirmReq(boolean confirmReq){
        state.put("confirmReq", confirmReq);
    }
    private void stateLevel(String level){
        state.put("level", level);
    }
    private void stateStatus(String status){
        state.put("status", status);
    }

    private void dataChannel(String channel){ data.put("channel", channel); }
    private void dataChName(String name){
        data.put("channelName", name);
    }
    private void dataThr(int tr){
        data.put("thr", tr);
    }
    private void dataThrType(String type){
        data.put("thrType", type);
    }
    private void dataUnit(String unit){
        data.put("unit", unit);
    }
    private void dataExtremum(int extr){
        data.put("extremum", extr);
    }
    private void dataStatus(String status){
        state.put("status", status);
    }


    public JSONObject CreateJsonMessage(){
        return EventMsg;
    }

}

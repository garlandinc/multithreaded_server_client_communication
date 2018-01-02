package server.utility;

/**
 * Created by lemon on 12/17/2016.
 */
public interface CallBack {
    void readMessage(String message);
    void readMessage(Data data);
    void removeDevice(String LOG_IN_NAME);
}

package server.utility;

/**
 * Created by lemon on 11/28/2016.
 */
public class Data {
    public final String FROM,TO,MESSAGE;
    public boolean TOALL=false;

    public Data(String FROM, String TO, String MESSAGE) {
        this.FROM = FROM;
        this.TO = TO;
        this.MESSAGE = MESSAGE;
    }
}

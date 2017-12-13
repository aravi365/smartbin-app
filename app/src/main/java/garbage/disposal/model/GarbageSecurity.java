package garbage.disposal.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Brain on 23/12/2016.
 */

public class GarbageSecurity implements Serializable{

    private Integer securityID;
    private char[] password;
    private GarbageCollector garbageCollector;

    public GarbageSecurity()
    {

    }

    public GarbageSecurity(Integer securityID, char[] password, GarbageCollector garbageCollector) {
        this.securityID = securityID;
        this.password = password;
        this.garbageCollector = garbageCollector;
    }

    public Integer getSecurityID() {
        return securityID;
    }

    public void setSecurityID(Integer securityID) {
        this.securityID = securityID;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public GarbageCollector getGarbageCollector() {
        return garbageCollector;
    }

    public void setGarbageCollector(GarbageCollector garbageCollector) {
        this.garbageCollector = garbageCollector;
    }

    @Override
    public String toString() {
        return "GarbageSecurity{" +
                "securityID=" + securityID +
                ", password=" + Arrays.toString(password) +
                ", garbageCollector=" + garbageCollector +
                '}';
    }
}

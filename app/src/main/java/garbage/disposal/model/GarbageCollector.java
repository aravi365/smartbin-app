package garbage.disposal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Brain on 22/12/2016.
 */

public class GarbageCollector implements Serializable{

    private Integer garbageCollectorID;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private GarbageSecurity garbageSecurity;
    private String email;
    private List<GarbageBin> garbageBins;

public  GarbageCollector()
{

}

    public GarbageCollector(Integer garbageCollectorID, String firstName, String lastName, String mobileNumber, GarbageSecurity garbageSecurity, String email, List<GarbageBin> garbageBins) {
        this.garbageCollectorID = garbageCollectorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.garbageSecurity = garbageSecurity;
        this.email = email;
        this.garbageBins = garbageBins;
    }

    public Integer getGarbageCollectorID() {
        return garbageCollectorID;
    }

    public void setGarbageCollectorID(Integer garbageCollectorID) {
        this.garbageCollectorID = garbageCollectorID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public GarbageSecurity getGarbageSecurity() {
        return garbageSecurity;
    }

    public void setGarbageSecurity(GarbageSecurity garbageSecurity) {
        this.garbageSecurity = garbageSecurity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<GarbageBin> getGarbageBins() {
        return garbageBins;
    }

    public void setGarbageBins(List<GarbageBin> garbageBins) {
        this.garbageBins = garbageBins;
    }

    @Override
    public String toString() {
        return "GarbageCollector{" +
                "garbageCollectorID=" + garbageCollectorID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", garbageSecurity=" + garbageSecurity +
                ", email='" + email + '\'' +
                ", garbageBins=" + garbageBins +
                '}';
    }
}

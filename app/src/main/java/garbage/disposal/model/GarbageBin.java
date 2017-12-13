package garbage.disposal.model;

/**
 * Created by Brain on 03/01/2017.
 */

public class GarbageBin {

    private Integer qrID;
    private String qrCode;
    private Integer currentCapacity;
    private String location;
    public GarbageBin(){

    }

    public GarbageBin(Integer qrID, String qrCode, Integer currentCapacity, String location) {
        this.qrID = qrID;
        this.qrCode = qrCode;
        this.currentCapacity = currentCapacity;
        this.location = location;
    }

    public Integer getQrID() {
        return qrID;
    }

    public void setQrID(Integer qrID) {
        this.qrID = qrID;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(Integer currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "GarbageBin{" +
                "qrID=" + qrID +
                ", qrCode='" + qrCode + '\'' +
                ", currentCapacity=" + currentCapacity +
                ", location='" + location + '\'' +
                '}';
    }
}

import java.util.Objects;

public final class ZipInfo {
    private final String cityName;
    private final String state;
    private final String zipCode;
    private final String longitude;
    private final String latitude;

    public ZipInfo(String cityName, String state, String zipCode, String longitude, String latitude) {
        this.cityName = cityName;
        this.state = state;
        this.zipCode = zipCode;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String cityName() {
        return cityName;
    }

    public String state() {
        return state;
    }

    public String zipCode() {
        return zipCode;
    }

    public String longitude() {
        return longitude;
    }

    public String latitude() {
        return latitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ZipInfo) obj;
        return Objects.equals(this.cityName, that.cityName) &&
                Objects.equals(this.state, that.state) &&
                Objects.equals(this.zipCode, that.zipCode) &&
                Objects.equals(this.longitude, that.longitude) &&
                Objects.equals(this.latitude, that.latitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cityName, state, zipCode, longitude, latitude);
    }

    @Override
    public String toString() {
        return "ZipInfo[" +
                "cityName=" + cityName + ", " +
                "state=" + state + ", " +
                "zipCode=" + zipCode + ", " +
                "longitude=" + longitude + ", " +
                "latitude=" + latitude + ']';
    }

}
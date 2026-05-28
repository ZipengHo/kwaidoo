package basic.cmn.event;

public enum EventDefinitionStatus {
    激活("ACTIVE"),
    停用("INACTIVE");
    private final String value;
    EventDefinitionStatus(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}

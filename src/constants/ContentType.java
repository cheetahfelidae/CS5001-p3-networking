package constants;

public enum ContentType {
    TEXT_HTML("TEXT_HTML"),
    NONE("");

    private String type;

    ContentType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}

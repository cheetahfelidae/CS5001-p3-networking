package constants;

public enum ContentType {
    TEXT_HTML("text/html"),
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

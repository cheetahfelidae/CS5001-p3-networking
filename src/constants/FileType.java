package constants;

/**
 * This is a set of predefined constants of the resource extensions for the HTML content-type field.
 */
public enum FileType {
    /**
     * HTML format.
     */
    HTML("html"),

    /**
     * GIF image format.
     */
    GIF("gif"),

    /**
     * JPEG image format.
     */
    JPEG("jpg"),

    /**
     * PNG image format.
     */
    PNG("png"),

    /**
     * used in convert() when there is nothing to be return.
     */
    NONE("");

    private String type;

    FileType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    /**
     * To be able to handle with switch case, this method converts a type of the string from String to Enum.
     *
     * @param code string-type value.
     * @return Enum-type value.
     */
    public static FileType convert(String code) {
        for (FileType e : FileType.values()) {
            if (e.toString().equals(code)) {
                return e;
            }
        }
        return NONE;
    }

}

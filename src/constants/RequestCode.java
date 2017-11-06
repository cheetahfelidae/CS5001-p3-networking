package constants;

/**
 * This is a set of predefined constants of the HTTP request codes.
 */
public enum RequestCode {
    /**
     * GET code.
     */
    GET("GET"),
    /**
     * HEAD code.
     */
    HEAD("HEAD"),
    /**
     * DELETE code.
     */
    DELETE("DELETE"),
    /**
     * used in convert() when there is nothing to be return.
     */
    NONE("");

    private String code;

    RequestCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    /**
     * To be able to handle with switch case, this method converts a type of the string from String to Enum.
     *
     * @param code string-type value.
     * @return Enum-type value.
     */
    public static RequestCode convert(String code) {
        for (RequestCode e : RequestCode.values()) {
            if (e.toString().equals(code)) {
                return e;
            }
        }
        return NONE;
    }
}

package constants;

public enum RequestCode {
    GET("GET"),
    HEAD("HEAD"),
    DELETE("DELETE"),
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
     * To be able to handle with switch case
     *
     * @param code
     * @return
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
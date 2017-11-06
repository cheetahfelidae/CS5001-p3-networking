package constants;

public enum ResponseCode {
    /**
     *
     */
    WORKING_OKAY("200 OK"),
    NOT_FOUND("404 Not Found"),
    NOT_IMPLEMENTED("501 Not Implemented");

    private String code;

    ResponseCode(String code) {
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
    public static ResponseCode convert(String code) {
        for (ResponseCode e : ResponseCode.values()) {
            if (e.toString().equals(code)) {
                return e;
            }
        }
        throw new RuntimeException("RESPONSE CODE IS NOT FOUND");
    }
}
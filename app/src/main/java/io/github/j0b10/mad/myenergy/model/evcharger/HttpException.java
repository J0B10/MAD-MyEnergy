package io.github.j0b10.mad.myenergy.model.evcharger;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Map;

public class HttpException extends IOException {

    /**
     * HTTP Status Codes as referenced in {@code RFC9110}.
     * <p>
     * See <a href="https://www.iana.org/assignments/http-status-codes/http-status-codes.txt">http-status-codes.txt</a>
     * </p>
     */
    private static final Map<Integer, String> STATUS_CODE_DESCRIPTIONS = Map.ofEntries(
            Map.entry(400, "Bad Request"),
            Map.entry(401, "Unauthorized"),
            Map.entry(402, "Payment Required"),
            Map.entry(403, "Forbidden"),
            Map.entry(404, "Not Found"),
            Map.entry(405, "Method Not Allowed"),
            Map.entry(406, "Not Acceptable"),
            Map.entry(407, "Proxy Authentication Required"),
            Map.entry(408, "Request Timeout"),
            Map.entry(409, "Conflict"),
            Map.entry(410, "Gone"),
            Map.entry(411, "Length Required"),
            Map.entry(412, "Precondition Failed"),
            Map.entry(413, "Content Too Large"),
            Map.entry(414, "URI Too Long"),
            Map.entry(415, "Unsupported Media Type"),
            Map.entry(416, "Range Not Satisfiable"),
            Map.entry(417, "Expectation Failed"),
            Map.entry(418, "I'm a teapot"),
            Map.entry(421, "Misdirected Request"),
            Map.entry(422, "Unprocessable Content"),
            Map.entry(423, "Locked"),
            Map.entry(424, "Failed Dependency"),
            Map.entry(425, "Too Early"),
            Map.entry(426, "Upgrade Required"),
            Map.entry(428, "Precondition Required"),
            Map.entry(429, "Too Many Requests"),
            Map.entry(431, "Request Header Fields Too Large"),
            Map.entry(451, "Unavailable For Legal Reasons"),

            Map.entry(500, "Internal Server Error"),
            Map.entry(501, "Not Implemented"),
            Map.entry(502, "Bad Gateway"),
            Map.entry(503, "Service Unavailable"),
            Map.entry(504, "Gateway Timeout"),
            Map.entry(505, "HTTP Version Not Supported"),
            Map.entry(506, "Variant Also Negotiates"),
            Map.entry(507, "Insufficient Storage"),
            Map.entry(508, "Loop Detected"),
            Map.entry(510, "Not Extended"),
            Map.entry(511, "Network Authentication Required")
    );

    private final int statusCode;

    public HttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpException(int statusCode) {
        this(statusCode, STATUS_CODE_DESCRIPTIONS.get(statusCode));
    }

    public int code() {
        return statusCode;
    }
}

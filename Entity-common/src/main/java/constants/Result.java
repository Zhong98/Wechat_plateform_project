package constants;


import lombok.Data;

@Data
//@AllArgsConstructor
public class Result {

    private int code;

    private String message;

    private Object data;

    public Result(int code) {
        this.code = code;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}

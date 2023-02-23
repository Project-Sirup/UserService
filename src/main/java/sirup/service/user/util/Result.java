package sirup.service.user.util;

public class Result<E, S> {
    private final E error;
    private final S success;

    private Result(E error, S success) {
        this.error = error;
        this.success = success;
    }

    public static <E,S> Result<E,S> error(E error) {
        return new Result<E,S>(error, null);
    }

    public static <E,S> Result<E,S> success(S success) {
        return new Result<E,S>(null, success);
    }

    public boolean failed() {
        return error != null;
    }

    public boolean succeeded() {
        return success != null;
    }

    public S getOr(S s) {
        return this.succeeded() ? success : s;
    }

    public S get() {
        return this.success;
    }

    public E error() {
        return this.error;
    }
}

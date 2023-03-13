package sirup.service.user.interfaces;

public interface Service<T> {
    boolean add(T t);
    T get(String id);
    T getBy(String column, String id);
    boolean update(T t);
    boolean delete(String id);
}

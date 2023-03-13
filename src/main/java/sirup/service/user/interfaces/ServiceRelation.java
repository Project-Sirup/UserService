package sirup.service.user.interfaces;

public interface ServiceRelation<A extends DTO,B extends DTO, C> {
    boolean add(A a, B b, C c);
    C get(A a, B b);
    boolean update(A a, B b, C c);
    boolean delete(A a, B b, C c);
}

package sirup.service.user.interfaces;

import java.util.List;

public interface ServiceRelation<A extends DTO,B extends DTO, C> {
    boolean add(A a, B b, C c);
    List<C> getAll(B b);
    C get(A a, B b);
    boolean update(A a, B b, C c);
    boolean delete(A a, B b, C c);
}

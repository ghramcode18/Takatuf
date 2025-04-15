package geekcode.takatuf.Repository;

import geekcode.takatuf.Entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByName(String name);

    List<Store> findByOwner_Id(Long ownerId);
    

}

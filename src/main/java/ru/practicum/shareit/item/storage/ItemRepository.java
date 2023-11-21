package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    String query = " select i " +
            "from Item i " +
            "where lower(i.name) like lower(concat('%', :search, '%')) " +
            "or lower(i.description) like lower(concat('%', :search, '%')) " +
            "and i.available = true";

    List<Item> getByOwnerId(Long ownerId);

    @Query(query)
    List<Item> getItemsByQuery(@Param("search") String text);

    default Item getByIdAndCheck(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Не найдена вещь с ид: " + id));
    }
}

package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    String query = "SELECT i FROM Item i " +
            "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            " OR LOWER(i.description) LIKE LOWER(concat('%', :search, '%')) " +
            " AND i.available = true";

    @Query("SELECT i FROM Item i JOIN FETCH i.owner WHERE i.owner.id = :userId ORDER BY i.id")
    List<Item> getByOwnerId(@Param("userId") Long ownerId);

    @Query(query)
    List<Item> getItemsByQuery(@Param("search") String text);

    default Item getByIdAndCheck(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Не найдена вещь с ид: " + id));
    }

    List<Item> getItemsByRequestIn(List<ItemRequest> itemRequests);

    List<Item> getItemsByRequest(ItemRequest itemRequest);

}

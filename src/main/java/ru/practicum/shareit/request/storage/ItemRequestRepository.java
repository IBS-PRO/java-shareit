package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    default ItemRequest getByIdAndCheck(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Не найден запрос с id " + id));
    }

    List<ItemRequest> getAllByRequestId(Long requestId);

    List<ItemRequest> findRequestsByRequestIdNotOrderByCreatedDesc(Long userId, Pageable pageable);

}

package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase
class RequestRepositoryTest {

    User createdUser1;
    User createdUser2;
    Item createdItem1;
    Item createdItem2;
    ItemRequest createdRequest1;
    ItemRequest createdRequest2;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void beforeEach() {
        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@mail.com");
        createdUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@mail.com");
        createdUser2 = userRepository.save(user2);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("testDescription1");
        request1.setRequestId(user1.getId());
        request1.setCreated(LocalDateTime.now().minusDays(5));
        createdRequest1 = itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("testDescription1");
        request2.setRequestId(user1.getId());
        request2.setCreated(LocalDateTime.now().minusDays(2));
        createdRequest2 = itemRequestRepository.save(request2);

        Item item1 = new Item();
        item1.setName("testItem");
        item1.setDescription("testItemDescription");
        item1.setAvailable(true);
        item1.setOwner(createdUser1);
        item1.setRequest(createdRequest1);
        createdItem1 = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("testItem2");
        item2.setDescription("testItem2Description");
        item2.setAvailable(true);
        item2.setOwner(createdUser2);
        item2.setRequest(createdRequest2);
        createdItem2 = itemRepository.save(item2);
    }

    @Test
    void getByIdAndCheck_IsRequestValid() {
        System.out.println(createdRequest1);
        System.out.println(createdRequest1);
        assertEquals(createdRequest1, itemRequestRepository.getByIdAndCheck(createdRequest1.getId()));
    }

    @Test
    void getByIdAndCheck_IsItemInvalid() {
        assertThrows(NotFoundException.class, () -> itemRequestRepository.getByIdAndCheck(999L));
    }

    @Test
    void getAllByRequestorId() {
        assertEquals(List.of(createdRequest1, createdRequest2), itemRequestRepository.getAllByRequestId(createdUser1.getId()));
    }

    @Test
    void findRequestsByRequestorIdNotOrderByCreatedDesc() {
        assertEquals(List.of(createdRequest2, createdRequest1), itemRequestRepository.findRequestsByRequestIdNotOrderByCreatedDesc(createdUser2.getId(), PageRequest.of(0, 10)));
    }
}

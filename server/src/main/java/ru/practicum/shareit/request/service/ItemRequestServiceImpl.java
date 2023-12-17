package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemResponseDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        userRepository.checkUser(userId);
        itemRequestDto.setRequestId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.requestDtoToRequest(itemRequestDto));
        return ItemRequestMapper.requestToRequestReturnDto(itemRequest);
    }

    @Override
    public ItemResponseDto getItemRequest(Long requestId, Long userId) {
        userRepository.checkUser(userId);
        ItemRequest itemRequest = itemRequestRepository.getByIdAndCheck(requestId);
        List<Item> items = itemRepository.getItemsByRequest(itemRequest);
        ItemResponseDto itemResponseDto = ItemRequestMapper.requestToRequestReturnDto(itemRequest);
        itemResponseDto.setItems(ItemMapper.itemlistToitemForRequestDtolist(items));
        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> getRequestorRequest(long requestId) {
        userRepository.checkUser(requestId);
        List<ItemRequest> itemRequests = itemRequestRepository.getAllByRequestId(requestId);
        return getRequestReturnDtoWithItems(itemRequests);
    }

    @Override
    public List<ItemResponseDto> getItemRequests(long userId, int from, int size) {
        userRepository.checkUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findRequestsByRequestIdNotOrderByCreatedDesc(userId, PageRequest.of(from, size));
        return getRequestReturnDtoWithItems(requests);
    }

    @Override
    public void deleteItemRequest(Long requestId, Long userId) {
        userRepository.checkUser(userId);
        itemRequestRepository.deleteById(requestId);
    }

    private List<ItemResponseDto> getRequestReturnDtoWithItems(List<ItemRequest> requests) {
        List<ItemResponseDto> requestsReturn = ItemRequestMapper.requestListToRequestReturnDtoList(requests);
        List<Item> itemsWithRequestsId = itemRepository.getItemsByRequestIn(requests);
        for (ItemResponseDto request : requestsReturn) {
            for (Item item : itemsWithRequestsId) {
                if (request.getId().equals(item.getRequest().getId())) {
                    request.getItems().add(ItemMapper.itemToItemShortForRequestDto(item));
                }
            }
        }
        return requestsReturn;
    }

}

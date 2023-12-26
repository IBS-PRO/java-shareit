package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemResponseDto addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    ItemResponseDto getItemRequest(Long requestId, Long userId);

    List<ItemResponseDto> getRequestorRequest(long requestorId);

    List<ItemResponseDto> getItemRequests(long userId, int from, int size);

    void deleteItemRequest(Long requestId, Long userId);

}

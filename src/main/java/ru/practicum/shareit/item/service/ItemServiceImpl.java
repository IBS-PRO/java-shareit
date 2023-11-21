package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(long ownerId, ItemDtoRequest itemDtoRequest) {
        User user = userRepository.checkUser(ownerId);
        Item item = ItemMapper.toItem(itemDtoRequest, user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long id, Long userId) {
        Item item = itemRepository.getByIdAndCheck(id);
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.getByIdAndCheck(id));
        itemDto.setComments(commentRepository.getAllByItemId(id));
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime currentTime = LocalDateTime.now();
            itemDto.setLastBooking(getLastBooking(id, currentTime));
            itemDto.setNextBooking(getNextBooking(id, currentTime));
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllOwnerItems(Long ownerId) {
        User owner = userRepository.checkUser(ownerId);
        List<Item> items = itemRepository.getByOwnerId(owner.getId());
        return getItemsDtoWithLastAndNextBookings(items);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDtoRequest itemDtoRequest, Long ownerId) {
        Item oldItem = itemRepository.getByIdAndCheck(itemId);
        User owner = userRepository.checkUser(ownerId);
        Item item = ItemMapper.toItem(itemDtoRequest, owner);
        if (item.getOwner() == null) {
            item.setOwner(owner);
        }
        if (!oldItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Неверный пользователь");
        }
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        item.setId(itemId);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));

    }

    @Override
    public List<ItemDto> searchAvailableItem(String text) {
        return text.isBlank() ?
                new ArrayList<>() :
                ItemMapper.itemlistToitemdtolist(itemRepository.getItemsByQuery(text));
    }

    @Override
    public CommentDto addComment(Long itemId, CommentDto commentDto, long userId) {
        Item item = itemRepository.getByIdAndCheck(itemId);
        User user = userRepository.checkUser(userId);
        LocalDateTime timeOfCreation = LocalDateTime.now();
        commentDto.setCreated(timeOfCreation);
        List<Booking> bookings = bookingRepository.getAllByRenterIdAndSubject_IdAndStatusAndEndDateIsBefore(
                userId,
                itemId,
                BookingStatus.APPROVED,
                timeOfCreation);
        if (!bookings.isEmpty()) {
            Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, item));
            return CommentMapper.toCommentDto(comment);
        } else {
            throw new ValidationException("Пользователь с id " + userId + " не имеет бронирования с ид " + item.getId());
        }
    }

    public List<ItemDto> getItemsDtoWithLastAndNextBookings(List<Item> items) {
        List<Booking> lastBookings = bookingRepository
                .getAllByEndDateBeforeAndStatusAndSubjectInOrderByStartDateDesc(LocalDateTime.now(), BookingStatus.APPROVED, items);
        List<Booking> nextBookings = bookingRepository
                .getAllByStartDateAfterAndStatusAndSubjectInOrderByStartDateDesc(LocalDateTime.now(), BookingStatus.APPROVED, items);
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            for (Booking lastBooking : lastBookings) {
                if (lastBooking.getSubject().getId() == itemDto.getId()) {
                    itemDto.setLastBooking(lastBooking);
                }
            }
            for (Booking nextBooking : nextBookings) {
                if (nextBooking.getSubject().getId() == itemDto.getId()) {
                    itemDto.setNextBooking(nextBooking);
                }
            }
            result.add(itemDto);
        }
        return result;
    }

    public Booking getLastBooking(Long itemId, LocalDateTime currentTime) {
        return bookingRepository
                .getFirstBySubjectIdAndStatusAndStartDateIsBeforeOrderByStartDateDesc(itemId, BookingStatus.APPROVED, currentTime);
    }

    public Booking getNextBooking(Long itemId, LocalDateTime currentTime) {
        return bookingRepository
                .getFirstBySubjectIdAndStatusAndStartDateIsAfterOrderByStartDateAsc(itemId, BookingStatus.APPROVED, currentTime);
    }

}

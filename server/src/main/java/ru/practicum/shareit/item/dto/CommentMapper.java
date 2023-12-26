package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment не может быть null");
        }
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getUser().getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .userId(comment.getUser().getId())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, User user, Item item) {
        if (commentDto == null || user == null || item == null) {
            throw new IllegalArgumentException("ItemDto|User|Item не может быть null");
        }
        return Comment.builder()
                .created(commentDto.getCreated())
                .text(commentDto.getText())
                .item(item)
                .user(user)
                .build();
    }

}
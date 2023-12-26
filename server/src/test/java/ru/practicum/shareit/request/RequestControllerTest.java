package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ItemRequestController.class)
class RequestControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService requestServiceMock;

    @SneakyThrows
    @Test
    void getItemRequest_requestIsInCorrect_returnNotfoundException() {
        Long requestId = 1L;
        Long userId = 1L;
        when(requestServiceMock.getItemRequest(requestId, userId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        verify(requestServiceMock).getItemRequest(requestId, userId);
    }

    @SneakyThrows
    @Test
    void getItemRequest_userIsCorrect_returnRequestList() {
        ItemResponseDto expected = new ItemResponseDto();
        expected.setId(1L);
        expected.setDescription("description");
        expected.setCreated(LocalDateTime.now());
        when(requestServiceMock.getRequestorRequest(anyLong())).thenReturn(List.of(expected));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expected))));
        verify(requestServiceMock).getRequestorRequest(1L);
    }

    @SneakyThrows
    @Test
    void getItemRequests() {
        ItemResponseDto expected = new ItemResponseDto();
        expected.setId(1L);
        expected.setDescription("description");
        expected.setCreated(LocalDateTime.now());
        when(requestServiceMock.getItemRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(expected));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expected))));
        verify(requestServiceMock).getItemRequests(1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void addItemRequest_requestIsValid_returnRequest() {
        ItemResponseDto expected = new ItemResponseDto();
        expected.setId(1L);
        expected.setDescription("description");
        expected.setCreated(LocalDateTime.now());

        when(requestServiceMock.addItemRequest(any(), any())).thenReturn(expected);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected)))

                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                .andReturn().getResponse().getContentAsString();
        assertEquals(result, objectMapper.writeValueAsString(expected));
        verify(requestServiceMock).addItemRequest(any(), any());
    }

    @SneakyThrows
    @Test
    void addItemRequest_whenRequestIsInvalid_ValidationException() {
        ItemResponseDto expected = new ItemResponseDto();
        expected.setId(1L);
        expected.setDescription(null);
        expected.setCreated(LocalDateTime.now());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected)))
                .andExpect(status().isBadRequest());
        verify(requestServiceMock, never()).addItemRequest(any(), any());
    }
}

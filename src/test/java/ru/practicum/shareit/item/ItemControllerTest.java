package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(null, "пользователь1", "mail@mail.com");

    private final Item item = new Item(1L, "вещь1", "описание вещи 1", true, userDto.getId(),
            null);
    private final ItemDto itemDto = new ItemDto(1L, "вещь1", "описание вещи 1", true,
            userDto.getId(), null);
    private final CommentDto commentDto = new CommentDto(1L, "комментарий1", item, userDto.getName(),
            LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1));
    private final ItemWithBookingInfoDto itemWithBookingInfoDto = new ItemWithBookingInfoDto(1L, "вещь1",
            "описание вещи 1", true, userDto.getId(), null, null, null,
            null);

    private static final String USER_ID = "X-Sharer-User-Id";
    private final List<ItemWithBookingInfoDto> itemList = new ArrayList<>();

    @Test
    @DisplayName("Тест создания вещи")
    void createTest() throws Exception {
        when(itemService.create(any(), any(Long.class)))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @DisplayName("Тест обновления вещи")
    void updateTest() throws Exception {
        when(itemService.update(any(), any(Long.class), any(Long.class)))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @DisplayName("Тест получения вещи")
    void getItemTest() throws Exception {
        when(itemService.getItem(any(Long.class), any(Long.class)))
                .thenReturn(itemWithBookingInfoDto);
        mvc.perform(get("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @DisplayName("Тест получения вещей владельцем")
    void getItemsByOwnerTest() throws Exception {
        when(itemService.getItemsByOwner(any(Long.class), any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(itemWithBookingInfoDto));
        mvc.perform(get("/items")
                        .content(objectMapper.writeValueAsString(itemList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @DisplayName("Тест поиска вещей")
    void searchItemTest() throws Exception {
        when(itemService.searchItem(any(String.class), any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search?text=description")
                        .content(objectMapper.writeValueAsString(itemList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @DisplayName("Тест создания комментария")
    void createComment() throws Exception {
        when(itemService.createComment(any(), any(Long.class), any(Long.class)))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}

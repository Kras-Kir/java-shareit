package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.ItemService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem() throws Exception {
        // given
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Аккумуляторная дрель");
        itemDto.setAvailable(true);

        ItemDto createdItem = new ItemDto();
        createdItem.setId(1L);
        createdItem.setName("Дрель");
        createdItem.setDescription("Аккумуляторная дрель");
        createdItem.setAvailable(true);

        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenReturn(createdItem);

        // when & then
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getUserItems() throws Exception {
        // given
        ItemResponseDto item1 = new ItemResponseDto();
        item1.setId(1L);
        item1.setName("Дрель");
        item1.setAvailable(true);

        ItemResponseDto item2 = new ItemResponseDto();
        item2.setId(2L);
        item2.setName("Молоток");
        item2.setAvailable(true);

        when(itemService.getAllItemsByOwner(anyLong())).thenReturn(Arrays.asList(item1, item2));

        // when & then
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Молоток"));
    }

    @Test
    void getItem() throws Exception {
        // given
        ItemResponseDto itemResponse = new ItemResponseDto();
        itemResponse.setId(1L);
        itemResponse.setName("Дрель");
        itemResponse.setAvailable(true);

        when(itemService.getItemByIdWithBookingsAndComments(anyLong(), anyLong())).thenReturn(itemResponse);

        // when & then
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void searchItems() throws Exception {
        // given
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setAvailable(true);

        when(itemService.searchItems(anyString())).thenReturn(Collections.singletonList(itemDto));

        // when & then
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }
}
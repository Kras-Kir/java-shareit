package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestDtoTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    void testSerialization() throws Exception {
        // given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 12, 0);

        BookingRequestDto dto = BookingRequestDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        // when
        JsonContent<BookingRequestDto> result = json.write(dto);

        // then
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-01-01T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
    }

    @Test
    void testDeserialization() throws Exception {
        // given
        String content = "{\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-01T12:00:00\",\"itemId\":1}";

        // when
        BookingRequestDto dto = json.parse(content).getObject();

        // then
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 1, 12, 0));
        assertThat(dto.getItemId()).isEqualTo(1L);
    }

    @Test
    void testValidDates() throws Exception {
        // given - valid case (end after start)
        String validContent = "{\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-01T12:00:00\",\"itemId\":1}";

        // when
        BookingRequestDto validDto = json.parse(validContent).getObject();

        // then
        assertThat(validDto.getEnd()).isAfter(validDto.getStart());
    }

    @Test
    void testInvalidDates() throws Exception {
        // given - invalid case (end before start)
        String invalidContent = "{\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-01T09:00:00\",\"itemId\":1}";

        // when
        BookingRequestDto invalidDto = json.parse(invalidContent).getObject();

        // then
        assertThat(invalidDto.getEnd()).isBefore(invalidDto.getStart());
    }
}
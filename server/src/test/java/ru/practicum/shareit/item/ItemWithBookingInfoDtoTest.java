package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemWithBookingInfoDtoTest {

    private final JacksonTester<ItemWithBookingInfoDto> json;
    private ItemWithBookingInfoDto itemWithBookingInfoDto;

    public ItemWithBookingInfoDtoTest(@Autowired JacksonTester<ItemWithBookingInfoDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        itemWithBookingInfoDto = new ItemWithBookingInfoDto(1L, "вещь", "описание", true,
            1L, null, new ShortBookingDto(1L, LocalDateTime
                .of(2023, 9, 1, 10, 30, 5, 1),
                LocalDateTime.of(2023, 9, 7, 10, 30, 5, 1),
                2L, 1L, Status.APPROVED), null, new ArrayList<>());

    }

    @Test
    void testJsonBookingDto() throws Exception {
        JsonContent<ItemWithBookingInfoDto> result = json.write(itemWithBookingInfoDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("вещь");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("описание");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo("2023-09-01T10:30:05.000000001");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo("2023-09-07T10:30:05.000000001");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo("APPROVED");
    }
}

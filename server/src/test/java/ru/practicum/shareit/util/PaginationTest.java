package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.paginationException.SizePaginationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationTest {

    @Test
    public void whenIncorrectSizeThenSizePaginationException() {
        assertThrows(SizePaginationException.class, () -> new Pagination(0, 0));
        assertThrows(SizePaginationException.class, () -> new Pagination(0, -1));
        assertThrows(SizePaginationException.class, () -> new Pagination(-1, 1));
    }

    @Test
    public void whenSizeLessFromThenResult() {
        Integer from = 10;
        Integer size = 2;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(10);
        assertThat(pager.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void whenSizeMoreFromThenResult() {
        Integer from = 2;
        Integer size = 5;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(2);
        assertThat(pager.getTotalPages()).isEqualTo(4);
    }

    @Test
    public void whenSizeEqualsFromThenResult() {
        Integer from = 5;
        Integer size = 5;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(5);
        assertThat(pager.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void whenSizeEqualsNotNullAndFromIsZeroThenResult() {
        Integer from = 0;
        Integer size = 5;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(0);
        assertThat(pager.getPageSize()).isEqualTo(5);
        assertThat(pager.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void whenFromEqualsZeroAndSizeNullThenResult() {
        Integer from = 0;
        Integer size = null;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(0);
        assertThat(pager.getPageSize()).isEqualTo(1000);
        assertThat(pager.getTotalPages()).isEqualTo(0);
    }
}

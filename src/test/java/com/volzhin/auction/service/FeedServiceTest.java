package com.volzhin.auction.service;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.entity.Category;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.entity.lot.LotCache;
import com.volzhin.auction.mapper.LotMapper;
import com.volzhin.auction.service.cache.LotCacheService;
import com.volzhin.auction.service.image.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private LotCacheService lotCacheService;
    @Mock
    private LotService lotService;
    @Mock
    private LotMapper lotMapper;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private FeedService feedService;

    @Test
    void getLotFeed_shouldReturnSortedFromCacheWithImages_whenCacheSufficientAndNoCategory() {
        // Arrange
        int page = 0;
        int size = 2;
        LocalDateTime now = LocalDateTime.now();

        // Данные из кеша (неотсортированные)
        LotCache cacheLot1 = LotCache.builder().id(1L).title("Cache Lot 1").endTime(now.plusHours(2)).build();
        LotCache cacheLot2 = LotCache.builder().id(2L).title("Cache Lot 2").endTime(now.plusHours(1)).build(); // Раньше заканчивается
        LotCache cacheLot3 = LotCache.builder().id(3L).title("Cache Lot 3").endTime(now.plusHours(3)).build();
        List<LotCache> cachedLotsFromService = Arrays.asList(cacheLot1, cacheLot2, cacheLot3);

        // Соответствующие DTO после маппинга (порядок может быть сохранен маппером)
        LotDto dto1 = LotDto.builder().id(1L).title("Cache Lot 1").endTime(now.plusHours(2)).build();
        LotDto dto2 = LotDto.builder().id(2L).title("Cache Lot 2").endTime(now.plusHours(1)).build();
        LotDto dto3 = LotDto.builder().id(3L).title("Cache Lot 3").endTime(now.plusHours(3)).build();
        List<LotDto> mappedDtosFromCache = Arrays.asList(dto1, dto2, dto3);

        // URL изображений
        List<String> urlsForLot1 = List.of("url1.jpg");
        List<String> urlsForLot2 = List.of("url2.png", "url2_thumb.png");
        // List<String> urlsForLot3 = Collections.emptyList();

        when(lotCacheService.getCacheLots()).thenReturn(cachedLotsFromService);
        when(lotMapper.toDtos(cachedLotsFromService)).thenReturn(mappedDtosFromCache);
        // Мокируем получение URL для лотов, которые попадут в результат (после сортировки)
        when(imageService.getImageUrlsByLotId(1L)).thenReturn(urlsForLot1);
        when(imageService.getImageUrlsByLotId(2L)).thenReturn(urlsForLot2);
        // when(imageService.getImageUrlsByLotId(3L)).thenReturn(urlsForLot3); // Можно не мокировать, т.к. лот 3 не попадет в результат page 0 size 2

        // Act: Запрашиваем первую страницу (page=0) размером 2 (size=2) без категории
        List<LotDto> result = feedService.getLotFeed(page, size, null);

        // Assert
        assertEquals(2, result.size(), "Should return 'size' elements");
        // Проверяем сортировку по endTime (lot2 должен быть первым)
        assertEquals(2L, result.get(0).getId(), "Lot 2 should be first (earlier end time)");
        assertEquals(1L, result.get(1).getId(), "Lot 1 should be second");

        // Проверяем, что изображения добавлены к правильным DTO
        assertEquals(urlsForLot2, result.get(0).getImageUrls(), "Lot 2 DTO should have its images");
        assertEquals(urlsForLot1, result.get(1).getImageUrls(), "Lot 1 DTO should have its images");

        verify(lotCacheService).getCacheLots();
        verify(lotMapper).toDtos(cachedLotsFromService);
        verify(imageService).getImageUrlsByLotId(1L); // Вызывается для обогащения DTO
        verify(imageService).getImageUrlsByLotId(2L); // Вызывается для обогащения DTO
        // verify(imageService, never()).getImageUrlsByLotId(3L); // Не должен вызываться, т.к. лот 3 не в результате
        verifyNoInteractions(lotService); // База данных не должна быть затронута
        verify(lotCacheService, never()).getCacheLotsByCategoryName(anyString());
    }

    @Test
    void getLotFeed_shouldReturnSortedFromDbWithImages_whenCacheIsEmpty() {
        int page = 0;
        int size = 2;
        LocalDateTime now = LocalDateTime.now();

        when(lotCacheService.getCacheLots()).thenReturn(Collections.emptyList());
        when(lotMapper.toDtos(Collections.emptyList())).thenReturn(Collections.emptyList());

        Lot dbLot1 = Lot.builder().id(10L).title("DB Lot 1").endTime(now.plusDays(1)).build();
        Lot dbLot2 = Lot.builder().id(11L).title("DB Lot 2").endTime(now.plusHours(5)).build();
        List<Lot> lotsFromDb = Arrays.asList(dbLot1, dbLot2);

        LotDto dto10 = LotDto.builder().id(10L).title("DB Lot 1").endTime(now.plusDays(1)).build();
        LotDto dto11 = LotDto.builder().id(11L).title("DB Lot 2").endTime(now.plusHours(5)).build();
        List<LotDto> mappedDtosFromDb = Arrays.asList(dto10, dto11);

        List<String> urlsForLot10 = List.of("db_url10.gif");
        List<String> urlsForLot11 = List.of();

        when(lotService.getLots(page, size, null)).thenReturn(lotsFromDb);
        when(lotMapper.toDto(lotsFromDb)).thenReturn(mappedDtosFromDb);
        when(imageService.getImageUrlsByLotId(10L)).thenReturn(urlsForLot10);
        when(imageService.getImageUrlsByLotId(11L)).thenReturn(urlsForLot11);

        List<LotDto> result = feedService.getLotFeed(page, size, null);

        assertEquals(2, result.size());
        assertEquals(11L, result.get(0).getId());
        assertEquals(10L, result.get(1).getId());
        assertEquals(urlsForLot11, result.get(0).getImageUrls());
        assertEquals(urlsForLot10, result.get(1).getImageUrls());

        verify(lotCacheService).getCacheLots();
        verify(lotMapper).toDtos(Collections.emptyList());
        verify(lotService).getLots(page, size, null);
        verify(lotMapper).toDto(lotsFromDb);
        verify(imageService).getImageUrlsByLotId(10L);
        verify(imageService).getImageUrlsByLotId(11L);
        verifyNoMoreInteractions(lotCacheService, lotService, lotMapper, imageService);
    }

    @Test
    void getLotFeed_shouldReturnFromDb_whenCacheNotSufficientForPage() {
        // Arrange
        int page = 1; // Запрашиваем вторую страницу
        int size = 1;
        int startIndex = page * size; // 1
        int endIndex = startIndex + size; // 2
        LocalDateTime now = LocalDateTime.now();

        // В кеше есть только один лот. Нам нужен элемент с индексом 1.
        LotCache cacheLot1 = LotCache.builder().id(1L).title("Cache Lot 1").endTime(now.plusHours(1)).build();
        List<LotCache> cachedLotsFromService = List.of(cacheLot1);

        // DTO из кеша (нужно для проверки условия)
        LotDto dto1 = LotDto.builder().id(1L).title("Cache Lot 1").endTime(now.plusHours(1)).build();
        List<LotDto> mappedDtosFromCache = List.of(dto1);

        // Данные из БД для page=1, size=1
        Lot dbLot2 = Lot.builder().id(2L).title("DB Lot 2").endTime(now.plusHours(2)).build();
        List<Lot> lotsFromDb = List.of(dbLot2);

        // DTO из БД
        LotDto dto2 = LotDto.builder().id(2L).title("DB Lot 2").endTime(now.plusHours(2)).build();
        List<LotDto> mappedDtosFromDb = List.of(dto2);

        List<String> urlsForLot2 = List.of("db_url2.jpg");

        when(lotCacheService.getCacheLots()).thenReturn(cachedLotsFromService);
        // Важно: маппер для кеша все равно вызывается перед проверкой размера
        when(lotMapper.toDtos(cachedLotsFromService)).thenReturn(mappedDtosFromCache);
        // Так как cachedLotCount (1) < endIndex (2), будет вызван lotService
        when(lotService.getLots(page, size, null)).thenReturn(lotsFromDb);
        when(lotMapper.toDto(lotsFromDb)).thenReturn(mappedDtosFromDb);
        when(imageService.getImageUrlsByLotId(2L)).thenReturn(urlsForLot2);

        // Act
        List<LotDto> result = feedService.getLotFeed(page, size, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(urlsForLot2, result.get(0).getImageUrls());

        verify(lotCacheService).getCacheLots();
        verify(lotMapper).toDtos(cachedLotsFromService); // Вызывается
        verify(lotService).getLots(page, size, null);
        verify(lotMapper).toDto(lotsFromDb);
        verify(imageService).getImageUrlsByLotId(2L);
        // Убедимся, что для кешированного лота URL не запрашивался (т.к. он не в результате)
        verify(imageService, never()).getImageUrlsByLotId(1L);
    }

    @Test
    void getLotFeed_shouldUseCategoryCacheAndFallbackToDb() {
        // Arrange
        int page = 0;
        int size = 2;
        String categoryName = "Electronics";
        LocalDateTime now = LocalDateTime.now();

        // В кеше по категории только один лот
        LotCache cacheLotElec = LotCache.builder().id(5L).categoryName(categoryName).endTime(now.plusHours(1)).build();
        List<LotCache> cachedLotsFromService = List.of(cacheLotElec);
        LotDto dto5 = LotDto.builder().id(5L).categoryName(categoryName).endTime(now.plusHours(1)).build();
        List<LotDto> mappedDtosFromCache = List.of(dto5);


        // Данные из БД для page=0, size=2, category=Electronics
        Lot dbLotElec1 = Lot.builder().id(6L).category(Category.builder().name(categoryName).build()).endTime(now.plusHours(3)).build();
        Lot dbLotElec2 = Lot.builder().id(7L).category(Category.builder().name(categoryName).build()).endTime(now.plusHours(2)).build();
        List<Lot> lotsFromDb = Arrays.asList(dbLotElec1, dbLotElec2);

        // DTO из БД
        LotDto dto6 = LotDto.builder().id(6L).categoryName(categoryName).endTime(now.plusHours(3)).build();
        LotDto dto7 = LotDto.builder().id(7L).categoryName(categoryName).endTime(now.plusHours(2)).build();
        List<LotDto> mappedDtosFromDb = Arrays.asList(dto6, dto7);

        List<String> urlsForLot6 = List.of("url6.png");
        List<String> urlsForLot7 = List.of("url7.jpg");

        when(lotCacheService.getCacheLotsByCategoryName(categoryName)).thenReturn(cachedLotsFromService);
        when(lotMapper.toDtos(cachedLotsFromService)).thenReturn(mappedDtosFromCache); // Вызывается
        // Условие cachedLotCount (1) < endIndex (2) истинно, идем в БД
        when(lotService.getLots(page, size, categoryName)).thenReturn(lotsFromDb);
        when(lotMapper.toDto(lotsFromDb)).thenReturn(mappedDtosFromDb);
        when(imageService.getImageUrlsByLotId(6L)).thenReturn(urlsForLot6);
        when(imageService.getImageUrlsByLotId(7L)).thenReturn(urlsForLot7);


        // Act
        List<LotDto> result = feedService.getLotFeed(page, size, categoryName);

        // Assert
        // Результат должен быть из БД, т.к. кеша не хватило для первой страницы
        assertEquals(2, result.size());
        assertEquals(7L, result.get(0).getId()); // Сортировка
        assertEquals(6L, result.get(1).getId());
        assertEquals(urlsForLot7, result.get(0).getImageUrls());
        assertEquals(urlsForLot6, result.get(1).getImageUrls());

        verify(lotCacheService).getCacheLotsByCategoryName(categoryName);
        verify(lotMapper).toDtos(cachedLotsFromService);
        verify(lotService).getLots(page, size, categoryName);
        verify(lotMapper).toDto(lotsFromDb);
        verify(imageService).getImageUrlsByLotId(6L);
        verify(imageService).getImageUrlsByLotId(7L);
        verify(imageService, never()).getImageUrlsByLotId(5L); // URL для кешированного лота не нужен
        verify(lotCacheService, never()).getCacheLots(); // Общий кеш не должен был запрашиваться
    }
}
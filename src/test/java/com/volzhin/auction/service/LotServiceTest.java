package com.volzhin.auction.service;

import com.volzhin.auction.dto.LotDto;
import com.volzhin.auction.dto.event.DeleteLotEvent;
import com.volzhin.auction.entity.Image;
import com.volzhin.auction.entity.lot.Lot;
import com.volzhin.auction.entity.lot.LotCache;
import com.volzhin.auction.producer.DeleteLotProducer;
import com.volzhin.auction.producer.UpdateLotProducer;
import com.volzhin.auction.repository.LotRepository;
import com.volzhin.auction.service.image.ImageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LotServiceTest {

    @Mock
    private LotRepository lotRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @Mock
    private UpdateLotProducer updateLotProducer;

    @Mock
    private DeleteLotProducer deleteLotProducer;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private LotService lotService;

    private LotDto createTestLotDto() {
        return LotDto.builder()
                .id(1L)
                .title("Test Lot")
                .description("Description")
                .startingPrice(BigDecimal.valueOf(100.0))
                .currentPrice(BigDecimal.valueOf(100.0))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .categoryName("Electronics")
                .sellerId(1L)
                .build();
    }

    private Lot createTestLot() {
        return Lot.builder()
                .id(1L)
                .title("Test Lot")
                .description("Description")
                .startingPrice(BigDecimal.valueOf(100.0))
                .currentPrice(BigDecimal.valueOf(100.0))
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(Lot.Status.active)
                .build();
    }

    @Test
    void createLot_ShouldSaveLotWithImages() {
        // Arrange
        LotDto dto = createTestLotDto();
        List<MultipartFile> files = List.of(file);
        List<Image> images = List.of(new Image());

        when(imageService.addImages(any(), any())).thenReturn(images);
        when(lotRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Lot result = lotService.createLot(dto, files);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(images.size(), result.getImages().size());
        verify(lotRepository, times(2)).save(any());
        verify(imageService).addImages(any(), eq(files));
    }

    @Test
    void updateLot_WhenLotNotExists_ShouldThrowException() {
        // Arrange
        LotDto dto = createTestLotDto();
        List<MultipartFile> files = List.of(file);
        List<Image> images = List.of(new Image());
        when(imageService.addImages(any(), any())).thenReturn(images);

        when(lotRepository.existsById(dto.getId())).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> lotService.updateLot(dto, files));
    }

    @Test
    void findById_WhenLotExists_ShouldReturnLot() {
        // Arrange
        Long lotId = 1L;
        Lot expected = createTestLot();
        when(lotRepository.findById(lotId)).thenReturn(Optional.of(expected));

        // Act
        Lot result = lotService.findById(lotId);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void findById_WhenLotNotExists_ShouldThrowException() {
        // Arrange
        Long lotId = 1L;
        when(lotRepository.findById(lotId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> lotService.findById(lotId));
    }

    @Test
    void findLotsEndingWithin_ShouldReturnCorrectLots() {
        // Arrange
        int minutes = 10;
        int size = 5;
        List<Lot> expected = List.of(createTestLot());
        when(lotRepository.findLotsEndingWithin(any(), any(), any())).thenReturn(expected);

        // Act
        List<Lot> result = lotService.findLotsEndingWithin(minutes, size);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void existsById_ShouldDelegateToRepository() {
        // Arrange
        Long lotId = 1L;
        when(lotRepository.existsById(lotId)).thenReturn(true);

        // Act
        boolean result = lotService.existsById(lotId);

        // Assert
        assertTrue(result);
    }

    @Test
    void getLots_WithCategoryFilter_ShouldReturnFilteredResults() {
        // Arrange
        String category = "Electronics";
        Pageable pageable = PageRequest.of(0, 10);
        List<Lot> expected = List.of(createTestLot());
        when(lotRepository.findByCategoryName(category, pageable)).thenReturn(expected);

        // Act
        List<Lot> result = lotService.getLots(0, 10, category);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getLots_WithoutCategory_ShouldReturnAll() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lot> page = new PageImpl<>(List.of(createTestLot()));
        when(lotRepository.findAll(pageable)).thenReturn(page);

        // Act
        List<Lot> result = lotService.getLots(0, 10, null);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getLotsByUserId_ShouldReturnUserLots() {
        // Arrange
        Long userId = 1L;
        List<Lot> expected = List.of(createTestLot());
        when(lotRepository.findLotsBySellerId(userId)).thenReturn(expected);

        // Act
        List<Lot> result = lotService.getLotsByUserId(userId);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void deleteLot_ShouldDeleteAndSendEvent() {
        // Arrange
        Long lotId = 1L;
        doNothing().when(lotRepository).deleteById(lotId);

        // Act
        lotService.deleteLot(lotId);

        // Assert
        verify(lotRepository).deleteById(lotId);
        verify(deleteLotProducer).send(any(DeleteLotEvent.class));
    }

    @Test
    void save_ShouldDelegateToRepository() {
        // Arrange
        Lot lot = createTestLot();
        when(lotRepository.save(lot)).thenReturn(lot);

        // Act
        lotService.save(lot);

        // Assert
        verify(lotRepository).save(lot);
    }
}
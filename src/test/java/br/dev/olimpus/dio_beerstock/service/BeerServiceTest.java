package br.dev.olimpus.dio_beerstock.service;

import br.dev.olimpus.dio_beerstock.builder.BeerDTOBuilder;
import br.dev.olimpus.dio_beerstock.dto.BeerDto;
import br.dev.olimpus.dio_beerstock.entity.Beer;
import br.dev.olimpus.dio_beerstock.exception.BeerAlreadyRegisteredException;
import br.dev.olimpus.dio_beerstock.exception.BeerNotFoundException;
import br.dev.olimpus.dio_beerstock.exception.BeerStockExceededException;
import br.dev.olimpus.dio_beerstock.mapper.BeerMapper;
import br.dev.olimpus.dio_beerstock.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private BeerRepository beerRepository;

    private BeerMapper beerMapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService beerService;

    @Test
    void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
        // given
        BeerDto expectedBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDto);

        // when
        when(beerRepository.findByName(expectedBeerDto.getName())).thenReturn(Optional.empty());
        when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        //then
        BeerDto createdBeerDto = beerService.createBeer(expectedBeerDto);

        assertThat(createdBeerDto.getId(), is(equalTo(expectedBeerDto.getId())));
        assertThat(createdBeerDto.getName(), is(equalTo(expectedBeerDto.getName())));
        assertThat(createdBeerDto.getQuantity(), is(equalTo(expectedBeerDto.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() {
        // given
        BeerDto expectedBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer duplicatedBeer = beerMapper.toModel(expectedBeerDto);

        // when
        when(beerRepository.findByName(expectedBeerDto.getName())).thenReturn(Optional.of(duplicatedBeer));

        // then
        assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDto));
    }

    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
        // given
        BeerDto expectedFoundBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDto);

        // when
        when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));

        // then
        BeerDto foundBeerDto = beerService.findByName(expectedFoundBeerDto.getName());

        assertThat(foundBeerDto, is(equalTo(expectedFoundBeerDto)));
    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
        // given
        BeerDto expectedFoundBeerDto = BeerDTOBuilder.builder().build().toBeerDto();

        // when
        when(beerRepository.findByName(expectedFoundBeerDto.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedFoundBeerDto.getName()));
    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfBeers() {
        // given
        BeerDto expectedFoundBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDto);

        //when
        when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

        //then
        List<BeerDto> foundListBeersDTO = beerService.listAll();

        assertThat(foundListBeersDTO, is(not(empty())));
        assertThat(foundListBeersDTO.get(0), is(equalTo(expectedFoundBeerDto)));
    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
        //when
        when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<BeerDto> foundListBeersDTO = beerService.listAll();

        assertThat(foundListBeersDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException{
        // given
        BeerDto expectedDeletedBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDto);

        // when
        when(beerRepository.findById(expectedDeletedBeerDto.getId())).thenReturn(Optional.of(expectedDeletedBeer));
        doNothing().when(beerRepository).deleteById(expectedDeletedBeerDto.getId());

        // then
        beerService.deleteById(expectedDeletedBeerDto.getId());

        verify(beerRepository, times(1)).findById(expectedDeletedBeerDto.getId());
        verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDto.getId());
    }

    @Test
    void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
        //given
        BeerDto expectedBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDto);

        //when
        when(beerRepository.findById(expectedBeerDto.getId())).thenReturn(Optional.of(expectedBeer));
        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedBeerDto.getQuantity() + quantityToIncrement;

        // then
        BeerDto incrementedBeerDto = beerService.increment(expectedBeerDto.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBeerDto.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedBeerDto.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        BeerDto expectedBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDto);

        when(beerRepository.findById(expectedBeerDto.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToIncrement = 80;
        assertThrows(BeerStockExceededException.class, () -> beerService.increment(expectedBeerDto.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        BeerDto expectedBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDto);

        when(beerRepository.findById(expectedBeerDto.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToIncrement = 45;
        assertThrows(BeerStockExceededException.class, () -> beerService.increment(expectedBeerDto.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> beerService.increment(INVALID_BEER_ID, quantityToIncrement));
    }
/*
    @Test
    void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
        BeerDto expectedBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDto);

        when(beerRepository.findById(expectedBeerDto.getId())).thenReturn(Optional.of(expectedBeer));
        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        int quantityToDecrement = 5;
        int expectedQuantityAfterDecrement = expectedBeerDto.getQuantity() - quantityToDecrement;
        BeerDto incrementedBeerDto = beerService.decrement(expectedBeerDto.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDto.getQuantity()));
        assertThat(expectedQuantityAfterDecrement, greaterThan(0));
    }

    @Test
    void whenDecrementIsCalledToEmptyStockThenEmptyBeerStock() throws BeerNotFoundException, BeerStockExceededException {
        BeerDto expectedBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDto);

        when(beerRepository.findById(expectedBeerDto.getId())).thenReturn(Optional.of(expectedBeer));
        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        int quantityToDecrement = 10;
        int expectedQuantityAfterDecrement = expectedBeerDto.getQuantity() - quantityToDecrement;
        BeerDto incrementedBeerDto = beerService.decrement(expectedBeerDto.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(0));
        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDto.getQuantity()));
    }

    @Test
    void whenDecrementIsLowerThanZeroThenThrowException() {
        BeerDto expectedBeerDto = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDto);

        when(beerRepository.findById(expectedBeerDto.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToDecrement = 80;
        assertThrows(BeerStockExceededException.class, () -> beerService.decrement(expectedBeerDto.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToDecrement = 10;

        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> beerService.decrement(INVALID_BEER_ID, quantityToDecrement));
    }*/
}

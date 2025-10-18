package br.dev.olimpus.dio_beerstock.mapper;

import br.dev.olimpus.dio_beerstock.dto.BeerDto;
import br.dev.olimpus.dio_beerstock.entity.Beer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toModel(BeerDto beerDTO);

    BeerDto toDTO(Beer beer);
}

package br.dev.olimpus.dio_beerstock.builder;

import br.dev.olimpus.dio_beerstock.dto.BeerDto;
import br.dev.olimpus.dio_beerstock.enums.BeerType;
import lombok.Builder;

@Builder
public class BeerDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Brahma";

    @Builder.Default
    private String brand = "Ambev";

    @Builder.Default
    private Integer max = 50;

    @Builder.Default
    private Integer quantity = 10;

    @Builder.Default
    private BeerType type = BeerType.LAGER;

    public BeerDto toBeerDto() {
        return new BeerDto(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}

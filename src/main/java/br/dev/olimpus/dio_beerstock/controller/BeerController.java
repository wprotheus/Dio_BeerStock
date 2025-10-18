package br.dev.olimpus.dio_beerstock.controller;

import br.dev.olimpus.dio_beerstock.dto.BeerDto;
import br.dev.olimpus.dio_beerstock.dto.QuantityDTO;
import br.dev.olimpus.dio_beerstock.exception.BeerAlreadyRegisteredException;
import br.dev.olimpus.dio_beerstock.exception.BeerNotFoundException;
import br.dev.olimpus.dio_beerstock.exception.BeerStockExceededException;
import br.dev.olimpus.dio_beerstock.service.BeerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerController /*implements BeerControllerDocs*/ {

    private final BeerService beerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerDto createBeer(@RequestBody @Valid BeerDto beerDTO) throws BeerAlreadyRegisteredException {
        return beerService.createBeer(beerDTO);
    }

    @GetMapping("/{name}")
    public BeerDto findByName(@PathVariable String name) throws BeerNotFoundException {
        return beerService.findByName(name);
    }

    @GetMapping
    public List<BeerDto> listBeers() {
        return beerService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws BeerNotFoundException {
        beerService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public BeerDto increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO)
            throws BeerNotFoundException, BeerStockExceededException {
        return beerService.increment(id, quantityDTO.getQuantity());
    }

    @PatchMapping("/{id}/decrement")
    public BeerDto decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO)
            throws BeerNotFoundException, BeerStockExceededException {
        return beerService.decrement(id, quantityDTO.getQuantity());
    }
}

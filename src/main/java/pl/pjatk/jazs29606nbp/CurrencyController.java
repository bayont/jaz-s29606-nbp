package pl.pjatk.jazs29606nbp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RestControllerAdvice
@RequestMapping("/currency")
@Tag(name = "Currency", description = "The Currency API")
public class CurrencyController {

    CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }


    @GetMapping("/{currencyCode}")
    @ResponseBody()
    @Operation(summary = "Get average rate for currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the currency",
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Invalid currency code",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content)
    })
    public ResponseEntity<Double> getCurrency(@PathVariable String currencyCode, @RequestParam() LocalDate startDate, @RequestParam() LocalDate endDate) throws CurrencyServiceException {
        return new ResponseEntity<>(currencyService.getAverageRate(currencyCode, startDate, endDate), HttpStatus.OK);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>("Currency not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrencyServiceException.class)
    public ResponseEntity<String> handleCurrencyServiceException(CurrencyServiceException e) {
        return new ResponseEntity<>("Bad request",HttpStatus.BAD_REQUEST);
    }

}

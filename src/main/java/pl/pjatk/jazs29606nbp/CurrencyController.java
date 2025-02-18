package pl.pjatk.jazs29606nbp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.jazs29606nbp.exceptions.BadRequestException;
import pl.pjatk.jazs29606nbp.exceptions.CurrencyServiceException;
import pl.pjatk.jazs29606nbp.exceptions.ExternalServiceUnhandledException;
import pl.pjatk.jazs29606nbp.exceptions.NotFoundException;
import pl.pjatk.jazs29606nbp.model.AverageRateResponse;
import pl.pjatk.jazs29606nbp.model.ErrorResponse;

import java.time.LocalDate;
import java.util.NoSuchElementException;

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
    @Operation(summary = "Get average rate for specific currency code within date ranges")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the currency within the range",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AverageRateResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid currency code or invalid date range",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Currency not found or no recorded rates within the date range",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Unhandled exception",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "502", description = "Unhandled exception from external dependency",
                    content = {@Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<AverageRateResponse> getCurrency(@PathVariable String currencyCode, @RequestParam() LocalDate startDate, @RequestParam() LocalDate endDate) throws CurrencyServiceException {
        return new ResponseEntity<>(currencyService.getAverageRate(currencyCode, startDate, endDate), HttpStatus.OK);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse("Currency not found or no recorded rates within the date range."), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        return new ResponseEntity<>(new ErrorResponse("Bad parameters sent, check your dates."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExternalServiceUnhandledException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceUnhandledException(ExternalServiceUnhandledException e) {
        return new ResponseEntity<>(new ErrorResponse("Unhandled exception from external dependency."),HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(CurrencyServiceException.class)
    public ResponseEntity<ErrorResponse> handleCurrencyServiceException(CurrencyServiceException e) {
        return new ResponseEntity<>(new ErrorResponse("Unhandled exception."),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return new ResponseEntity<>(new ErrorResponse("Missing one of the required query parameter: '" + e.getParameterName() + "'"), HttpStatus.BAD_REQUEST);
    }

}

package pl.pjatk.jazs29606nbp;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import pl.pjatk.jazs29606nbp.exceptions.BadRequestException;
import pl.pjatk.jazs29606nbp.exceptions.CurrencyServiceException;
import pl.pjatk.jazs29606nbp.exceptions.ExternalServiceUnhandledException;
import pl.pjatk.jazs29606nbp.exceptions.NotFoundException;
import pl.pjatk.jazs29606nbp.model.AverageRateResponse;
import pl.pjatk.jazs29606nbp.model.Result;
import pl.pjatk.jazs29606nbp.model.dto.CurrencyDTO;
import pl.pjatk.jazs29606nbp.model.dto.RateDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;


@Service
public class CurrencyService {
    private final RestTemplate restTemplate;
    private final CurrencyResultRepository currencyResultRepository;
    public CurrencyService(RestTemplate restTemplate, CurrencyResultRepository currencyResultRepository) {
        this.restTemplate = restTemplate;
        this.currencyResultRepository = currencyResultRepository;
    }

    public AverageRateResponse getAverageRate(String currencyCode, LocalDate startDate, LocalDate endDate) throws CurrencyServiceException {
        CurrencyDTO currencyDTO;
        try {
            currencyDTO = getCurrency(currencyCode, this.formatDate(startDate), this.formatDate(endDate));
        } catch (HttpStatusCodeException e) {
            HttpStatusCode statusCode = e.getStatusCode();
            if (statusCode.equals(HttpStatus.NOT_FOUND)) {
                throw new NotFoundException();
            } else if (statusCode.equals(HttpStatus.BAD_REQUEST)) {
                throw new BadRequestException();
            } else if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                throw new ExternalServiceUnhandledException();
            }
            throw new CurrencyServiceException();
        }
        double averageRate = calculateAverageRate(currencyDTO);
        saveResultToDatabase(currencyDTO.getCurrency(), currencyCode, startDate, endDate, averageRate, LocalDateTime.now());
        return new AverageRateResponse(averageRate);
    }

    private double calculateAverageRate(CurrencyDTO currencyDTO) {
        return currencyDTO.getRates().stream().mapToDouble(RateDTO::getMid).average().orElse(0);
    }

    private void saveResultToDatabase(String currency, String currencyCode, LocalDate startDate, LocalDate endDate, double averageRate, LocalDateTime requestDate) {
        this.currencyResultRepository.save(new Result(currency, currencyCode, averageRate, startDate, endDate, requestDate));
    }

    private CurrencyDTO getCurrency(String currencyCode,String startDate, String endDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        return restTemplate.exchange("http://api.nbp.pl/api/exchangerates/rates/a/" + currencyCode + "/" + startDate + "/" + endDate, HttpMethod.GET, entity, CurrencyDTO.class).getBody();
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_DATE); // YYYY-MM-DD
    }

}

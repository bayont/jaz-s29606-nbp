package pl.pjatk.jazs29606nbp;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import pl.pjatk.jazs29606nbp.model.Result;
import pl.pjatk.jazs29606nbp.model.dto.CurrencyDTO;
import pl.pjatk.jazs29606nbp.model.dto.RateDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class CurrencyServiceException extends Exception {
    public CurrencyServiceException(String message) {
        super(message);
    }
}

class NotFoundException extends CurrencyServiceException {
    public NotFoundException(String message) {
        super(message);
    }
}

class BadRequestException extends CurrencyServiceException {
    public BadRequestException(String message) {
        super(message);
    }
}

@Service
public class CurrencyService {
    private final RestTemplate restTemplate;
    private final CurrencyResultRepository currencyResultRepository;
    public CurrencyService(RestTemplate restTemplate, CurrencyResultRepository currencyResultRepository) {
        this.restTemplate = restTemplate;
        this.currencyResultRepository = currencyResultRepository;
    }

    public Double getAverageRate(String currencyCode, LocalDate startDate, LocalDate endDate) throws CurrencyServiceException {
        CurrencyDTO currencyDTO;
        try {

            currencyDTO = getCurrency(currencyCode, "d", "x");
        } catch (HttpStatusCodeException e) {
            HttpStatusCode statusCode = e.getStatusCode();
            if (statusCode.equals(NOT_FOUND)) {
                throw new NotFoundException("Currency not found");
            } else if (statusCode.equals(BAD_REQUEST)) {
                throw new BadRequestException("Bad request");
            }
            throw new CurrencyServiceException("Unknown error");
        }
        double averageRate = calculateAverageRate(currencyDTO);
        saveResultToDatabase(currencyDTO.getCurrency(), currencyCode, startDate, endDate, averageRate, LocalDateTime.now());
        return averageRate;
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

}

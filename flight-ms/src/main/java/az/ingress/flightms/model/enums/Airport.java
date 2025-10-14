package az.ingress.flightms.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Airport {
    GYD("Baku", "Azerbaijan"),
    JFK("New York", "USA"),
    LHR("London", "UK"),
    ATL("Atlanta", "USA"),
    PEK("Beijing", "China"),
    DXB("Dubai", "UAE"),
    LAX("Los Angeles", "USA"),
    ORD("Chicago", "USA"),
    CDG("Paris", "France"),
    AMS("Amsterdam", "Netherlands"),
    HND("Tokyo", "Japan"),
    SYD("Sydney", "Australia"),
    ICN("Seoul", "South Korea"),
    SIN("Singapore", "Singapore"),
    FRA("Frankfurt", "Germany"),
    DEL("Delhi", "India"),
    MUC("Munich", "Germany"),
    YYZ("Toronto", "Canada"),
    MAD("Madrid", "Spain"),
    GRU("São Paulo", "Brazil"),
    IST("Istanbul", "Turkey"),
    BKK("Bangkok", "Thailand"),
    HKG("Hong Kong", "Hong Kong"),
    MEX("Mexico City", "Mexico"),
    EZE("Buenos Aires", "Argentina"),
    JNB("Johannesburg", "South Africa"),
    SFO("San Francisco", "USA"),
    ZRH("Zurich", "Switzerland"),
    DME("Moscow", "Russia")
    ;

    private final String city;
    private final String country;

    public static List<Airport> findByCity(String city) {
        return Arrays.stream(values())
                .filter(airport -> airport.getCity().toLowerCase().contains(city.toLowerCase()))
                .toList();
    }

    public static List<Airport> findByCountry(String country) {
        return Arrays.stream(values())
                .filter(airport -> airport.getCountry().toLowerCase().contains(country.toLowerCase()))
                .toList();
    }

}

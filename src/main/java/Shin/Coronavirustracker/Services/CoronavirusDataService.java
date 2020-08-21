package Shin.Coronavirustracker.Services;

import Shin.Coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.Map.Entry;


@Service
public class CoronavirusDataService {
    //get the data and parse it

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    public Map<String, Integer> getNowStore() {
        return nowStore;
    }

    public Map<String, Integer> getPreStore() {
        return preStore;
    }

    private LocationStats locationStats;


    public CoronavirusDataService(LocationStats locationStats) {
        this.locationStats = locationStats;
    }

    private Map<String, Integer> nowStore =new HashMap<>();
    private Map<String, Integer> preStore =new HashMap<>();

    private List<LocationStats> allStats = new ArrayList<>();

    //after construction execute this method
    @PostConstruct
    //코로나 상황을 업데이트하기위해 스케쥴 사용
    @Scheduled(cron = "* * 1 * * *") //seconds minutes hours days months years
    public void fetchVirusData() throws IOException, InterruptedException {
        nowStore.clear();
        preStore.clear();
        List<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = (HttpRequest) HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        //request , body-handler

        //CSV를 콤마를 기준으로 파싱할수도 있지만 더 쉬운 아파치 common-csv 라이브러리를 사용
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        // CSV -> String -> StringReader - > Common-CSV parsing
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);


        for (CSVRecord record : records) {
            //케이스 같은 경우는 매일 날짜가 바뀌므로 레코드의 사이즈를 가져와서 맨 마지막 컬럼을 가져옴
            nowStore.put(record.get("Country/Region"), nowStore.getOrDefault(record.get("Country/Region"),0)+ Integer.parseInt(record.get(record.size() - 1)));
            preStore.put(record.get("Country/Region"), preStore.getOrDefault(record.get("Country/Region"),0)+ Integer.parseInt(record.get(record.size() - 2)));
        }

        //맵을 키별로 조회하기 위해서 변환

        Set nowSet = nowStore.entrySet();
        Iterator nowIterator = nowSet.iterator();
        Set preSet = preStore.entrySet();
        Iterator preIterator = preSet.iterator();

        while(nowIterator.hasNext()){
            LocationStats locationStats = new LocationStats();

            Entry<String,Integer> entry = (Entry)nowIterator.next();
            Entry<String,Integer> entry2 = (Entry)preIterator.next();
            String key = (String)entry.getKey();
            int NowValue = (Integer)entry.getValue();
            int PreValue = (Integer)entry2.getValue();
            locationStats.setCountry(key);
            locationStats.setLatestTotal(NowValue);
            locationStats.setDiffFromPrevDay(NowValue-PreValue);

            newStats.add(locationStats);

        }


        this.allStats = newStats;

    }

    public int TotalCases(){
        int result = 0;
        result = allStats.stream().mapToInt(stat -> stat.getLatestTotal()).sum();
        return result;
    }
    public int totalNewCases(){
        int result = 0;
        result = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        return result;
    }
}

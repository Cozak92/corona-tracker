package Shin.Coronavirustracker.models;

import org.springframework.stereotype.Service;

@Service
public class LocationStats {

    private String country;
    private int latestTotal;
    private int diffFromPrevDay;

    public int getDiffFromPrevDay() {
        return diffFromPrevDay;
    }

    public void setDiffFromPrevDay(int diffFromPrevDay) {
        String country = this.getCountry();
        this.diffFromPrevDay = diffFromPrevDay;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getLatestTotal() {

        return latestTotal;
    }

    public void setLatestTotal(int latestTotal) {
        this.latestTotal = latestTotal;
    }


    @Override
    public String toString() {
        return "LocationStats{" +
                ", country='" + country + '\'' +
                ", latestTotal=" + latestTotal +
                '}';
    }
}

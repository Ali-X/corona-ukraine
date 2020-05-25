package ua.ali_x.telegrambot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Integer infected;
    private Integer deaths;
    private Integer recovered;
    private Date date;

    public Statistic(Integer infected, Integer deaths, Integer recovered, Date date) {
        this.infected = infected;
        this.deaths = deaths;
        this.recovered = recovered;
        this.date = date;
    }

    public Statistic() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistic statistic = (Statistic) o;
        return infected.equals(statistic.infected) &&
                deaths.equals(statistic.deaths) &&
                recovered.equals(statistic.recovered);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infected, deaths, recovered);
    }
}

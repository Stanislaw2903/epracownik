package com.eurosoftit.ebok.web.rest.dashboard;


import com.eurosoftit.ebok.BooleanToStringConverter;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "okres")
@Data
public class RangesEntity {

    @Id
    @Column(name = "okr_serial")
    private Long id;
    @Column(name = "okr_data_od")
    private ZonedDateTime dateFrom;
    @Column(name = "okr_data_do")
    private ZonedDateTime dateTo;
    @Column(name = "okr_dni_pracy")
    private Integer workDays;
    @Column(name = "okr_1")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day1;
    @Column(name = "okr_2")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day2;
    @Column(name = "okr_3")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day3;
    @Column(name = "okr_4")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day4;
    @Column(name = "okr_5")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day5;
    @Column(name = "okr_6")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day6;
    @Column(name = "okr_7")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day7;
    @Column(name = "okr_8")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day8;
    @Column(name = "okr_9")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day9;
    @Column(name = "okr_10")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day10;
    @Column(name = "okr_11")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day11;
    @Column(name = "okr_12")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day12;
    @Column(name = "okr_13")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day13;
    @Column(name = "okr_14")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day14;
    @Column(name = "okr_15")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day15;
    @Column(name = "okr_16")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day16;
    @Column(name = "okr_17")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day17;
    @Column(name = "okr_18")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day18;
    @Column(name = "okr_19")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day19;
    @Column(name = "okr_20")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day20;
    @Column(name = "okr_21")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day21;
    @Column(name = "okr_22")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day22;
    @Column(name = "okr_23")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day23;
    @Column(name = "okr_24")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day24;
    @Column(name = "okr_25")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day25;
    @Column(name = "okr_26")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day26;
    @Column(name = "okr_27")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day27;
    @Column(name = "okr_28")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day28;
    @Column(name = "okr_29")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day29;
    @Column(name = "okr_30")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day30;
    @Column(name = "okr_31")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean day31;
}

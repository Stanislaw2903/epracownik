package com.eurosoftit.ebok.web.rest.payroll;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Subselect("SELECT row_number() over() as id, " +
    "sum(p.lpl_pdst_zus) as nine, " +
    "sum(p.lpl_pdst_zus) as ten, " +
    "sum(p.lpl_pdst_zus) as eleven, " +
    "sum(p.lpl_pdst_zdrowotne) as twelve, " +
    "sum(p.lpl_emer_ubez) as thirteen, " +
    "sum(p.lpl_rent_ubez) as fourteen, " +
    "sum(p.lpl_chor_ubez) as fifteen, " +
    "sum(p.lpl_emer_ubez + p.lpl_rent_ubez + p.lpl_chor_ubez) as sixteen, " +
    "sum(p.lpl_zdro_ubez) as seventeen, " +
    "sum(p.lpl_pod_zdro_kw) as eighteen,  " +
    "sum(p.lpl_zdro_ubez - p.lpl_pod_zdro_kw) as eighteen_a, " +
    "sum(p.lpl_przych_opod) as nineteen, " +
    "sum(p.lpl_koszty) as twenty, " +
    "sum(p.lpl_zal_stawka) as twenty_one, " +
    "sum(p.lpl_zal_wolna) as twenty_two, " +
    "sum(p.lpl_zal_doch) as twenty_three, " +
    "sum(p.lpl_kwota_dod_1) as twenty_four, " +
    "sum(p.lpl_kwota_dod_2) as twenty_five, " +
    "max(p.lpl_oso_serial) as external_id, " +
    "date_trunc('MONTH',p.lpl_data_wyplaty) as payment_date "  +
    "FROM PLACE p WHERE exists (SELECT 1 FROM LISTY l WHERE l.lis_serial = p.lpl_lis_serial AND l.lis_char_1 = 'T') " +
    "GROUP BY p.lpl_oso_serial, date_trunc('MONTH',p.lpl_data_wyplaty)")
@Immutable
@Getter
@Setter
public class PayrollAdditionalDescription {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "payment_date")
    private ZonedDateTime paymentDate;
    @Column(name = "nine")
    private BigDecimal nine;
    @Column(name = "ten")
    private BigDecimal ten;
    @Column(name = "eleven")
    private BigDecimal eleven;
    @Column(name = "twelve")
    private BigDecimal twelve;
    @Column(name = "thirteen")
    private BigDecimal thirteen;
    @Column(name = "fourteen")
    private BigDecimal fourteen;
    @Column(name = "fifteen")
    private BigDecimal fifteen;
    @Column(name = "sixteen")
    private BigDecimal sixteen;
    @Column(name = "seventeen")
    private BigDecimal seventeen;
    @Column(name = "eighteen")
    private BigDecimal eighteen;
    @Column(name = "eighteen_a")
    private BigDecimal eighteenA;
    @Column(name = "nineteen")
    private BigDecimal nineteen;
    @Column(name = "twenty")
    private BigDecimal twenty;
    @Column(name = "twenty_one")
    private BigDecimal twentyOne;
    @Column(name = "twenty_two")
    private BigDecimal twentyTwo;
    @Column(name = "twenty_three")
    private BigDecimal twentyThree;
    @Column(name = "twenty_four")
    private BigDecimal twentyFour;
    @Column(name = "twenty_five")
    private BigDecimal twentyFive;
    @Column(name = "external_id")
    private Long externalId;
}

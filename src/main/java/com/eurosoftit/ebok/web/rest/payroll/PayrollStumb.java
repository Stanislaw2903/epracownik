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
@Subselect("select row_number() over() as id,\n" +
    " max(a.payment_date) as payment_date,\n" +
    " a.external_id as external_id,\n" +
    " min(a.date_from) as date_from,\n" +
    " max(a.date_to) as date_to,\n" +
    " sum(a.salary_in_cash) as salary_in_cash,\n" +
    " sum(a.salary_in_kind) as salary_in_kind,\n" +
    " sum(a.taxed) as taxed,\n" +
    " sum(COALESCE(a.ld_kwota_brutto,0) + COALESCE(a.ld_kwota_zlecone,0) + COALESCE(a.ld_kwota_urlop_o,0)) as gross_premium, " +
    " sum(COALESCE(a.taxed_without_zus,0)) as taxed_without_zus,\n" +
    " sum(COALESCE(a.without_zus_and_tax,0)) as without_zus_and_tax,\n" +
    " sum(a.social_insurance_benefits) as social_insurance_benefits,\n" +
    " sum(COALESCE(a.inability_2,0)) as inability_2,\n" +
    " sum(a.retirement) as retirement ,\n" +
    " sum(a.disability) as disability,\n" +
    " sum(a.sickness) as sickness,\n" +
    " sum(a.contributions_sum) as contributions_sum,\n" +
    " sum(a.cost_of_getting_income) as cost_of_getting_income,\n" +
    " sum(a.social_security_deducted) as social_security_deducted,\n" +
    " sum(a.income_in_a_given_month) as income_in_a_given_month,\n" +
    " (select sum(p2.lpl_pdst_pod_doch) from PLACE p2 JOIN  LISTY l ON l.lis_serial = p2.lpl_lis_serial and l.lis_char_1 = 'T' and p2.lpl_oso_serial = a.external_id and extract('MONTH' FROM p2.lpl_data_wyplaty) <= extract('MONTH' FROM max(a.payment_date)) and extract('YEAR' from max(a.payment_date)) = extract('YEAR' from p2.lpl_data_wyplaty)) as income_from_beginning_of_year,\n" +
    " max(a.rate) as rate,\n" +
    " sum(a.free_amount) as free_amount,\n" +
    " sum(a.accrued_advance_payment_for_income_tax) as accrued_advance_payment_for_income_tax,\n" +
    " sum(a.taken) as taken,\n" +
    " sum(a.deducted) as deducted,\n" +
    " sum(a.due) as due,\n" +
    " sum(a.collected) as collected,\n" +
    " sum(a.deductions) as deductions,\n" +
    " sum(a.to_pay) as to_pay\n" +
    "FROM ( SELECT \n" +
    " row_number() over() as id," +
    " max(p.lpl_data_wyplaty) as payment_date,\n" +
    " p.lpl_oso_serial as external_id,\n" +
    "  date_trunc('MONTH',p.lpl_data_wyplaty) as cal_month," +
    " min(date_trunc('MONTH',p.lpl_data_wyplaty)) as date_from,\n" +
    " max((date_trunc('month', p.lpl_data_wyplaty) + interval '1 month' - interval '1 day')) as date_to,\n" +
    " sum(case when t.tyt_zas_swi_dni <> 'N' then p.lpl_podst + p.lpl_niezd + p.lpl_zas_rodz + p.lpl_zas_piel + p.lpl_zas_wych else 0 end) as salary_in_cash,\n" +
    " sum(case when t.tyt_zas_swi_dni = 'N' then p.lpl_podst + p.lpl_niezd + p.lpl_zas_rodz + p.lpl_zas_piel + p.lpl_zas_wych else 0 end) as salary_in_kind,\n" +
    " sum(p.lpl_przych_opod) as taxed,\n" +
    " l.lis_serial as list_id,"+
    "  (SELECT sum(COALESCE(ps.skl_kwota,0)) FROM PLACE_SKL ps LEFT JOIN wyn_kod_skl wks ON wks.wks_serial = ps.skl_wks_serial WHERE ( wks.wks_dod_15='T' OR ( wks.wks_dod_15 is null AND NOT ( ps.skl_zus='N' AND ps.skl_chor_wyp='N' AND ps.skl_zdrowotne='N' ) ) ) AND ps.skl_rodzaj<>'U' and p.lpl_serial = ps.skl_lpl_serial and p.lpl_oso_serial = ps.skl_oso_serial) as ld_kwota_brutto,\n" +
    "  (SELECT sum(COALESCE(pz.pzl_kwota,0)) FROM place_zlec pz LEFT JOIN wyn_kod_skl wks ON wks.wks_serial = pz.pzl_wks_serial WHERE ( wks_dod_15='T' OR ( wks_dod_15 is null AND NOT ( pz.pzl_zus='N' AND pz.pzl_chor_wyp='N' AND pz.pzl_zdrowotne='N' ) ) ) and pz.pzl_lis_serial = l.lis_serial ) as ld_kwota_zlecone,\n" +
    "   (SELECT sum(COALESCE(pp.prz_kwota,0)) FROM place_prz pp LEFT JOIN wyn_kod_prz wkp ON wkp.wkp_serial = pp.prz_wkp_serial\n" +
    "    WHERE wkp.wkp_kod<>'331' AND wkp.wkp_kod<>'332' AND wkp.wkp_kod<>'333' AND wkp.wkp_kod<>'334' AND max(t.tyt_typ)='S'\n" +
    "     and pp.prz_lis_serial = l.lis_serial and pp.prz_oso_serial = p.lpl_oso_serial) as ld_kwota_urlop_o,\n" +
    "(SELECT sum(COALESCE(ps.skl_kwota,0)) FROM PLACE_SKL ps WHERE ps.skl_zus='N' AND ps.skl_chor_wyp='N' AND ps.skl_zdrowotne='N' AND ps.skl_pod_doch='T' and p.lpl_serial = ps.skl_lpl_serial and p.lpl_oso_serial = ps.skl_oso_serial) as taxed_without_zus,\n" +
    " (SELECT sum( COALESCE(ps.skl_kwota,0)) FROM PLACE_SKL ps WHERE ps.skl_pod_doch='N' and p.lpl_serial = ps.skl_lpl_serial and p.lpl_oso_serial = ps.skl_oso_serial) as without_zus_and_tax,  +\n" +
    " sum(case when t.tyt_typ = 'P' then lpl_podst + lpl_niezd else 0 end) as social_insurance_benefits,\t\n" +
    " (SELECT sum(COALESCE (pp.prz_kwota,0)) FROM place_prz pp LEFT JOIN wyn_kod_prz wkp ON wkp.wkp_serial = pp.prz_wkp_serial\n" +
    " WHERE wkp.wkp_kod<>'331' AND wkp.wkp_kod<>'332' AND wkp.wkp_kod<>'333' AND wkp.wkp_kod<>'334' AND max(t.tyt_typ)='S'\n" +
    " and pp.prz_lis_serial = l.lis_serial and pp.prz_oso_serial = p.lpl_oso_serial) as inability_2,\n" +
    " sum(p.lpl_emer_ubez) as retirement ,\n" +
    " sum(p.lpl_rent_ubez) as disability,\n" +
    " sum(p.lpl_chor_ubez) as sickness,\n" +
    " sum((p.lpl_emer_ubez + p.lpl_rent_ubez + p.lpl_chor_ubez)) as contributions_sum,\n" +
    " sum(p.lpl_koszty) as cost_of_getting_income, sum(p.lpl_pod_zus_kw) as social_security_deducted,\n" +
    " sum(p.lpl_pdst_pod_doch) as income_in_a_given_month,\n" +
    " sum(p.lpl_zal_stawka) as rate,\n" +
    " sum(p.lpl_zal_wolna) as free_amount,\n" +
    " sum(p.lpl_kwota_dod_6) as accrued_advance_payment_for_income_tax,\n" +
    " sum(p.lpl_zdro_ubez) as taken,\n" +
    " sum(p.lpl_pod_zdro_kw) as deducted,\n" +
    " sum(p.lpl_zal_doch) as due,\n" +
    " sum(p.lpl_kwota_dod_1) as collected,\n" +
    " sum(p.lpl_potracenia) as deductions,\n" +
    " sum(p.lpl_podst + p.lpl_niezd + p.lpl_zas_rodz + p.lpl_zas_piel + p.lpl_zas_wych - p.lpl_potracenia - (p.lpl_emer_ubez + p.lpl_rent_ubez + p.lpl_chor_ubez + p.lpl_wyp_ubez + p.lpl_emer_pfron + p.lpl_rent_pfron + p.lpl_chor_pfron) - p.lpl_zdro_ubez - p.lpl_kwota_dod_1 - p.lpl_przych_opod_odl) as to_pay\n" +
    "FROM PLACE p \n" +
    "JOIN LISTY l ON l.lis_serial = p.lpl_lis_serial \n" +
    "JOIN TYTUL t ON t.tyt_serial = l.lis_tyt_serial \n" +
    "WHERE l.lis_char_1 = 'T' " +
    "GROUP BY p.lpl_oso_serial, date_trunc('MONTH',p.lpl_data_wyplaty), p.lpl_serial, l.lis_serial) a\n" +
    "GROUP BY a.external_id,  cal_month")
@Immutable
@Getter
@Setter
public class PayrollStumb {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "payment_date")
    private ZonedDateTime paymentDate;
    @Column(name = "external_id")
    private Long externalId;
    @Column(name = "date_to")
    private ZonedDateTime dateTo;
    @Column(name = "salary_in_cash")
    private BigDecimal salaryInCash;
    @Column(name = "salary_in_kind")
    private BigDecimal salaryInKind;
    @Column(name = "taxed")
    private BigDecimal taxed;
    @Column(name = "gross_premium")
    private BigDecimal grossPremium;
    @Column(name = "taxed_without_zus")
    private BigDecimal taxedWithoutZus;
    @Column(name = "without_zus_and_tax")
    private BigDecimal withoutZusAndTax;
    @Column(name = "inability")
    private BigDecimal inability;
    @Column(name = "retirement")
    private BigDecimal retirement;
    @Column(name = "disability")
    private BigDecimal disability;
    @Column(name = "sickness")
    private BigDecimal sickness;
    @Column(name = "contributions_sum")
    private BigDecimal contributionsSum;
    @Column(name = "cost_of_getting_income")
    private BigDecimal costOfGettingIncome;
    @Column(name = "social_security_deducted")
    private BigDecimal socialSecurityDeducted;
    @Column(name = "income_in_a_given_month")
    private BigDecimal incomeInAGivenMonth;
    @Column(name = "income_from_beginning_of_year")
    private BigDecimal incomeFromBeginningOfYear;
    @Column(name = "rate")
    private BigDecimal rate;
    @Column(name = "free_amount")
    private BigDecimal freeAmount;
    @Column(name = "accrued_advance_payment_for_income_tax")
    private BigDecimal accruedAdvancePaymentForIncomeTax;
    @Column(name = "taken")
    private BigDecimal taken;
    @Column(name = "deducted")
    private BigDecimal deducted;
    @Column(name = "due")
    private BigDecimal due;
    @Column(name = "collected")
    private BigDecimal collected;
    @Column(name = "deductions")
    private BigDecimal deductions;
    @Column(name = "to_pay")
    private BigDecimal toPay;
    @Column(name = "social_insurance_benefits")
    private BigDecimal socialInsuranceBenefits;
    @Column(name = "inability_2")
    private BigDecimal inability2;
}


package com.eurosoftit.ebok.web.rest.payroll;

import com.eurosoftit.ebok.domain.DictionaryDTO;
import com.eurosoftit.ebok.web.rest.UserUtils;
import io.github.jhipster.service.QueryService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PayrollService extends QueryService<Payroll> {
    @Autowired
    private PayrollRepository repository;
    @Autowired
    private PayrollDetailsRepository detailsRepository;
    @Autowired
    private PayrollAdditionalDescriptionRepository additionalDescriptionRepository;
    @Autowired
    private UserUtils userUtils;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PayrollAdditionalDescriptionDeductionsRepository payrollAdditionalDescriptionDeductionsRepository;
    @Autowired
    private PayrollAdditionalDescriptionIngredientsRepository payrollAdditionalDescriptionIngredientsRepository;
    @Autowired
    private PayrollAdditionalDescriptionBreaksRepository payrollAdditionalDescriptionBreaksRepository;

    public PayrollDetailsDTO getPayrollDetails(Integer year) {
        Specification<PayrollDetails> specification = getSpecification(year);
        List<PayrollDetails> rows = detailsRepository.findAll(specification);
        List<PayrollDetailsListColumns> columns = detailsRepository.findAllColumns(userUtils.getExternalUserId(), year);

        return new PayrollDetailsDTO(rows, columns.stream().distinct().collect(Collectors.toList()));
    }

    public List<PayrollDTO> getPayroll(Integer year) {
        String sql = "select row_number() over() as id,\n" +
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
            "WHERE p.lpl_oso_serial = :externalId \n" +
            "AND extract('YEAR' from p.lpl_data_wyplaty) = :currentYear \n" +
            "AND l.lis_char_1 = 'T' \n" +
            "GROUP BY p.lpl_oso_serial, date_trunc('MONTH',p.lpl_data_wyplaty), p.lpl_serial, l.lis_serial) a\n" +
            "GROUP BY a.external_id,  cal_month";

        return entityManager.unwrap(Session.class).createNativeQuery(sql)
            .setParameter("externalId", userUtils.getExternalUserId()).setParameter("currentYear", year).setResultTransformer(new PayrollTransformer()).getResultList();
    }

    private <T> Specification<T> getSpecification(Integer year) {
        return getSpecification(year, 1);
    }

    private <T> Specification<T> getSpecification(Integer year, Integer month) {
        Specification<T> specification = Specification.where(equalExternalId(userUtils.getExternalUserId()));
        if (year != null) {
            ZonedDateTime start = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault());
            ZonedDateTime end = start.plusYears(1).minusSeconds(1);

            specification = specification.and(between(start, end, "paymentDate"));
        }

        return specification;
    }

    public static <T> Specification<T> between(final ZonedDateTime start, final ZonedDateTime end, String field) {
        return (Specification<T>) (root, criteriaQuery, criteriaBuilder) ->
            Objects.isNull(start) || Objects.isNull(end) ? null : criteriaBuilder.between(root.get(field), start, end);
    }

    static <T> Specification<T> equalExternalId(final Long externalId) {

        return (Specification<T>) (eventRoot, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.equal(eventRoot.get("externalId"), externalId);
    }

    public PayrollAdditionalDescriptionDTO getPayrollAdditionalDescription(Integer year, Integer month) {

        Specification<PayrollAdditionalDescription> specification = Specification.where(equalExternalId(userUtils.getExternalUserId()));
        if (year != null) {
            ZonedDateTime start = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault());

            specification = specification.and(between(start, start, "paymentDate"));
        }

        PayrollAdditionalDescription result = additionalDescriptionRepository.findOne(specification).orElse(null);

        Specification<PayrollAdditionalDescriptionDeductions> specification2 = Specification.where(equalExternalId(userUtils.getExternalUserId()));
        if (year != null) {
            ZonedDateTime start = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault());

            specification2 = specification2.and(between(start, start, "paymentDate"));
        }

        List<PayrollAdditionalDescriptionDeductions> deductions = payrollAdditionalDescriptionDeductionsRepository.findAll(specification2);

        Specification<PayrollAdditionalDescriptionIngredients> specification3 = Specification.where(equalExternalId(userUtils.getExternalUserId()));
        if (year != null) {
            ZonedDateTime start = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault());

            specification3 = specification3.and(between(start, start, "paymentDate"));
        }

        List<PayrollAdditionalDescriptionIngredients> ingredients = payrollAdditionalDescriptionIngredientsRepository.findAll(specification3);

        Specification<PayrollAdditionalDescriptionBreaks> specification4 = Specification.where(equalExternalId(userUtils.getExternalUserId()));
        if (year != null) {
            ZonedDateTime start = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault());

            specification4 = specification4.and(between(start, start, "paymentDate"));
        }

        List<PayrollAdditionalDescriptionBreaks> breaks = payrollAdditionalDescriptionBreaksRepository.findAll(specification4);

        return new PayrollAdditionalDescriptionDTO(result, deductions, ingredients, breaks);
    }

    public List<DictionaryDTO> getIngredients() {
        return payrollAdditionalDescriptionIngredientsRepository.getIngredients();
    }

    public List<DictionaryDTO> getBreaks() {
        return payrollAdditionalDescriptionBreaksRepository.getBreaks();
    }

    public List<DictionaryDTO> getDeductions() {
        return payrollAdditionalDescriptionDeductionsRepository.getDeductions();
    }

    private class PayrollTransformer implements org.hibernate.transform.ResultTransformer {

        private <T> T getValue(Class<T> clazz, Object object) {

            if (Objects.isNull(object)) {
                return null;
            }

            return clazz.cast(object);
        }

        private Long toLong(Object object) {

            if (Objects.isNull(object)) {
                return null;
            }

            if (object instanceof BigInteger) {
                return getValue(BigInteger.class, object).longValue();
            }

            return getValue(Integer.class, object).longValue();
        }

        private ZonedDateTime toZonedDateTime(Object object) {

            if (Objects.isNull(object)) {
                return null;
            }

            if (object instanceof Timestamp) {
                return getValue(Timestamp.class, object).toLocalDateTime().atZone(ZoneId.systemDefault());
            }

            return getValue(Date.class, object).toLocalDate().atStartOfDay(ZoneId.systemDefault());
        }

        @Override
        public Object transformTuple(Object[] objects, String[] strings) {
            PayrollDTO payrollDTO = new PayrollDTO();

            payrollDTO.setId(toLong(objects[0]));
            payrollDTO.setPaymentDate(toZonedDateTime(objects[1]));
            payrollDTO.setExternalId(toLong(objects[2]));

            payrollDTO.setDateTo(toZonedDateTime(objects[3]));
            payrollDTO.setDateFrom(toZonedDateTime(objects[4]));
            payrollDTO.setSalaryInCash(getValue(BigDecimal.class, objects[5]));
            payrollDTO.setSalaryInKind(getValue(BigDecimal.class, objects[6]));
            payrollDTO.setTaxed(getValue(BigDecimal.class, objects[7]));
            payrollDTO.setGrossPremium(getValue(BigDecimal.class, objects[8]));
            payrollDTO.setTaxedWithoutZus(getValue(BigDecimal.class, objects[9]));
            payrollDTO.setWithoutZusAndTax(getValue(BigDecimal.class, objects[10]));
            payrollDTO.setSocialInsuranceBenefits(getValue(BigDecimal.class, objects[11]));
            payrollDTO.setInability2(getValue(BigDecimal.class, objects[12]));

            payrollDTO.setSocialSecurityContributionsPaid(new SocialSecurityContributionsPaid(getValue(BigDecimal.class, objects[13]), getValue(BigDecimal.class, objects[14]), getValue(BigDecimal.class, objects[15]), getValue(BigDecimal.class, objects[16])));

            payrollDTO.setCostOfGettingIncome(getValue(BigDecimal.class, objects[17]));
            payrollDTO.setSocialSecurityDeducted(getValue(BigDecimal.class, objects[18]));
            payrollDTO.setIncomeInAGivenMonth(getValue(BigDecimal.class, objects[19]));

            payrollDTO.setIncomeFromBeginningOfYear(getValue(BigDecimal.class, objects[20]));
            payrollDTO.setRate(getValue(BigDecimal.class, objects[21]));
            payrollDTO.setFreeAmount(getValue(BigDecimal.class, objects[22]));
            payrollDTO.setAccruedAdvancePaymentForIncomeTax(getValue(BigDecimal.class, objects[23]));
            payrollDTO.setHealthInsurancePremium(new HealthInsurancePremium(getValue(BigDecimal.class, objects[24]), getValue(BigDecimal.class, objects[25])));

            payrollDTO.setAdvancePaymentForIncomeTax(new AdvancePaymentForIncomeTax(getValue(BigDecimal.class, objects[26]), getValue(BigDecimal.class, objects[27]), getValue(BigDecimal.class, objects[28]), getValue(BigDecimal.class, objects[29])));
            return payrollDTO;
        }

        @Override
        public List transformList(List list) {
            return list;
        }
    }
}

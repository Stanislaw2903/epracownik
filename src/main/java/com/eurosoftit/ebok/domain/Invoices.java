package com.eurosoftit.ebok.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Invoices.
 */
@Entity
@Table(name = "invoices")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Invoices implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "payment_deadline")
    private LocalDate paymentDeadline;

    @Column(name = "gross_amount")
    private Float grossAmount;

    @Column(name = "to_pay")
    private Float toPay;

    @Lob
    @Column(name = "document_file")
    private byte[] documentFile;

    @Column(name = "document_file_content_type")
    private String documentFileContentType;

    @Column(name = "status")
    private Boolean status;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public Invoices issueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public Invoices documentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
        return this;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getPaymentDeadline() {
        return paymentDeadline;
    }

    public Invoices paymentDeadline(LocalDate paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
        return this;
    }

    public void setPaymentDeadline(LocalDate paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public Float getGrossAmount() {
        return grossAmount;
    }

    public Invoices grossAmount(Float grossAmount) {
        this.grossAmount = grossAmount;
        return this;
    }

    public void setGrossAmount(Float grossAmount) {
        this.grossAmount = grossAmount;
    }

    public Float getToPay() {
        return toPay;
    }

    public Invoices toPay(Float toPay) {
        this.toPay = toPay;
        return this;
    }

    public void setToPay(Float toPay) {
        this.toPay = toPay;
    }

    public byte[] getDocumentFile() {
        return documentFile;
    }

    public Invoices documentFile(byte[] documentFile) {
        this.documentFile = documentFile;
        return this;
    }

    public void setDocumentFile(byte[] documentFile) {
        this.documentFile = documentFile;
    }

    public String getDocumentFileContentType() {
        return documentFileContentType;
    }

    public Invoices documentFileContentType(String documentFileContentType) {
        this.documentFileContentType = documentFileContentType;
        return this;
    }

    public void setDocumentFileContentType(String documentFileContentType) {
        this.documentFileContentType = documentFileContentType;
    }

    public Boolean isStatus() {
        return status;
    }

    public Invoices status(Boolean status) {
        this.status = status;
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoices)) {
            return false;
        }
        return id != null && id.equals(((Invoices) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Invoices{" +
            "id=" + getId() +
            ", issueDate='" + getIssueDate() + "'" +
            ", documentNumber='" + getDocumentNumber() + "'" +
            ", paymentDeadline='" + getPaymentDeadline() + "'" +
            ", grossAmount=" + getGrossAmount() +
            ", toPay=" + getToPay() +
            ", documentFile='" + getDocumentFile() + "'" +
            ", documentFileContentType='" + getDocumentFileContentType() + "'" +
            ", status='" + isStatus() + "'" +
            "}";
    }
}

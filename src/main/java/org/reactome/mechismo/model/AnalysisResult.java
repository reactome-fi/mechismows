package org.reactome.mechismo.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double pvalue;
    private double fdr;
    // As of 05/09/2018, change the fetch type to EAGER so that all values can be loaded.
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
//    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CancerType cancerType;
    
    public AnalysisResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getFdr() {
        return fdr;
    }

    public void setFdr(double fdr) {
        this.fdr = fdr;
    }

    public double getPvalue() {
        return pvalue;
    }

    public void setPvalue(double pvalue) {
        this.pvalue = pvalue;
    }

    public CancerType getCancerType() {
        return cancerType;
    }

    public void setCancerType(CancerType cancerType) {
        this.cancerType = cancerType;
    }

}

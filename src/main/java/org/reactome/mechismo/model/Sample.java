package org.reactome.mechismo.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model cancer samples.
 * @author wug
 *
 */
@Entity
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String source; // e.g. TCGA or COSMIC
    private String name; // For TCGA, this should be the barcode
    @ManyToOne(cascade= CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CancerType cancerType;
    
    public Sample() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public CancerType getCancerType() {
        return cancerType;
    }
    
    public void setCancerType(CancerType cancerType) {
        this.cancerType = cancerType;
    }
    
    @Override
    public String toString() {
        return (cancerType == null ? "null" : cancerType.getAbbreviation()) + ":" + name;
    }
    
}

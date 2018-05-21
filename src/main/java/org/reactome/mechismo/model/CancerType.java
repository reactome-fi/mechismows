package org.reactome.mechismo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Type of cancers (e.g. BRCA, COADREAD, OV). This list most likely will be pulled
 * from TCGA or COSMIC.
 * @author wug
 */
@Entity
public class CancerType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String abbreviation;
    private String name;
    
    public CancerType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}

package org.reactome.mechismo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Describe a mutation in an protein entity.
 * @author wug
 *
 */
@Entity
public class Mutation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // This should be a many to many relation sine a mutation
    // can have multiple samples and a sample can have multiple
    // mutations
    @ManyToMany(cascade= CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Sample> samples;
    // Residue is not shared among mutations
    @OneToOne(cascade= CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Residue residue;
    private String variant;
    
    public Mutation() {
    }

    public Set<Sample> getSamples() {
        return samples;
    }

    public void setSamples(Set<Sample> samples) {
        this.samples = samples;
    }
    
    public void addSamples(Set<Sample> samples) {
        if (samples == null || samples.size() == 0)
            return; // Nothing to be done
        if (this.samples == null)
            this.samples = new HashSet<>();
        this.samples.addAll(samples);
    }

    public Residue getResidue() {
        return residue;
    }

    public void setResidue(Residue residue) {
        this.residue = residue;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

}

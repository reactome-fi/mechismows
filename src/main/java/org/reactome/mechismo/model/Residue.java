package org.reactome.mechismo.model;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Describe a specific residue in a structure.
 * @author wug
 *
 */
@javax.persistence.Entity
public class Residue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int position;
    private String residue;
    private double mechismoScore;
    // Structure is not shared among residue obejcts
    @OneToOne(cascade= CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ResidueStructure structure;
    @ManyToOne(cascade= CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Entity protein;
    
    public Residue() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResidueStructure getStructure() {
        return structure;
    }

    public void setStructure(ResidueStructure structure) {
        this.structure = structure;
    }

    public Entity getProtein() {
        return protein;
    }

    public void setProtein(Entity protein) {
        this.protein = protein;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getResidue() {
        return residue;
    }

    public void setResidue(String residue) {
        this.residue = residue;
    }

    public double getMechismoScore() {
        return mechismoScore;
    }

    public void setMechismoScore(double mechismoScore) {
        this.mechismoScore = mechismoScore;
    }

}

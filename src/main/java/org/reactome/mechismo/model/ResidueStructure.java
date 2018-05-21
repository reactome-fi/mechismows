package org.reactome.mechismo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Describe the structure for one specific {@link Residue Residue}.
 * @author wug
 *
 */
@Entity
public class ResidueStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String pdb;
    private int pdbPosition;
    private String pdbChain;
    private String pdbResidue;
    
    public ResidueStructure() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPdb() {
        return pdb;
    }

    public void setPdb(String pdb) {
        this.pdb = pdb;
    }

    public int getPdbPosition() {
        return pdbPosition;
    }

    public void setPdbPosition(int pdbPosition) {
        this.pdbPosition = pdbPosition;
    }

    public String getPdbChain() {
        return pdbChain;
    }

    public void setPdbChain(String pdbChain) {
        this.pdbChain = pdbChain;
    }

    public String getPdbResidue() {
        return pdbResidue;
    }

    public void setPdbResidue(String pdbResidue) {
        this.pdbResidue = pdbResidue;
    }

}

package org.reactome.mechismo.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Describe targets invovled in interactions. They are usually proteins
 * but may be chemicals.
 * @author wug
 *
 */
@javax.persistence.Entity
public class Entity {
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name; // For protein, this is gene symbol
    private String identifier; // For protein, this is UniProt id
    
    public Entity() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    

}

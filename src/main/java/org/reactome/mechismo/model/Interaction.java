package org.reactome.mechismo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A very simple interaction really.
 * @author wug
 *
 */
@javax.persistence.Entity
@Table(indexes = {@Index(name = "name", columnList = "name")}) // For quick search based on name
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "name", unique = true, nullable = false)
    private String name; // A key for each search
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // Use this annotation based on https://stackoverflow.com/questions/24994440/no-serializer-found-for-class-org-hibernate-proxy-pojo-javassist-javassist
    // To solve lazy loading problem.
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Entity> partners;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Have to use ALL. Otherwise, mutations cannot be saved automatically for some reason.
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Mutation> mutations;
    // Use EAGER for update analysis results.
    // In most of use cases, analysisResults are needed to be loaded.
    // Therefore the fetch type is set to EAGER to make things easier (5/9/2018)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<AnalysisResult> analysisResults;
    
    public Interaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<AnalysisResult> getAnalysisResults() {
        return analysisResults;
    }

    public void setAnalysisResults(Set<AnalysisResult> analysisResults) {
        this.analysisResults = analysisResults;
    }
    
    public void addAnalysisResult(AnalysisResult result) {
        if (analysisResults == null)
            analysisResults = new HashSet<>();
        analysisResults.add(result);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Entity> getPartners() {
        return partners;
    }

    public void setPartners(Set<Entity> partners) {
        this.partners = partners;
    }

    public Set<Mutation> getMutations() {
        return mutations;
    }

    public void setMutations(Set<Mutation> mutations) {
        this.mutations = mutations;
    }
    
    public void addMutation(Mutation mutation) {
        if (mutations == null)
            mutations = new HashSet<>();
        mutations.add(mutation);
    }

}

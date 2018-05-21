package org.reactome.mechismo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Used to model reaction-based analysis results.
 * @author wug
 *
 */
@Entity
public class Reaction {
    @Id
    private Long id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AnalysisResult> analysisResults;
    
    public Reaction() {
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
}

package org.reactome.mechismo.dao;

import java.util.List;

import org.reactome.mechismo.model.CancerType;
import org.reactome.mechismo.model.Interaction;
import org.reactome.mechismo.model.Sample;

public interface InteractionDAO {
    
    public <T> List<T> list(Class<T> cls);
    
    public <T> long count(Class<T> cls);
    
    public <T> void update(T obj);
    
    public <T> void delete(T obj);
    
    public <T> void save(T obj);
     
    public CancerType loadCancerType(String abbreviation);
    
    public Sample loadSample(String name);
    
    /**
     * Query for a specific interaction based on the name.
     * @param name
     * @return
     */
    public Interaction queryInteraction(String name);
    
    public List<Interaction> queryInteractions(List<String> names);

    /**
     * Query stored interaction for the passed interaction.
     * @param interaction
     * @return
     */
    public Interaction queryInteraction(Interaction interaction);
    
    /**
     * Add an interaction to the database.
     * @param interaction
     */
    public void addInteraction(Interaction interaction);
    
    /**
     * Update the interaction in the database.
     * @param interaction
     */
    public void updateInteraction(Interaction interaction);
    
}

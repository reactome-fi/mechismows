package org.reactome.mechismo.service;

import java.util.List;

import org.reactome.mechismo.model.CancerType;
import org.reactome.mechismo.model.Interaction;
import org.reactome.mechismo.model.Sample;

public interface InteractionService {
    
//    public List<Interaction> listInteractions();
    
    public <T> List<T> list(Class<T> cls);
    
    public <T> long count(Class<T> cls);
    
    public <T> void update(T obj);
    
    public <T> void delete(T obj);
    
    public <T> void save(T obj);
    
    public Interaction queryInteraction(String name);
    
    public List<Interaction> queryInteractions(List<String> names);
    
    public void addInteraction(Interaction interaction);
    
    public CancerType loadCancerType(String abbreivation);
    
    public Sample loadSample(String name);

}

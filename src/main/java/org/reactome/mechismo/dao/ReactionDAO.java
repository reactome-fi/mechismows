package org.reactome.mechismo.dao;

import java.util.List;

import org.reactome.mechismo.model.Reaction;

public interface ReactionDAO {
    
    public Reaction fetchReaction(Long dbId);
    
    public void addReaction(Reaction reaction);
    
    public List<Reaction> fetchReactions(List<Long> dbIds);

}

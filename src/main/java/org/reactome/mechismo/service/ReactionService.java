package org.reactome.mechismo.service;

import java.util.List;

import org.reactome.mechismo.model.Reaction;

public interface ReactionService {
    
    public Reaction fetchReaction(Long dbId);
    
    public List<Reaction> fetchReactions(List<Long> dbIds);
    
    public void addReaction(Reaction reaction);

}

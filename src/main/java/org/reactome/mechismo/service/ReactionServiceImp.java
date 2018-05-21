package org.reactome.mechismo.service;

import java.util.List;

import org.reactome.mechismo.dao.ReactionDAO;
import org.reactome.mechismo.model.Reaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReactionServiceImp implements ReactionService {
    @Autowired
    private ReactionDAO reactionDAO;

    @Transactional(readOnly = true)
    @Override
    public Reaction fetchReaction(Long dbId) {
        return reactionDAO.fetchReaction(dbId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Reaction> fetchReactions(List<Long> dbIds) {
        return reactionDAO.fetchReactions(dbIds);
    }
    
    @Transactional
    @Override
    public void addReaction(Reaction reaction) {
        reactionDAO.addReaction(reaction);
    }

}

package org.reactome.mechismo.dao;

import java.util.List;

import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.reactome.mechismo.model.Reaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReactionDAOImp implements ReactionDAO {
    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public Reaction fetchReaction(Long dbId) {
        Session session = sessionFactory.getCurrentSession();
        Reaction reaction = session.load(Reaction.class, dbId);
        return reaction;
    }
    
    @Override
    public List<Reaction> fetchReactions(List<Long> dbIds) {
        Session session = sessionFactory.getCurrentSession();
        // Use the following new API in hibernate to load multiple instances
        // to avoid level 1 cache issue.
        MultiIdentifierLoadAccess<Reaction> access = session.byMultipleIds(Reaction.class);
        List<Reaction> reactions = access.multiLoad(dbIds);
        return reactions;
    }

    @Override
    public void addReaction(Reaction reaction) {
        Session session = sessionFactory.getCurrentSession();
        session.save(reaction);
    }

}

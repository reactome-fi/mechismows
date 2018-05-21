package org.reactome.mechismo.dao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.reactome.mechismo.model.CancerType;
import org.reactome.mechismo.model.Interaction;
import org.reactome.mechismo.model.Sample;
import org.reactome.r3.util.InteractionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InteractionDAOImp implements InteractionDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    public InteractionDAOImp() {
    }
    
    @Override
    public <T> void update(T obj) {
        Session session = sessionFactory.getCurrentSession();
        session.update(obj);
    }
    
    @Override 
    public <T> void save(T obj) {
        Session session = sessionFactory.getCurrentSession();
        session.save(obj);
    }
    
    @Override
    public <T> List<T> list(Class<T> cls) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<T> query = session.createQuery("FROM " + cls.getName(), cls);
        return query.getResultList();
    }

    @Override
    public CancerType loadCancerType(String abbreviation) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<CancerType> query = session.createQuery("FROM CancerType AS c WHERE c.abbreviation = :abbreviation", 
                                                           CancerType.class);
        query.setParameter("abbreviation", abbreviation);
        List<CancerType> cancerTypes = query.getResultList();
        return cancerTypes.isEmpty() ? null : cancerTypes.get(0);
    }

    @Override
    public Sample loadSample(String name) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Sample> query = session.createQuery("FROM Sample AS c WHERE c.name = :name", 
                                                        Sample.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    @Override
    public <T> long count(Class<T> cls) {
        Session session = sessionFactory.getCurrentSession();
        String queryText = "SELECT COUNT(o) FROM " + cls.getName() + " o";
        // Long will be returned from the above query
        TypedQuery<Long> query = session.createQuery(queryText, 
                                                     Long.class);
        Long total = query.getSingleResult();
        return total;
    }

    @Override
    public Interaction queryInteraction(String name) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Interaction> query = session.createQuery("FROM Interaction AS i WHERE i.name = :name", 
                                                            Interaction.class);
        query.setParameter("name", name);
        List<Interaction> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }
    
    @Override
    public List<Interaction> queryInteractions(List<String> names) {
        // Make sure the order of genes in in FI names are correct
        Set<String> set = names.stream()
                               .map(name -> name.split("\t")) // Expect delimit.
                               .map(tokens -> InteractionUtilities.generateFIFromGene(tokens[0], tokens[1]))
                               .collect(Collectors.toSet());
        // The following code may not be optimized
        Session session = sessionFactory.getCurrentSession();
        List<Interaction> interactions = session.createQuery("SELECT i FROM Interaction i WHERE i.name in :names", Interaction.class)
                                                .setParameter("names", set)
                                                .getResultList();
        return interactions;
    }

    @Override
    public Interaction queryInteraction(Interaction interaction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addInteraction(Interaction interaction) {
        Session session = sessionFactory.getCurrentSession();
        session.save(interaction);
    }

    @Override
    public void updateInteraction(Interaction interaction) {
        // TODO Auto-generated method stub
    }
    
}

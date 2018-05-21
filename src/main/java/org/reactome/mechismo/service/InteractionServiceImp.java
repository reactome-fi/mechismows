package org.reactome.mechismo.service;

import java.util.List;

import org.reactome.mechismo.dao.InteractionDAO;
import org.reactome.mechismo.model.CancerType;
import org.reactome.mechismo.model.Interaction;
import org.reactome.mechismo.model.Sample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InteractionServiceImp implements InteractionService {
    
    @Autowired
    private InteractionDAO interactionDAO;
    
    public InteractionServiceImp() {
    }
    
    @Transactional
    @Override
    public void addInteraction(Interaction interaction) {
        interactionDAO.addInteraction(interaction);
    }

    /**
     * This method is not supported since fetch eager is used:
     * too much data will be pulled out.
     * @return
     */
//    @Transactional(readOnly = true)
//    @Override
//    public List<Interaction> listInteractions() {
//        return interactionDAO.listInteractions();
//    }

    @Transactional(readOnly = true)
    @Override
    public <T> long count(Class<T> cls) {
        return interactionDAO.count(cls);
    }
    
    @Transactional(readOnly = true)
    @Override
    public Interaction queryInteraction(String name) {
        return interactionDAO.queryInteraction(name);
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<Interaction> queryInteractions(List<String> names) {
        return interactionDAO.queryInteractions(names);
    }

    @Transactional(readOnly = true)
    @Override
    public CancerType loadCancerType(String abbreivation) {
        return interactionDAO.loadCancerType(abbreivation);
    }

    @Transactional(readOnly = true)
    @Override
    public Sample loadSample(String name) {
        return interactionDAO.loadSample(name);
    }
    
    @Transactional(readOnly = true)
    @Override
    public <T> List<T> list(Class<T> cls) {
        return interactionDAO.list(cls);
    }
    
    @Transactional
    @Override
    public <T> void update(T obj) {
        interactionDAO.update(obj);
    }
    
    @Transactional
    @Override
    public <T> void save(T obj) {
        interactionDAO.save(obj);
    }

}

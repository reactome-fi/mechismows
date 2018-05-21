package org.reactome.mechismo.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.reactome.mechismo.model.AnalysisResult;
import org.reactome.mechismo.model.Reaction;

/**
 * This class is used to load reaction analysis results.
 * @author wug
 *
 */
public class MechismoReactionResultLoader extends MechismoResultLoader {
    private Map<Long, Reaction> idToReaction;
    
    public MechismoReactionResultLoader() {
        idToReaction = new HashMap<>();
    }
    
    public Collection<Reaction> getReactions() {
        return idToReaction.values();
    }

    @Override
    public void attachAnalysisResult(String[] tokens, 
                                     boolean isPanCancer,
                                     AnalysisResult result) {
        Reaction reaction = getReaction(tokens, isPanCancer);
        reaction.addAnalysisResult(result);
    }

    private Reaction getReaction(String[] tokens, boolean isPanCancer) {
        Long id = new Long(tokens[isPanCancer ? 0 : 1]);
        idToReaction.computeIfAbsent(id, key -> {
            Reaction reaction = new Reaction();
            reaction.setId(id);
            reaction.setName(tokens[isPanCancer ? 1 : 2]);
            return reaction;
        });
        return idToReaction.get(id);
    }

}

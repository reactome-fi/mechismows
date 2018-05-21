package org.reactome.mechismo.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.reactome.mechismo.model.AnalysisResult;
import org.reactome.mechismo.model.Interaction;
import org.reactome.r3.util.InteractionUtilities;

/**
 * This class is used to load analysis results for interactions.
 * @author wug
 *
 */
public class InteractionResultLoader extends MechismoResultLoader {
    private static final Logger logger = Logger.getLogger(InteractionResultLoader.class.getName());
    private Map<String, Interaction> nameToInteraction;
    
    public InteractionResultLoader() {
        nameToInteraction = new HashMap<>();
    }
    
    public Collection<Interaction> getInteractions() {
        return nameToInteraction.values();
    }

    @Override
    public void attachAnalysisResult(String[] tokens,
                                     boolean isPanCancer,
                                     AnalysisResult result) {
        Interaction interaction = getInteraction(tokens, isPanCancer);
        if (interaction == null)
            return;
        interaction.addAnalysisResult(result);
    }
    
    private Interaction getInteraction(String[] tokens,
                                       boolean isPanCancer) {
        int index = 0;
        if (!isPanCancer)
            index = 1;
        String gene1 = tokens[index];
        String gene2 = tokens[index + 1];
        String fi = InteractionUtilities.generateFIFromGene(gene1, gene2);
        // FIs between proteins and DNA/RNA have not been loaded
        if (fi.contains("[DNA/RNA]"))
            return null;
        // Use cached values
        Interaction interaction = nameToInteraction.get(fi);
        if (interaction != null)
            return interaction;
        interaction = interactionService.queryInteraction(fi);
        if (interaction == null) {
//            throw new IllegalStateException("Cannot find interaction: " + fi);
            logger.warning("Cannot find interaction for " + fi);
            return null;
        }
        nameToInteraction.put(fi, interaction);
        return interaction;
    }

}

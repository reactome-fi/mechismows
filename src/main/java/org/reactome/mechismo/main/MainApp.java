package org.reactome.mechismo.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.Test;
import org.reactome.mechismo.config.AppConfig;
import org.reactome.mechismo.model.AnalysisResult;
import org.reactome.mechismo.model.Entity;
import org.reactome.mechismo.model.Interaction;
import org.reactome.mechismo.model.Reaction;
import org.reactome.mechismo.service.InteractionService;
import org.reactome.mechismo.service.ReactionService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Use this class for generating the database according to the following methods running:
 * 0). If the database is empty, make sure hibernate.hbm2ddl.auto=create in db.properties in the resource folder
 * and run main() after commenting out loadInteractions() statement.
 * 1). main() by passing the file name for mechismo input (make sure hibernate.hbm2ddl.auto=none in db.properties)
 * 2). checkEntities() to fix some entity names
 * 3). updateInteractionName() to fix some interaction names
 * 4). loadInteractionAnalysisResults() to load analysis results for interactions
 * 5). loadReactionAnalysisResults() for load reaction analysis results.
 * 6). If the last two steps are needed to be reloaded, run deleteAnalysisResults() first after updating the LazyType to EAGER.
 * @author wug
 *
 */
public class MainApp {
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());
    private static final String RESULT_DIR = "/Users/wug/datasets/Mechismo/FrancescoResults/080918/";
    
    /**
     * Run this method to load all interactions into the database.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        InteractionService interactionService = context.getBean(InteractionService.class);
       
        long total = interactionService.count(Interaction.class);
        logger.info("Total interactions before adding: " + total);
        
//        if (true) {
//            context.close();
//            return;
//        }
        
        loadInteractions(args, interactionService);
        context.close();
     }
    
    @Test
    public void queryAnalysisResults() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        InteractionService interactionService = context.getBean(InteractionService.class);
//        String fi = "NRAS\tSOS1";
        String fi = "BRAF\tMAP2K1";
        Interaction interaction = interactionService.queryInteraction(fi);
        System.out.println("Interaction: " + interaction.getName());
        Set<AnalysisResult> results = interaction.getAnalysisResults();
        results.forEach(result -> {
            System.out.println(result.getCancerType().getAbbreviation() + "\t" + result.getPvalue() + "\t" + result.getFdr());
        });
        context.close();
    }
    
   /**
     * Remove interaction names having {ECO:. This needs to be run after loading all interactions
     * via main().
     * Note: ECO is for Evidence Ontology.
     * @throws Exception
     */
    @Test
    public void updateInteractionName() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        InteractionService interactionService = context.getBean(InteractionService.class);
        List<Interaction> interactions = interactionService.list(Interaction.class);
        System.out.println("Total interactions: " + interactions.size());
        // To be updated
        List<Interaction> toBeUpdated = interactions.stream()
                                                    .filter(interaction -> interaction.getName().contains("{ECO:"))
                                                    .collect(Collectors.toList());
        System.out.println("ToBeUpdated: " + toBeUpdated.size());
        toBeUpdated.forEach(interaction -> {
            String name = interaction.getName();
            System.out.println(name);
            String[] names = name.split("\t");
            int index = names[0].indexOf("{ECO:");
            if (index > 0)
                names[0] = names[0].substring(0, index).trim();
            index = names[1].indexOf("{ECO:");
            if (index > 0)
                names[1] = names[1].substring(0, index).trim();
            name = names[0] + "\t" + names[1];
            System.out.println("New name: " + name);
            interaction.setName(name);
            interactionService.update(interaction);
        });
        context.close();
    }
    
    /**
     * This method is used to fix some gene names errors in the output.
     * @throws Exception
     */
    @Test
    public void checkEntities() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        InteractionService interactionService = context.getBean(InteractionService.class);
        List<Entity> entities = interactionService.list(Entity.class);
        System.out.println("Total entities: " + entities.size());
        Set<String> names = entities.stream()
                                    .filter(entity -> entity.getName().contains("{ECO"))
                                    .map(entity -> entity.getName())
                                    .map(name -> name.split(" ")[0])
                                    .collect(Collectors.toSet());
        System.out.println("Total names: " + names.size());
        List<Entity> toBeUpdated = new ArrayList<>();
        entities.forEach(entity -> {
            String name = entity.getName();
            if (name.contains("{ECO")) {
                toBeUpdated.add(entity);
                return;
            }
            if (names.contains(name))
                System.out.println("Duplicated: " + name);
        });
        
        System.out.println("Update entities now:");
        toBeUpdated.forEach(entity ->  {
            logger.info("Updating " + entity.getName());
            String name = entity.getName();
            name = name.split(" ")[0];
            entity.setName(name);
            interactionService.update(entity);
        });
        context.close();
    }
    
    /**
     * To make the following method work, change the following setting in Reaction class:
     * @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY): LAZY should be EAGER.
     * private Set<AnalysisResult> analysisResults;
     * @throws Exception
     */
    @Test
    public void loadReactionAnalysisResults() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        InteractionService interactionService = context.getBean(InteractionService.class);
        long count = interactionService.count(Reaction.class);
        logger.info("Total reactions before parsing: " + count);
        
        Collection<Reaction> reactionsToBeLoaded = getReactionsToBeLoaded(interactionService);
        logger.info("Total reactions to be loaded: " + reactionsToBeLoaded.size());
        ReactionService reactionService = context.getBean(ReactionService.class);
        for (Reaction reaction : reactionsToBeLoaded) {
            logger.info("Saving reaction " + reaction.getId() + "...");
            reactionService.addReaction(reaction);
        }
        context.close();
    }
    
    /**
     * Delete the loaded analysis results for re-loading without re-generating the database.
     * Make sure FetcthType is EAGER in both Reaction and Interaction.
     * @throws Exception
     */
    @Test
    public void deleteAnalysisResults() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        InteractionService interactionService = context.getBean(InteractionService.class);
        List<Interaction> interactions = interactionService.list(Interaction.class);
        logger.info("Total interactions: " + interactions.size());
        for (Interaction i : interactions) {
            if (i.getAnalysisResults() == null || i.getAnalysisResults().size() == 0)
                continue;
            i.setAnalysisResults(null); // reset the analysis results
            interactionService.update(i);
        }
        List<Reaction> reactions = interactionService.list(Reaction.class);
        logger.info("Total reactions: " + reactions.size());
        for (Reaction reaction : reactions) {
            interactionService.delete(reaction); // Delete the reaction directly so that it can be reloaded again.
        }
        List<AnalysisResult> results = interactionService.list(AnalysisResult.class);
        logger.info("Total AnalysisResult: " + results.size());
        for (AnalysisResult result : results)
            interactionService.delete(result);
        context.close();
    }
    
    private Collection<Reaction> getReactionsToBeLoaded(InteractionService service) throws Exception {
//        String dir = "datasets/Mechismo/FrancescoResults/041018/";
        // Updated results
//        String dir = "datasets/Mechismo/FrancescoResults/051118/";
        String cancerWiseResult = RESULT_DIR + "tcga_mechismo_stat_cancer_wise_reactions.tsv";
        String pancancerResult = RESULT_DIR + "tcga_mechismo_stat_pancancer_reactions.tsv";
        MechismoReactionResultLoader loader = new MechismoReactionResultLoader();
        loader.setInteractionService(service);
        loader.parseResults(cancerWiseResult, false);
        loader.parseResults(pancancerResult, true);
        return loader.getReactions();
    }
    
    /**
     * To make this work, have to change the following setting in Interaction class
     * @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY): Lazy should be EAGER
     * @throws Exception
     */
    @Test
    public void loadInteractionAnalysisResults() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        InteractionService interactionService = context.getBean(InteractionService.class);
        Collection<Interaction> interactionsToBeUpdated = getInteractionsToBeUpdated(interactionService);
        logger.info("Total interactions to be updated: " + interactionsToBeUpdated.size());
        interactionsToBeUpdated.forEach(interaction -> {
            logger.info("Update interaction: " + interaction.getName());
            interactionService.update(interaction);
        });
        context.close();
    }
    
    private Collection<Interaction> getInteractionsToBeUpdated(InteractionService service) throws Exception {
//        String dir = "datasets/Mechismo/FrancescoResults/041018/";
//        String cancerWiseResult = dir + "tcga_mechismo_stat_cancer_wise.tsv";
//        String pancancerResult = dir + "tcga_mechismo_stat_pancancer.tsv";
        // Results updated on May 18, 2018 without considering directions.
//        String dir = "datasets/Mechismo/FrancescoResults/051118/";
        String cancerWiseResult = RESULT_DIR + "tcga_mechismo_stat_cancer_wise_undirected.tsv";
        String pancancerResult = RESULT_DIR + "tcga_mechismo_stat_pancancer_undirected.tsv";
        InteractionResultLoader loader = new InteractionResultLoader();
        loader.setInteractionService(service);
        loader.parseResults(cancerWiseResult, false);
        loader.parseResults(pancancerResult, true);
        return loader.getInteractions();
    }

    private static void loadInteractions(String[] args, InteractionService interactionService) throws IOException {
        MechismoOutputParser parser = new MechismoOutputParser();
        parser.parseOutput(args[0]);
        Set<Interaction> parsedInteractions = parser.getInteractions();
        logger.info("Total interactions to be saved: " + parsedInteractions.size());
        parsedInteractions.forEach(interaction -> {
            logger.info("Saving " + interaction.getName() + "...");
//            try {
//                parser.checkInteraction(interaction);
//            }
//            catch(JsonProcessingException e) {}
            interactionService.addInteraction(interaction);
        });
        
        long total = interactionService.count(Interaction.class);
        logger.info("Total interactions after adding: " + total);
    }

}

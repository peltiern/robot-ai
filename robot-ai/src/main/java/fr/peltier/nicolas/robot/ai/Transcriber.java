package fr.peltier.nicolas.robot.ai;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

/**
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class Transcriber {
    public static void main(String[] args) throws Exception {
        System.out.println("Loading models...");

        Configuration configuration = new Configuration();

        // Load model from the jar
        configuration.setAcousticModelPath("file:/home/npeltier/Robot/Programme/reconnaissance-vocale/lium_french_f0");
        
        // You can also load model from folder
        // configuration.setAcousticModelPath("file:en-us");
        
//        configuration.setDictionaryPath("file:/home/npeltier/Robot/Programme/reconnaissance-vocale/lium_french_f0/dict/simple_french_2.dic");
        configuration.setDictionaryPath("file:/home/npeltier/Robot/Programme/reconnaissance-vocale/lium_french_f0/dict/frenchWords62K.dic");
        
        configuration.setLanguageModelPath("file:/home/npeltier/Robot/Programme/reconnaissance-vocale/lium_french_f0/dict/french3g62K.lm.dmp");

        LiveSpeechRecognizer recognizer = 
            new LiveSpeechRecognizer(configuration);
        recognizer.startRecognition(true);

        SpeechResult result;

        while ((result = recognizer.getResult()) != null) {
        
            recognizer.stopRecognition();
            System.out.format("Hypothesis: %s\n",
                              result.getHypothesis());
                              
            System.out.println("List of recognized words and their times:");
            for (WordResult r : result.getWords()) {
            System.out.println(r);
            }

            System.out.println("Best 3 hypothesis:");            
            for (String s : result.getNbest(3))
                System.out.println(s);

            System.out.println("Lattice contains " + result.getLattice().getNodes().size() + " nodes");
            Thread.sleep(10000);
            recognizer.startRecognition(true);
        }

        recognizer.stopRecognition();
    }
}

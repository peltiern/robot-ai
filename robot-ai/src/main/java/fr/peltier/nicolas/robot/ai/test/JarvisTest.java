package fr.peltier.nicolas.robot.ai.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import javaFlacEncoder.FLACFileWriter;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.google.common.io.Files;

/**
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class JarvisTest {

//    public static void main(String[] args){
//        GSpeechDuplex dup = new GSpeechDuplex("AIzaSyDJS4zpAAUwj-Bnctf6PoCPxQ4jXIQJtHE");//Instantiate the API
//        dup.addResponseListener(new GSpeechResponseListener(){// Adds the listener
//            public void onResponse(GoogleResponse gr){
//                System.out.println("Google thinks you said: " + gr.getResponse());
//                System.out.println("with " + 
//                ((gr.getConfidence()!=null)?(Double.parseDouble(gr.getConfidence())*100):null) 
//                    + "% confidence.");
//                System.out.println("Google also thinks that you might have said:" 
//                        + gr.getOtherPossibleResponses());
//            }
//        });
//        Microphone mic = new Microphone(FLACFileWriter.FLAC);//Instantiate microphone and have 
//        // it record FLAC file.
//        File file = new File("CRAudioTest.flac");//The File to record the buffer to. 
//        //You can also create your own buffer using the getTargetDataLine() method.
//        while(true){
//            try{
//                mic.captureAudioToFile(file);//Begins recording
//                Thread.sleep(10000);//Records for 10 seconds
//                mic.close();//Stops recording
//                //Sends 10 second voice recording to Google
//                byte[] data = Files.toByteArray(mic.getAudioFile());//Saves data into memory.
//                        dup.recognize(data, (int)mic.getAudioFormat().getSampleRate());
//                mic.getAudioFile().delete();//Deletes Buffer file
//                //REPEAT
//            }
//            catch(Exception ex){
//                ex.printStackTrace();//Prints an error if something goes wrong.
//            }
//        }
//    }
    
    public static void main(String[] args){
        try {
            Runtime.getRuntime().exec("google-chrome https://localhost:8080/speech.html");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

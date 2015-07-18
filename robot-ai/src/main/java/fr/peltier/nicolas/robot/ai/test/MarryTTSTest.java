package fr.peltier.nicolas.robot.ai.test;

import java.io.IOException;
import java.util.Locale;

import javax.sound.sampled.AudioInputStream;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.client.RemoteMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;
import marytts.util.data.audio.MaryAudioUtils;

public class MarryTTSTest {

    /**
     * @param args
     * @throws MaryConfigurationException 
     * @throws SynthesisException 
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException, SynthesisException, MaryConfigurationException {
//        MaryInterface marytts = new RemoteMaryInterface("localhost", 59125);
        MaryInterface marytts = new LocalMaryInterface();
        System.out.println("I currently have " + marytts.getAvailableVoices() + " voices in "
                + marytts.getAvailableLocales() + " languages available.");
        marytts.setLocale(Locale.FRENCH);
        marytts.setVoice("enst-camille");
        marytts.setAudioEffects("Robot(amount=100),Volume(amount=1.3)");
        System.out.println("Audio effects = " + marytts.getAudioEffects());
        AudioInputStream audio = marytts.generateAudio("Je suis une voix de synthèse en cours d'expérimentation. Arrives-tu à me comprendre ? Quel est ton nom ? Tu t'appelles Nicolas. Je suis un robot. Pour l'instant, je n'éprouve aucune émotion.");
        AudioPlayer ap = new AudioPlayer();
        ap.setAudio(audio);
        ap.start();
//        MaryAudioUtils.writeWavFile(MaryAudioUtils.getSamplesAsDoubleArray(audio), "/home/npeltier/test-MaryTTS-femme.wav", audio.getFormat());

    }

}

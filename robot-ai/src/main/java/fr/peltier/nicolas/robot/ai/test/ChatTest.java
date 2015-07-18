package fr.peltier.nicolas.robot.ai.test;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

/**
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class ChatTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        System.setProperty("robot.dir", "/home/npeltier/Robot/Programme");
     // Création du chat
        String botname="amy";
        String path=System.getProperty("robot.dir");
        Bot bot = new Bot(botname, path);
        final Chat chat = new Chat(bot);
        final String reponse = chat.multisentenceRespond("ACTION(PARLER#CommaESSAI#CommaPHRASE)");
        System.out.println("Reponse = " + reponse);

    }

}

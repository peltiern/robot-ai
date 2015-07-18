package fr.peltier.nicolas.robot.ai.test.reconnaissance;

/**
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class LFWBean {

    private String personne;
    
    public String getPersonne() {
        return personne;
    }

    public void setPersonne(String personne) {
        this.personne = personne;
    }

    public String getNumeroImage() {
        return numeroImage;
    }

    public void setNumeroImage(String numeroImage) {
        this.numeroImage = numeroImage;
    }

    public double getMale() {
        return male;
    }

    public void setMale(double male) {
        this.male = male;
    }

    public double getSourire() {
        return sourire;
    }

    public void setSourire(double sourire) {
        this.sourire = sourire;
    }

    public double getFilleAttirante() {
        return filleAttirante;
    }

    public void setFilleAttirante(double filleAttirante) {
        this.filleAttirante = filleAttirante;
    }

    private String numeroImage;
    
    private double male;
    
    private double sourire;
    
    private double filleAttirante;
}

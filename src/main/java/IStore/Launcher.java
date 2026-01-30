package IStore;

/**
 * Classe de lancement pour l'application iStore.
 * Cette classe permet de contourner le problème de JavaFX runtime
 * lorsque l'application n'est pas lancée via Maven.
 *
 * @author IStore Team
 * @version 1.0
 */
public class Launcher {

    /**
     * Point d'entrée qui délègue à Main.main()
     *
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}

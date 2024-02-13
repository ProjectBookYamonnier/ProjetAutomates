/**
 * La classe LexVelo implemente un analyseur lexical pour une application de location de velos
 * A COMPLETER
 * 
 * @author Yann MONNIER, Renan LAUTRAM
 * janvier 2024
 */

import java.io.InputStream;

public class LexVelo extends Lex {
	
	/** codage des items lexicaux */
	protected final int
		ADULTE = 0, DEBUT = 1, ENFANT = 2, FIN = 3, HEURES = 4, IDENT = 5, NBENTIER = 6, VIRG = 7,
		PTVIRG = 8, BARRE = 9, AUTRES = 10;
	/** tableau de l'affichage souhaite des items lexicaux */
	public static final String[] images = { "ADULTE", "DEBUT", "ENFANT", "FIN", "HEURES", "IDENT", 
		"NBENT", "VIRG", "PTVIRG", "BARRE", "AUTRE" };
 
	/** nombre de mots reserves dans l'application Velo */
	private final int NBRES = 5 ;

	/** attributs lexicaux associes resp. a NBENTIER et IDENT */
	private int valEnt, numIdCourant ; 
	
	/** methodes d'acces aux attributs lexicaux */
	public int getvalEnt() {
		return this.valEnt;
	}
	public int getNumIdCourant() {
		return this.numIdCourant;
	}

	/** caractere courant */
	private char carLu ; 
	/** methode d'acces a l'attribut caractere courant */
	public char getCarLu(){
		return this.carLu;
	}
    
    /** constructeur classe LexVelo 
     * 
     * @param flot : donnee d'entree a analyser 
     * */
    public LexVelo(InputStream flot) {
	 	/** initialisation du flot par la classe abstraite */
    	super(flot);
    	
    	/** prelecture du premier caractere de la donnee */
    	lireCarLu();
    	
    	/** initialisation de tabIdent par mots reserves de l'application Velo */
    	this.tabIdent.add(0, "ADULTE"); this.tabIdent.add(1, "DEBUT") ; this.tabIdent.add(2, "ENFANT") ; 
    	this.tabIdent.add(3, "FIN") ; this.tabIdent.add(4, "HEURES") ;
    	
    	System.out.println(this.tabIdent);
    }

	/** methode de lecture d'un nouveau caractere courant carLu
	 * a partir de la donnee en entree flot
	*/
    private void lireCarLu() {
		carLu = Lecture.lireChar(flot);
		this.notifyObservers(carLu);
		/** transformation de tous les caracteres de separation en espaces et forcage lettres en majuscules */
		if ((carLu == '\r') || (carLu == '\n') || (carLu == '\t'))
			carLu = ' ';
		if (Character.isWhitespace(carLu))
			carLu = ' ';
		else
			carLu = Character.toUpperCase(carLu);
	} 


	/** definition de la methode abstraite lireSymb de Lex 
	 * 
	 * 	@return code de l'item lexical detecte
	 * */ 
	public int lireSymb() {

		/** on "avale" espaces ou assimiles */
		while (carLu == ' ')
			lireCarLu();
		if ((carLu >= '0') && (carLu <= '9'))
			return lireEnt();
		String s = lireStr();
		Boolean mot = false;
		if(s.length() > 1) mot = true;
		if (mot) {
			switch (s) {
				case "ADULTE" : return ADULTE;
				case "DEBUT" : return DEBUT;
				case "ENFANT" : return ENFANT;
				case "FIN" : return FIN;
				case "HEURES" : return HEURES;
				default : 
					if(!tabIdent.contains(s)) 
						this.tabIdent.add(s); 
					this.numIdCourant = this.tabIdent.indexOf(s); 
					return IDENT;
			}
		}else {
			switch(s) {
				case "," : return VIRG;
				case ";" : return PTVIRG;
				case "/" : return BARRE;
				default : return AUTRES;
		}
	}

	}
	
	/**
	 * lecture item lexical NBENTIER et maj de l'attribut lexical valEnt
	 * @return
	 */
	private String lireStr() {
		String s = "";
		do {
			s = s + carLu; 
			lireCarLu();
		}while(((carLu >= 'a') && (carLu <= 'z')) || ((carLu >= 'A') && (carLu <= 'Z')));
		return s;
	}

	/**
	 * lecture item lexical NBENTIER et maj de l'attribut lexical valEnt
	 * @return
	 */
	private int lireEnt() {
		String s = "";
		do {
			s = s + carLu; lireCarLu();
		}while((carLu >= '0') && (carLu <= '9'));
		valEnt = Integer.parseInt(s);
		return NBENTIER;
	}
	
	/** definition de la methode abstraite chaineIdent de Lex 
	 * 
	 * @param numIdent : numero d'un ident dans la table des idents
	 * @return chaine correspondant a numIdent
	 * */
	public String chaineIdent(int numIdent) {
		return this.tabIdent.get(numIdent);
	} 
	
	/** utilitaire de test de l'analyseur lexical seul (sans analyse syntaxique) */
	private void testeur_lexical() {
		/** Unite lexicale courante */
		int token;
		/** definition du caractere de fin de chaine 
		 * utile uniquement pour test autonome du lexical*/ 
		int finDeChaine = BARRE;
		do {
			token = lireSymb();
			if (token == NBENTIER)
				Lecture.attenteSurLecture("token : " + images[token] + " attribut valEnt = " + valEnt);
			else if (token == IDENT)
				Lecture.attenteSurLecture("token : " + images[token] + " attribut numIdCourant = " + numIdCourant
						+ " chaine associee = " + chaineIdent(numIdCourant));
			else
				Lecture.attenteSurLecture("token : " + images[token]);
		} while (token != finDeChaine);
	}

	
	/**
	 * Main pour tester l'analyseur lexical seul (sans analyse syntaxique)
	 */
	public static void main(String args[]) {
		String nomfich;
		nomfich = Lecture.lireString("nom du fichier d'entree : ");
		InputStream flot = Lecture.ouvrir(nomfich);
		if (flot == null) {
			System.exit(0);
		}

		LexVelo testVelo = new LexVelo(flot);
		testVelo.testeur_lexical();
		
		Lecture.fermer(flot);
		Lecture.attenteSurLecture("fin d'analyse");
		System.exit(0);
		
	}


}

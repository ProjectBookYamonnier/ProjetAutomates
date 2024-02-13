import java.io.*;
/**
 * La classe AutoVelo met en oeuvre l'automate d'analyse syntaxique des locations de velos
 * Realisation par interpreteur de tables
 * A COMPLETER
 * 
 * @author LAUTRAM Renan, MONNIER Yann
 * janvier 2024
 */

public class AutoVelo extends Automate{
	
	/**
	 * RAPPEL: reprise après erreur demandee sur les items lexicaux VIRG, PTVIRG et BARRE
	 */

	/** table des transitions */
	
	private static final int[][] TRANSIT = {
		/* Etat      ADULTE DEBUT ENFANT   FIN   HEURES  IDENT  NBENTIER  VIRG PTVIRG  BARRE AUTRES  */
		/* 0 */     {  13,   13,    13,     13,    13,     1,      13,     13,    13,    12,    13   },
		/* 1 */     {  13,    5,    13,      4,    13,    13,       2,     13,    13,    13,    13   },
		/* 2 */     {  13,   13,    13,     13,     3,    13,      13,     13,    13,    13,    13   },
		/* 3 */     {  13,    5,    13,      4,    13,    13,      13,     13,    13,    13,    13   },
		/* 4 */     {  13,   13,    13,     13,    13,    13,      13,     11,     0,    13,    13   },
		/* 5 */     {  13,   13,    13,     13,    13,    13,       6,     13,    13,    13,    13   },
		/* 6 */     {   8,   13,     7,     13,    13,    13,      13,     13,    13,    13,    13   },
		/* 7 */     {  13,   13,    13,     13,    13,    13,      13,     11,     0,    13,    13   },
		/* 8 */     {  13,   13,    13,     13,    13,    13,       9,     11,     0,    13,    13   },
		/* 9 */     {  13,   13,    10,     13,    13,    13,      13,     13,    13,    13,    13   },
		/*10 */     {  13,   13,    13,     13,    13,    13,      13,     11,     0,    13,    13   },
		/*11 */     {  13,   13,    13,     13,    13,     1,      13,     13,    13,    13,    13   },
		/*12 */     {  12,   12,    12,     12,    12,    12,      12,     12,    12,    12,    12   },
 /*ERREUR 13 */     {  13,   13,    13,     13,    13,    13,      13,      0,     0,    12,    13   },
	};

	/** gestion de l'affichage sur la fenetre de trace de l'execution */
	public void newObserver(ObserverAutomate oAuto, ObserverLexique oLex ){
		this.newObserver(oAuto);
		this.analyseurLexical.newObserver(oLex);
		analyseurLexical.notifyObservers(((LexVelo)analyseurLexical).getCarLu());
	}
	/** fin gestion de l'affichage sur la fenetre de trace de l'execution */

	/**
	 *  constructeur classe AutoVelo pour l'application Velo
	 *  
	 * @param flot : donnee a analyser
	 */
	public AutoVelo(InputStream flot) {
		/** on utilise ici un analyseur lexical de type LexVelo */
		analyseurLexical = new LexVelo(flot);
		/** initialisation etats particuliers de l'automate fini d'analyse syntaxique*/
		this.etatInitial = 0;
		this.etatFinal = TRANSIT.length;
		this.etatErreur = TRANSIT.length - 1;
	}

	/** definition de la methode abstraite getTransition de Automate 
	 * 
	 * @param etat : code de l'etat courant
	 * @param unite : code de l'unite lexicale courante
	 * @return code de l'etat suivant
	 **/
	int getTransition(int etat, int unite) {
		return this.TRANSIT[etat][unite];
	}

	/** ici la methode abstraite faireAction de Automate n'est pas encore definie */
	void faireAction(int etat, int unite) {};

	/** ici la methode abstraite initAction de Automate n'est pas encore definie */
	void initAction() {};

	/** ici la methode abstraite getAction de Automate n'est pas encore definie */
	int getAction(int etat, int unite) {return 0;};

}

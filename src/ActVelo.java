import java.io.InputStream;
import java.util.ArrayList;


/**
 * La classe ActVelo met en oeuvre les actions de l'automate d'analyse syntaxique des locations de velos
 * 
 * @author Renan LAUTRAM, Yann MONNIER
 * janvier 2024
 */


public class ActVelo extends AutoVelo {

	/** table des actions */
	private final int[][] ACTION =
		{ 	/* Etat      ADULTE DEBUT ENFANT   FIN   HEURES  IDENT  NBENTIER  VIRG PTVIRG  BARRE AUTRES  */
			/* 0 */     {  16,   16,    16,     16,    16,     1,      16,    16,   16,    12,    16   },
			/* 1 */     {  16,    5,    16,      4,    16,    16,       3,    16,   16,    16,    16   },
			/* 2 */     {  16,   16,    16,     16,    -1,    16,      16,    16,   16,    16,    16   },
			/* 3 */     {  16,    5,    16,      4,    16,    16,      16,    16,   16,    16,    16   },
			/* 4 */     {  16,   16,    16,     16,    16,    16,      16,    15,   14,    16,    16   },
			/* 5 */     {  16,   16,    16,     16,    16,    16,       6,    16,   16,    16,    16   },
			/* 6 */     {   7,   16,     8,     16,    16,    16,      16,    16,   16,    16,    16   },
			/* 7 */     {  16,   16,    16,     16,    16,    16,      16,    11,    2,    16,    16   },
			/* 8 */     {  16,   16,    16,     16,    16,    16,       9,    10,    2,    16,    16   },
			/* 9 */     {  16,   16,    -1,     16,    16,    16,      16,    16,   16,    16,    16   },
			/* 10*/     {  16,   16,    16,     16,    16,    16,      16,    13,    2,    16,    16   },
			/* 11*/     {  16,   16,    16,     16,    16,     1,      16,    16,   16,    16,    16   },
			/* 12*/     {  16,   16,    16,     16,    16,    16,      16,    16,   16,    12,    16   },
	  /*ERREUR 13*/     {  -1,   -1,    -1,     -1,    -1,    -1,      -1,    -1,   14,    12,    -1   },
		} ;      

	/** constructeur classe ActVelo
	 * @param flot : donnee a analyser
	 *  */
	public ActVelo(InputStream flot) {
		super(flot);
	}
	

	/** definition de la methode abstraite getAction de Automate 
	 * 
	 * @param etat : code de l'etat courant
	 * @param unite : code de l'unite lexicale courante
	 * @return code de l'action suivante
	 **/
	public int getAction(int etat, int unite) {
		return ACTION[etat][unite];
	}

	/**
	 * definition methode abstraite initAction de Automate
	 */
	public void initAction() {
		/**  correspond a l'action 0 a effectuer a l'init */
		initialisations();
	}

	/** definition de la methode abstraite faireAction de Automate 
	 * 
	 * @param etat : code de l'etat courant
	 * @param unite : code de l'unite lexicale courante
	 * @return code de l'etat suivant
	 **/
	public void faireAction(int etat, int unite) {
		executer(ACTION[etat][unite]);
	}

	/** types d'erreurs detectees */
	private static final int FATALE = 0, NONFATALE = 1;
	
	/** gestion des erreurs 
	 * @param tErr type de l'erreur (FATALE ou NONFATALE)
	 * @param messErr message associe a l'erreur
	 */
	private void erreur(int tErr, String messErr) {
		Lecture.attenteSurLecture(messErr);
		switch (tErr) {
		case FATALE:
			errFatale = true;
			break;
		case NONFATALE:
			etatCourant = etatErreur;
			break;
		default:
			Lecture.attenteSurLecture("parametre incorrect pour erreur");
		}
	}

	/** attribut sauvegardant l'ensemble des locations en cours (non terminees) */
	private BaseDeLoc maBaseDeLoc = new BaseDeLoc();

	
	/** nombre de velos initialement disponibles */
	private static final int MAX_VELOS_ADULTES = 50, MAX_VELOS_ENFANT = 20;
	

	/**
	 * acces a un attribut lexical 
	 * cast pour preciser que analyseurLexical est de type LexVelo
	 * @return valEnt associe a l'unite lexicale NBENTIER
	 */
	private int valEnt() {
		return ((LexVelo)analyseurLexical).getvalEnt();
	}
	/**
	 * acces a un attribut lexical 
	 * cast pour preciser que analyseurLexical est de type LexVelo
	 * @return numIdCourant associe a l'unite lexicale IDENT
	 */
	private int numId() {
		return ((LexVelo)analyseurLexical).getNumIdCourant();
	}


	
	/** variables 
	 * a prevoir pour actions */ 

	// Rappel: chaque <Validation> correspond a un jour different
	// jourCourant correspond a la <Validation> en cours d'analyse
	private int jourCourant=1;
	
	// Rappel: chaque <Validation> est composee de plusieurs operations 
	// nbOperationTotales correspond a toutes les operations contenues dans la donnee a analyser
	// erronees ou non
	private int nbOperationTotales;
	
	// nbOperationCorrectes correspond a toutes les operations sans erreur 
	// de la donnee a analyser
	private int nbOperationCorrectes;
	
	// ensemble des clients differents vus chaque jour 
	// clientsParJour.get(i) donne l'ensemble des clients differents vus le ieme jour
	//		(NB: SmallSet.class fourni dans libClass_UtilitairesVelo)
	private ArrayList<SmallSet> clientsParJour;
	private String nom_S;
	private int heureDebut;
	private int qteAdulte;
	private int qteEnfant;
	private int stock;
	private boolean heures;
	private int stockAdultes;
	private int stockEnfants;
	private int prix;
	private boolean executer;
	
	/**
	 * initialisations a effectuer avant les actions
	 */
	private void initialisations() {
		nbOperationCorrectes = 0; nbOperationTotales = 0;
		clientsParJour=new ArrayList<SmallSet>();
		/** initialisation clients du premier jour
		 * NB: le jour 0 n'est pas utilise */
		clientsParJour.add(0,new SmallSet()); 
		clientsParJour.add(1,new SmallSet()); 	
		nom_S = "";
		heureDebut = 0;
		qteAdulte = 0;
		qteEnfant = 0;
		stock = 0;
		heures = false;
		stockAdultes = MAX_VELOS_ADULTES;
		stockEnfants = MAX_VELOS_ENFANT;
		prix = 0;
		executer = true;
		
	} // fin initialisations



	/**
	 * execution d'une action
	 * @param numAction :  numero de l'action a executer
	 */
	public void executer(int numAction) {
//		System.out.println("etat  " + etatCourant + "  action  " + numAction);

		switch (numAction) {
			case -1:	// action vide
				break;
			case 0:
				initAction();
				break;
				
			case 1: 
				SmallSet set = clientsParJour.get(jourCourant);
				nom_S = ((LexVelo) analyseurLexical).tabIdent.get(numId());
				set.add(numId());
				break;
				
			case 2: 
				if(executer) {
					maBaseDeLoc.enregistrerLoc(nom_S, jourCourant, heureDebut, qteAdulte, qteEnfant);
				}

				executer = true;
				nbOperationCorrectes++;
				jourCourant++;
				clientsParJour.add(jourCourant, new SmallSet());
				Ecriture.ecrireStringln("\nBILAN DU JOUR " + (jourCourant-1));
				maBaseDeLoc.afficherLocationsEnCours();
				Ecriture.ecrireStringln("******************************************************************************");
				break;
				
			case 3://enregistre nbHeure
				heureDebut = valEnt();
				heures = true;
				if(heureDebut < 8 || heureDebut > 19) {
					erreur(NONFATALE, "Err 3 : L'heure n'est pas correcte");
					executer = false;
					nbOperationTotales++;
					SmallSet set1 = clientsParJour.get(jourCourant);
					set1.remove(numId());
				}
				break;
				
			case 4: //cas FIN
				if(heures) {
					if (maBaseDeLoc.getInfosClient(((LexVelo) analyseurLexical).tabIdent.get(numId())) == null) {
						erreur(NONFATALE, "Err 4 : Le client n'existe pas");
						executer = false;
						break;
					}if(jourCourant == maBaseDeLoc.getInfosClient(nom_S).jourEmprunt && heureDebut < maBaseDeLoc.getInfosClient(nom_S).heureDebut) {
						erreur(FATALE, "Err 4 : Impossible l'heure saisi est inférieur à l'heure de location");
						executer = false;
						break;
					}else {
						prix = calculPrix(calculDureeLoc(maBaseDeLoc.getInfosClient(nom_S).jourEmprunt, maBaseDeLoc.getInfosClient(nom_S).heureDebut, jourCourant, heureDebut), maBaseDeLoc.getInfosClient(nom_S).qteEnfant,  maBaseDeLoc.getInfosClient(nom_S).qteAdulte);
						stockAdultes += maBaseDeLoc.getInfosClient(nom_S).qteAdulte;
						stockEnfants += maBaseDeLoc.getInfosClient(nom_S).qteEnfant;
						Ecriture.ecrireStringln("\nLe client:  " + nom_S + "  doit payer : " 
								+ prix + " euros pour " + maBaseDeLoc.getInfosClient(nom_S).qteAdulte + " velo(s) adulte et " 
								+ maBaseDeLoc.getInfosClient(nom_S).qteEnfant + " velo(s) enfant" );
						maBaseDeLoc.supprimerClient(nom_S);
						break;
					}
				}else {
					if (maBaseDeLoc.getInfosClient(((LexVelo) analyseurLexical).tabIdent.get(numId())) == null) {
						erreur(NONFATALE, "Err 4 : Le client n'existe pas");
						executer = false;
						nbOperationCorrectes--;
						nbOperationTotales++;
						break;
					}if(jourCourant == maBaseDeLoc.getInfosClient(nom_S).jourEmprunt && heureDebut < maBaseDeLoc.getInfosClient(nom_S).heureDebut) {
						erreur(FATALE, "Err 4 : Impossible l'heure saisi est inférieur à l'heure de location");
						executer = false;
						nbOperationCorrectes--;
						nbOperationTotales++;
						break;
					}
					heureDebut = 19;
					heures = false;
					prix = calculPrix(calculDureeLoc(maBaseDeLoc.getInfosClient(nom_S).jourEmprunt, maBaseDeLoc.getInfosClient(nom_S).heureDebut, jourCourant, heureDebut), maBaseDeLoc.getInfosClient(nom_S).qteEnfant,  maBaseDeLoc.getInfosClient(nom_S).qteAdulte);
					stockAdultes += maBaseDeLoc.getInfosClient(nom_S).qteAdulte;
					stockEnfants += maBaseDeLoc.getInfosClient(nom_S).qteEnfant;
					Ecriture.ecrireStringln("\nLe client:  " + nom_S + "  doit payer : " 
							+ prix + " euros pour " + maBaseDeLoc.getInfosClient(nom_S).qteAdulte + " velo(s) adulte et " 
							+ maBaseDeLoc.getInfosClient(nom_S).qteEnfant + " velo(s) enfant");
					maBaseDeLoc.supprimerClient(nom_S);
					break;
				}
				
			case 5: 
				if (maBaseDeLoc.getInfosClient(nom_S) != null) {
					erreur(NONFATALE, "Err 5 : Le client existe déjà");
					executer = false;
				}else {
					if(!heures) {
						heureDebut = 8;
						heures = false;
					}
				}
				break;
				
			case 6:
				stock = valEnt();
				break;
				
			case 7:
				qteAdulte = stock;
				
				if (qteAdulte < 0) {
					erreur(NONFATALE, "Err 7 : Quantité adulte trop faible");
					qteAdulte = 0;
					nbOperationTotales++;
					SmallSet set1 = clientsParJour.get(jourCourant);
					set1.remove(numId());
					nbOperationCorrectes--;
					nbOperationTotales++;
					
				}
				stockAdultes -= qteAdulte;
				if (stockAdultes < 0) {
					erreur(NONFATALE, "Err 7 : Quantité adulte indisponible");
					stockAdultes += qteAdulte;
					nbOperationTotales++;
					qteAdulte = 0;
					SmallSet set1 = clientsParJour.get(jourCourant);
					set1.remove(numId());
					nbOperationCorrectes--;
					nbOperationTotales++;
				}
				break;
				
			case 8:
				qteEnfant = stock;
				
				if (qteEnfant < 0) {
					erreur(NONFATALE, "Err 8 : Quantité enfant trop faible");
					executer = false;
					qteEnfant = 0;
				}
				stockEnfants -= qteEnfant;
				if (stockEnfants < 0) {
					erreur(NONFATALE, "Err 8 : Quantité enfant indisponible");
					executer = false;
					stockEnfants += qteEnfant;
					qteEnfant = 0;
				}
				break;
				
			case 9: 
				qteEnfant = ((LexVelo) analyseurLexical).getvalEnt();
				stockEnfants -= qteEnfant;
				if(qteEnfant > MAX_VELOS_ENFANT) {
					erreur(NONFATALE, "Err 9 : Quantité enfant trop importante");
					executer = false;
				}else if (qteEnfant < 0) {
					erreur(NONFATALE, "Err 9 : Quantité enfant trop faible");
					executer = false;
				}
				break;
				
			case 10:
				if(executer) {
					qteEnfant = 0;
					maBaseDeLoc.enregistrerLoc(nom_S, jourCourant, heureDebut, qteAdulte, qteEnfant);
					nbOperationCorrectes++;
				}
				
				executer = true;
				break;
				
			case 11:
				if(executer) {
					qteAdulte = 0;
					maBaseDeLoc.enregistrerLoc(nom_S, jourCourant, heureDebut, qteAdulte, qteEnfant);
					nbOperationCorrectes++;
				}else {
					SmallSet set11 = clientsParJour.get(jourCourant);
					set11.remove(numId());
				}
				
				executer = true;
				break;
				
			case 12:
				Ecriture.ecrireStringln("**************************** BILAN FINAL **********************************\n");
				Ecriture.ecrireStringln("Nombre de velos adulte manquants :  " + (MAX_VELOS_ADULTES - stockAdultes));
				Ecriture.ecrireStringln("Nombre de velos enfant manquants :  " + (MAX_VELOS_ENFANT - stockEnfants));
				Ecriture.ecrireStringln("Operations correctes : " + nbOperationCorrectes + " - Nombre total d'operations : " + (nbOperationTotales+nbOperationCorrectes));
				Ecriture.ecrireStringln("\nVoici les clients qui doivent encore rendre des velos");
				maBaseDeLoc.afficherLocationsEnCours();
				int maxClient = 0;
				int jour = 0;
				for(int i = 0; i < clientsParJour.size(); i++) {
					SmallSet sset = clientsParJour.get(i);
					if(sset.size() > maxClient) {
						maxClient = sset.size();
						jour = i;
					}
				}
				Ecriture.ecrireStringln("**************************** BILAN AFFLUENCE **********************************\n"
									  + "Le jour de plus grande affluence  est : " + jour 
									  + " avec " + maxClient + " clients différents servis");
				break;
			
			case 13: 
				if(executer) {
					maBaseDeLoc.enregistrerLoc(nom_S, jourCourant, heureDebut, qteAdulte, qteEnfant);
					nbOperationCorrectes++;
				}
				
				executer = true;
				break;
				
			case 14:
				executer = true;
				jourCourant++;
				nbOperationCorrectes++;
				clientsParJour.add(jourCourant, new SmallSet());
				Ecriture.ecrireStringln("\nBILAN DU JOUR " + (jourCourant-1));
				maBaseDeLoc.afficherLocationsEnCours();
				Ecriture.ecrireStringln("******************************************************************************");
				break;
			
			case 15:
				executer = true;
				nbOperationCorrectes++;
				break;
				
			case 16:
				nbOperationTotales++;
				break;
				
			default:
				Lecture.attenteSurLecture("action " + numAction + " non prevue");
		}
	} // fin executer

	private static int calculPrix(int nbH, int nbVE, int nbVA) {
		return nbVE * nbH * 2 + nbVA * nbH * 4;
	}

	/**
	 * 
	 * utilitaire de calcul de la duree d'une location
	 *
	 * @param jourDebutLoc : numero du jour de début de la location à partir de 1
	 * @param heureDebutLoc: heure du debut de la location, entre 8 et 19
	 * @param jourFinLoc   : numero du jour de la fin de la location à partir de 1
	 * @param heureFinLoc  : heure de fin de la location, entre 8 et 19
	 * @return nombre d'heures comptabilisées pour la location 
	 * 			(les heures de nuit entre 19h et 8h ne sont pas comptabilisees)
	 */
	private int calculDureeLoc(int jourDebutLoc, int heureDebutLoc, int jourFinLoc, int heureFinLoc) {
		int duree;
		// velos rendus le jour de l'emprunt
		if (jourDebutLoc == jourFinLoc) { 
			if (heureFinLoc != heureDebutLoc) duree = heureFinLoc - heureDebutLoc; 
			else duree = 1;
		// velos rendus quelques jours apres l'emprunt (la duree ne peut pas etre negative)
		} else { 
			duree = 19 - heureDebutLoc; // duree du premier jour
			duree = duree + (heureFinLoc - 8); // ajout de la duree du dernier jour
			if (jourFinLoc > jourDebutLoc + 1) { // plus 24h par jour intermediaire
				duree = duree + 11 * (jourFinLoc - jourDebutLoc - 1);
			} 
		}
		return duree;
	}
	
	
}

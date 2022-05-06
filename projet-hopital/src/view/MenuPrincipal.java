package view;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import controller.Controller;
import db.ConnectionManager;
import model.Hopital;
import model.Patient;
import model.SalleConsultation;
import model.Visite;

public class MenuPrincipal implements MenuView {

	private Controller controller;
	private Scanner clavierint = new Scanner(System.in);
	private Scanner clavierString = new Scanner(System.in);
	private AffichagePatient affPat = new AffichagePatient();
	private AffichageVisite affVis = new AffichageVisite();

	public MenuPrincipal() {

	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void afficherMenuPrincipal() throws ClassNotFoundException, SQLException, IOException {

		System.out.println("--------------------------------------------");
		System.out.println("|           Bienvenue àl'hopital.          |");
		System.out.println("|                version 2.0                |");
		System.out.println("--------------------------------------------");
		System.out.println(
				"\nVeuillez choisir parmi les options suivantes : \n\n" + "1. Menu Login\n" + "2. Quitter l'hopital");
		int choix = clavierint.nextInt();

		switch (choix) {
		case 1:
			afficherMenuAuthentification();
			break;
		case 2:
			System.out.println("--------------------------------------------");
			System.out.println("Au revoir!");
			for (SalleConsultation sc : Hopital.getHopital().getSalles())
				controller.saveVisitesBD(sc.getId_salle());
			ConnectionManager.getInstance().closeConn();
			System.exit(0);
			break;
		default:
			System.out.println("--------------------------------------------");
			System.out.println("Votre saisie est incorrecte.");
			afficherMenuPrincipal();
			;
		}

	}

	@Override
	public void afficherMenuAuthentification() throws ClassNotFoundException, SQLException, IOException {
		System.out.println("--------------------------------------------");
		System.out.println("Veuillez vous identifiez.");
		System.out.println("Entrez votre username : ");
		String username = clavierString.nextLine();
		System.out.println("Entrez votre mot de passe : ");
		String mdp = clavierString.nextLine();
		controller.setUser(username);
		if (!username.isEmpty() && !mdp.isEmpty()) {
			if (controller.verifLogin(username, mdp)) {
				switch (controller.getUser().getMetier()) {
				case 0:
					afficherMenuSecretaire();
					break;
				case 1:
					afficherMenuMedecin();
					break;
				default:
					System.out.println("--------------------------------------------");
					System.out.println("Votre saisie est incorrecte.");
					afficherMenuAuthentification();
				}
			} else {
				System.out.println("--------------------------------------------");
				System.out.println("Les identifiants sont incorrects!\n");
				afficherMenuPrincipal();
			}
		} else {
			System.out.println("--------------------------------------------");
			System.out.println("Vous n'avez pas saisi de username et / ou de mot de passe");
			afficherMenuAuthentification();
		}

	}

	@Override
	public void afficherMenuSecretaire() throws ClassNotFoundException, SQLException, IOException {

		System.out.println("--------------------------------------------");
		System.out.println("Bonjour " + controller.getUser().getNom() + "\n");
		System.out.println("Veuillez choisir parmi les options suivantes :\n\n"
				+ "1. Ajouter un patient àla file d'attente\n" + "2. Afficher la file d'attente \n"
				+ "3. Afficher le prochain patient de la file \n" + "4. Ajouter une adresse/tel � un patient\n"
				+ "5. Afficher la liste des visites en base d'un patient selon son ID\n" + "6. Menu principal");
		int choixSec = clavierint.nextInt();
		switch (choixSec) {
		case 1:
			System.out.println("--------------------------------------------");
			System.out.println("Saisir l'ID du patient :");
			int idPatient = clavierint.nextInt();
			break;
		case 2:
			System.out.println("--------------------------------------------");
			System.out.println("File d'attente :");
			for (Patient p : controller.getFile())
				System.out.println(affPat.afficherPatient(p));
			break;
		case 3:
			System.out.println("--------------------------------------------");
			System.out.println("Prochain patient dans la file d'attente : ");
			if (controller.getProchainPatient() != null) {
				System.out.println(affPat.afficherPatient(controller.getProchainPatient()));
			} else {
				System.out.println("Il n'y a pas de patient en file d'attente");
			}
			break;
		case 4:
			System.out.println("--------------------------------------------");
			System.out.println("Veuillez entrer l'ID du patient � modifier");
			int id = clavierint.nextInt();
			System.out.println("Veuillez entrer l'adresse");
			String adr = clavierString.nextLine();
			System.out.println("Veuillez entrer le t�l�phone");
			String tel = clavierString.nextLine();
			controller.miseAJourPatient(id, adr, tel);
			System.out.println("Mise � jour effectu�e !");
			break;
		case 5:
			System.out.println("--------------------------------------------");
			System.out.println("Veuillez entrer l'ID du patient ");
			int idlist = clavierint.nextInt();
			for (Visite v : controller.voirVisitePatBD(idlist))
				System.out.println(affVis.afficherVisite(v));
			break;
		case 6:
			afficherMenuPrincipal();
			break;
		default:
			System.out.println("Saisie incorrecte");
		}
		afficherMenuSecretaire();
	}

	public void menuPatientFile(int idPatient) throws ClassNotFoundException, SQLException, IOException {

		String nomPatient = "";
		String prenomPatient = "";
		String datePatient = "";
		String adrPatient = "";
		String telPatient = "";
		Patient patient = controller.findByIdPat(idPatient);
		if (patient != null) {
			System.out.println("--------------------------------------------");
			if (!controller.isPatientDansListe(patient.getId())) {
				controller.addPatient(patient);
				controller.ecrireRapport(idPatient);
				System.out.println("Patient id n�" + idPatient + " ajout� �la file");
			} else {
				System.out.println("Ce patient n�" + idPatient + " est d�j� dans la file d'attente");
			}
		} else {
			System.out.println("--------------------------------------------");
			System.out.println("Saisir le nom : ");
			nomPatient = clavierString.nextLine();
			System.out.println("Saisir le pr�nom : ");
			prenomPatient = clavierString.nextLine();
			System.out.println("Saisir la date de naissance : (AAAA-MM-JJ)");
			datePatient = clavierString.nextLine();
			Patient patient1 = new Patient(idPatient, nomPatient, prenomPatient, datePatient);
			System.out.println("Voulez vous saisir l'adresse et le t�l�phone? O/N");
			String choixPatient1 = clavierString.nextLine();
			switch (choixPatient1) {
			case "O":
			case "o":
				System.out.println("--------------------------------------------");
				System.out.println("Saisir l'adresse");
				adrPatient = clavierString.nextLine();
				System.out.println("Saisir le t�l�phone");
				telPatient = clavierString.nextLine();
				patient1.setAdresse(adrPatient);
				patient1.setTelephone(telPatient);
				break;
			}
			System.out.println("--------------------------------------------");
			controller.createPatient(patient1);
			controller.addPatient(controller.findByIdPat(idPatient));
			controller.ecrireRapport(idPatient);
			System.out.println("Patient id n�" + idPatient + " ajout� �la file");
		}
		afficherMenuSecretaire();
	}

	@Override
	public void afficherMenuMedecin() throws ClassNotFoundException, SQLException, IOException {

		System.out.println("--------------------------------------------");
		System.out.println("Bonjour dans la salle de consultation n�" + controller.getUser().getSalle() + " "
				+ controller.getUser().getNom() + "\n");
		SalleConsultation salle = controller.getSalle(controller.getUser().getSalle());
		salle.setMedecin(controller.getUser().getNom());
		System.out.println("Veuillez choisir parmi les options suivantes : \n\n"
				+ "1. Accueillir le prochain patient (rendre la salle disponible)\n"
				+ "2. Afficher la file d'attente \n" + "3. Afficher la liste des visites\n"
				+ "4. Sauvegarder en base les visites \n" + "5. Afficher la liste des visites de la BD\n"
				+ "6. Menu principal");
		int choixMed = clavierint.nextInt();
		switch (choixMed) {
		case 1:
			System.out.println("--------------------------------------------");
			// System.out.println(controller.getProchainPatient());
			if (controller.getProchainPatient() != null) {
				controller.medProchainPatient(salle.getId_salle());
				System.out.println("Patient qui entre dans la salle de consultation " + salle.getId_salle() + " : ");
				System.out.println(affPat.afficherPatient(salle.getPatient()));
				System.out.println();
				controller.addVisite(salle.getPatient().getId(), salle);

			} else {
				System.out.println("--------------------------------------------");
				System.out.println("Il n'y a plus de patients dans la salle d'attente");
			}
			afficherMenuMedecin();
			break;
		case 2:
			System.out.println("--------------------------------------------");
			System.out.println("File d'attente :");
			for (Patient p : controller.getFile())
				System.out.println(affPat.afficherPatient(p));
			afficherMenuMedecin();
			break;
		case 3:
			System.out.println("--------------------------------------------");
			System.out.println("Voici la liste des visites du jour (avant sauvegarde en BD) : ");
			for (Visite v : controller.getListVisites(salle.getId_salle()))
				System.out.println(affVis.afficherVisite(v));
			afficherMenuMedecin();
			break;
		case 4:
			System.out.println("--------------------------------------------");
			controller.saveVisitesBD(salle.getId_salle());
			System.out.println("Liste des visites sauvegard�es en BD.");
			afficherMenuMedecin();
			break;
		case 5:
			System.out.println("--------------------------------------------");
			for (Visite v : controller.voirVisitesEnBD())
				System.out.println(affVis.afficherVisite(v));
			afficherMenuMedecin();
			break;
		case 6:
			afficherMenuPrincipal();
			break;

		default:
			System.out.println("--------------------------------------------");
			System.out.println("Saisie incorrecte");
			afficherMenuMedecin();

		}
	}

	@Override
	public void afficherMenuOrdonnance() throws ClassNotFoundException, SQLException, IOException {
		
		System.out.println("--------------------------------------------");
		System.out.println("Menu ordonnance");
		System.out.println("Liste des m�dicaments disponibles : ");
		System.out.println(); // controller pour r�cup�rer la liste des m�dicaments depuis la liste
		System.out.println("9. terminer l'ordonnance");
		int choixMedicament = 0;
		do {
			choixMedicament = clavierint.nextInt();
			// controller pour ajouter le m�dicament dans l'ordonnance
			System.out.println("m�dicament ajout� � la l'ordonnance"); // r�cup�rer le nom du m�dicament
		} while (choixMedicament != 9);
		afficherMenuMedecin();
	}
	
	
	
}

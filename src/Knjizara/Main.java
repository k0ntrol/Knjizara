package Knjizara;
import java.util.Scanner;


public class Main {


	public static void main(String[] args) {
		DatabaseConnection d = new DatabaseConnection("jdbc:mysql://localhost:3306/knjizara", "root", "");

		CenteredBoxAsciiArt.main(args);

		boolean exit = false;  // kontrola izlaska iz programa

		while (!exit) {
			System.out.println("=====================================");
			System.out.println("  Glavni meni:");
			System.out.println("  1) Dodaj");
			System.out.println("  2) Ažuriraj");
			System.out.println("  3) Prikaži");
			System.out.println("  4) Obriši");
			System.out.println("  5) Izdaj račun");
			System.out.println("  6) Import knjiga iz fajla");
			System.out.println("  0) Izlaz");
			System.out.println("=====================================");
			System.out.print("Izaberite opciju: ");

			Scanner scanner = new Scanner(System.in);
			String choice = scanner.nextLine();

			switch (choice) {
				case "1":
					showAddSubMenu();
					break;
				case "2":
					showUpdateSubMenu();
					break;
				case "3":
					showDisplaySubMenu();
					break;
				case "4":
					showDeleteSubMenu();
					break;
				case "5":
					d.izdajRacunIzKonzole();
					break;
				case "6":
					d.importKnjigaIzFajla();
					break;
				case "0":
					exit = true;
					System.out.println("Izlaz iz programa.");
					break;
				default:
					System.out.println("Nepoznata opcija, pokušajte ponovo.");
			}
			System.out.println();
		}


	}
	private static void showAddSubMenu() {
		Scanner scanner = new Scanner(System.in);
		DatabaseConnection d = new DatabaseConnection("jdbc:mysql://localhost:3306/knjizara", "root", "");

		boolean exit = false;
		while (!exit) {
			System.out.println("\nŠta hoćete da dodate?");
			System.out.println("1) Knjigu");
			System.out.println("2) Autora");
			System.out.println("3) Izdavača");
			System.out.println("4) Distributera");
			System.out.println("5) Kupca");
			System.out.println("6) Kategoriju");
			System.out.println("7) Kreiraj karticu");
			System.out.println("0) Izlaz");
			System.out.print("Izbor: ");

			String subChoice = scanner.nextLine();

			switch (subChoice) {
				case "1":
					d.dodajKnjiguIzKonzole();
					break;
				case "2":
					d.dodajAutoraIzKonzole();
					break;
				case "3":
					d.dodajIzdavacaIzKonzole();
					break;
				case "4":
					d.dodajDistributeraIzKonzole();
					break;
				case "5":
					d.dodajKupcaIzKonzole();
					break;
				case "6":
					d.dodajKategorijuIzKonzole();
					break;
				case "7":
					System.out.println("Unesite broj telefona kupca za kreiranje kartice:");
					String brojTelefona = scanner.nextLine();
					if(DatabaseConnection.validanBrojTelefona(brojTelefona)) {
						System.out.println("Nevalidan broj telefona.");
						return;
					}
					Integer kupacId = d.getKupacByBroj(brojTelefona);
					if(d.imaAktivnuKarticu(kupacId)){
						System.out.println("Kupac već ima aktivnu karticu.");
						return;
					}
					if(d.kreirajKarticuZaKupca(kupacId)){
						System.out.println("Kartica je kreirana.");
						return;
					}
					break;
				case "0":
					exit = true;
					System.out.println("Izlaz iz programa.");
					break;
				default:
					System.out.println("Nepoznata opcija u podmeniju (Dodaj).");
			}
		}

	}

	private static void showUpdateSubMenu() {
		Scanner scanner = new Scanner(System.in);
		DatabaseConnection d = new DatabaseConnection("jdbc:mysql://localhost:3306/knjizara", "root", "");

		boolean exit = false;
		while (!exit) {
			System.out.println("\nŠta hoćete da ažurirate?");
			System.out.println("1) Autore knjige");
			System.out.println("2) Informacije o knjizi");
			System.out.println("3) Poveži distributera i izdavača");
			System.out.println("4) Deaktiviraj karticu");
			System.out.println("5) Distributera");
			System.out.println("6) Izdavača");
			System.out.println("7) Autora");
			System.out.println("8) Kupca");
			System.out.println("0) Izlaz");
			System.out.print("Izbor: ");

			String subChoice = scanner.nextLine();

			switch (subChoice) {
				case "1":
					System.out.println("Unesite ISBN knjige:");
					String isbn = scanner.nextLine();
					Integer knjigaId = d.getIdByName("knjiga", "isbn", isbn);
					if (knjigaId == null) {
						System.out.println("Knjiga sa ISBN-om " + isbn + " ne postoji.");
					} else {
						d.dodajAutoreZaKnjigu(knjigaId, scanner);
					}
					break;
				case "2":
					d.azurirajKnjiguIzKonzole();

					break;
				case "3":
					d.spojiDistributeraIIzdavaca();
					break;
				case "4":
					System.out.println("Unesite broj telefona kupca za deaktiviranje kartice:");
					String brojTelefonaZaDeaktivacijuKartice = scanner.nextLine();
					if(DatabaseConnection.validanBrojTelefona(brojTelefonaZaDeaktivacijuKartice)) {
						System.out.println("Nevalidan broj telefona.");
						return;
					}
					Integer kupacId = d.getKupacByBroj(brojTelefonaZaDeaktivacijuKartice);
					d.deaktivirajKarticu(kupacId);

					break;
				case "5":
					System.out.println("Unesite ime distributera za ažuriranje:");
					String imeDistributera = scanner.nextLine();
					if(d.azurirajDistributera(imeDistributera,scanner)) {
						System.out.println("Uspešno ažuriranje.");
					}

					break;
				case "6":
					System.out.println("Unesite ime izdavača za ažuriranje:");
					String imeIzdavaca = scanner.nextLine();
					if(d.azurirajIzdavaca(imeIzdavaca,scanner)){
						System.out.println("Uspešno ažuriranje.");
					}

					break;
				case "7":
					System.out.println("Unesite ime autora za ažuriranje:");
					String ime = scanner.nextLine();
					System.out.println("Unesite prezime autora za ažuriranje:");
					String prezime = scanner.nextLine();
					d.azurirajAutora(ime, prezime,scanner);
					break;
				case "8":
					System.out.println("Unesite broj telefona kupca za ažuriranje:");
					String brojTelefonaZaAzuriranje = scanner.nextLine();
					if(DatabaseConnection.validanBrojTelefona(brojTelefonaZaAzuriranje)) {
						System.out.println("Nevalidan broj telefona.");
						return;
					}
					if(d.azurirajKupca(brojTelefonaZaAzuriranje,scanner)){
						System.out.println("Uspešno ažuriranje.");
					}
					break;
				case "0":
					exit = true;
					System.out.println("Izlaz iz podmenija za ažuriranje.");
					break;
				default:
					System.out.println("Nepoznata opcija, pokušajte ponovo.");
			}
		}
	}

	private static void showDisplaySubMenu() {
		Scanner scanner = new Scanner(System.in);
		DatabaseConnection d = new DatabaseConnection("jdbc:mysql://localhost:3306/knjizara", "root", "");

		boolean exit = false;
		while (!exit) {
			System.out.println("\nŠta želite da prikažete?");
			System.out.println("1) Sve knjige na stanju");
			System.out.println("2) Informacije o određenoj knjizi");
			System.out.println("3) Svi računi kupca koji imaju cenu veću od prosečne");
			System.out.println("4) Prikaz računa u mesecu");
			System.out.println("5) Prikaz računa po mesecima");

			System.out.println("0) Izlaz");
			System.out.print("Izbor: ");

			String subChoice = scanner.nextLine();

			switch (subChoice) {
				case "1":
					d.prikaziSveKnjige();
					break;
				case "2":
					d.prikaziKnjiguPoISBN();
					break;
				case "3":
					d.prikaziRacuneSaCenomVecomOdProsecne();
					break;
				case "4":
					d.prikaziRacuneUMesecu();
					break;
				case "5":
					d.prikaziRacunePoMesecima();
					break;
				case "0":
					exit = true;
					System.out.println("Izlaz iz podmenija za prikaz.");

					break;
				default:
					System.out.println("Nepoznata opcija, pokušajte ponovo.");
			}
		}
	}

	private static void showDeleteSubMenu() {
		Scanner scanner = new Scanner(System.in);
		DatabaseConnection d = new DatabaseConnection("jdbc:mysql://localhost:3306/knjizara", "root", "");

		boolean exit = false;
		while (!exit) {
			System.out.println("\nŠta želite da obrišete?");
			System.out.println("1) Knjigu sa stanja");
			System.out.println("2) Ukloni neaktivne kartice:");
            System.out.println("3) Obrisi knjige iz fajla:");
			System.out.println("0) Izlaz");
			System.out.print("Izbor: ");

			String subChoice = scanner.nextLine();

			switch (subChoice) {
				case "1":
					d.ukloniKnjigu();
					break;
				case "2":
					d.obrisiNeaktivneVipKartice();
					break;
                case "3":
                    d.obrisiKnjigeIzFajla();
                    break;
				case "0":
					exit = true;
					System.out.println("Izlaz iz podmenija za brisanje.");
					break;
				default:
					System.out.println("Nepoznata opcija, pokušajte ponovo.");
			}
		}
	}


}



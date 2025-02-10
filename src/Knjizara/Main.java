package Knjizara;
import java.sql.SQLOutput;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class Main {
	static Scanner scanner = new Scanner(System.in);

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
			System.out.println("  0) Izlaz");
			System.out.println("=====================================");
			System.out.print("Izaberite opciju: ");


			String choice = scanner.nextLine();

			switch (choice) {
				case "1": // Dodaj
					showAddSubMenu(scanner);
					break;
				case "2":
					showUpdateSubMenu(scanner);
					break;
				case "3": // Prikaži
					// Ovde metode za prikaz
					System.out.println("Pozvaćemo metode za prikaz...");
					break;
				case "4": // Obriši
					// Ovde metode za brisanje
					System.out.println("Pozvaćemo metode za brisanje...");
				case "5":

					System.out.println("Pozvaćemo metode izdavanje računa");
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

	/**
	 * Pomoćni metod za prikaz podmenija prilikom izbora "Dodaj"
	 */
	private static void showAddSubMenu(Scanner scanner) {
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
				case "0":
					exit = true;
					System.out.println("Izlaz iz programa.");
					break;
				default:
					System.out.println("Nepoznata opcija u podmeniju (Dodaj).");
			}
		}
	}

	private static void showUpdateSubMenu(Scanner scanner) {
		DatabaseConnection d = new DatabaseConnection("jdbc:mysql://localhost:3306/knjizara", "root", "");

		boolean exit = false;
		while (!exit) {
			System.out.println("\nŠta hoćete da ažurirate?");
			System.out.println("1) Autore knjige");
			System.out.println("2) Informacije o knjizi");
			System.out.println("3) Podatke o autoru");
			System.out.println("4) Izdavača");
			System.out.println("5) Distributera");
			System.out.println("6) Kupca");
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
					System.out.println("Unesite ISBN knjige za ažuriranje informacija:");
					String isbnZaAzuriranje = scanner.nextLine();
//					d.updateKnjigaInfo(isbnZaAzuriranje, scanner);

					break;
				case "3":
					break;
				case "4":
					break;
				case "5":
					break;
				case "6":
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


//	private static void showDisplaySubMenu(Scanner scanner) {
//		DatabaseConnection d = new DatabaseConnection("jdbc:mysql://localhost:3306/knjizara", "root", "");
//
//		boolean exit = false;
//		while (!exit) {
//			System.out.println("\nŠta želite da prikažete?");
//			System.out.println("1) Sve knjige");
//			System.out.println("2) Informacije o određenoj knjizi");
//			System.out.println("3) Sve autore");
//			System.out.println("4) Sve izdavače");
//			System.out.println("5) Sve kupce");
//			System.out.println("0) Izlaz");
//			System.out.print("Izbor: ");
//
//			String subChoice = scanner.nextLine();
//
//			switch (subChoice) {
//				case "1":
//					d.prikaziSveKnjige();
//					break;
//				case "2":
//					System.out.println("Unesite ISBN ili naziv knjige:");
//					String knjigaInfo = scanner.nextLine();
//					d.prikaziInformacijeOKnjizi(knjigaInfo);
//					break;
//				case "3":
//					d.prikaziSveAutore();
//					break;
//				case "4":
//					d.prikaziSveIzdavace();
//					break;
//				case "5":
//					d.prikaziSveKupce();
//					break;
//				case "0":
//					exit = true;
//					System.out.println("Izlaz iz podmenija za prikaz.");
//					break;
//				default:
//					System.out.println("Nepoznata opcija, pokušajte ponovo.");
//			}
//		}
//	}
//
//	private static void showDeleteSubMenu(Scanner scanner) {
//		DatabaseConnection d = new DatabaseConnection("jdbc:mysql://localhost:3306/knjizara", "root", "");
//
//		boolean exit = false;
//		while (!exit) {
//			System.out.println("\nŠta želite da obrišete?");
//			System.out.println("1) Knjigu");
//			System.out.println("2) Autora");
//			System.out.println("3) Izdavača");
//			System.out.println("4) Distributera");
//			System.out.println("5) Kupca");
//			System.out.println("0) Izlaz");
//			System.out.print("Izbor: ");
//
//			String subChoice = scanner.nextLine();
//
//			switch (subChoice) {
//				case "1":
//					System.out.println("Unesite ISBN knjige za brisanje:");
//					String isbn = scanner.nextLine();
//					d.obrisiKnjigu(isbn);
//					break;
//				case "2":
//					System.out.println("Unesite ime i prezime autora:");
//					String ime = scanner.nextLine();
//					String prezime = scanner.nextLine();
//					d.obrisiAutora(ime, prezime);
//					break;
//				case "3":
//					d.obrisiIzdavacaIzKonzole();
//					break;
//				case "4":
//					d.obrisiDistributeraIzKonzole();
//					break;
//				case "5":
//					d.obrisiKupcaIzKonzole();
//					break;
//				case "0":
//					exit = true;
//					System.out.println("Izlaz iz podmenija za brisanje.");
//					break;
//				default:
//					System.out.println("Nepoznata opcija, pokušajte ponovo.");
//			}
//		}
//	}


}



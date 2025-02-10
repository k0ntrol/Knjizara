package Knjizara;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class DatabaseConnection {

	private final String url;
	private final String user;
	private final String pass;

	public DatabaseConnection(String url, String user, String pass) {
		super();
		this.url = url;
		this.user = user;
		this.pass = pass;
	}

	public Connection open() {
		try {
			return DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			System.out.println("Greska prilikom konektovanja!");
			// e.printStackTrace();
			return null;
		}
	}

	public void close(Connection c) {
		try {
			c.close();
		} catch (SQLException e) {
			System.out.println("Greska u zatvaranju konekcije");
		}
	}



	public Integer getOrCreateAdresa(String grad, String drzava, String ulica, String brojUlice, String postanskiBroj, Scanner scanner) {
		Integer drzavaId = getOrCreateEntitet("drzava", "naziv", drzava, scanner);
		if (drzavaId == null) return null;

		Connection conn = open();
		if (conn == null) return null;

		// Proveravamo da li adresa postoji
		String checkSql = "SELECT id FROM adresa WHERE grad = ? AND drzava_id = ? AND naziv_ulice = ? AND broj_ulice = ? AND postanski_broj = ?";
		try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
			checkPs.setString(1, grad);
			checkPs.setInt(2, drzavaId);
			checkPs.setString(3, ulica);
			checkPs.setString(4, brojUlice);
			checkPs.setString(5, postanskiBroj);

			ResultSet rs = checkPs.executeQuery();
			if (rs.next()) {
				close(conn);
				return rs.getInt(1); // Vraćamo postojeći ID adrese
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Ako ne postoji, dodajemo novu adresu
		String insertSql = "INSERT INTO adresa (grad, drzava_id, naziv_ulice, broj_ulice, postanski_broj) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
			insertPs.setString(1, grad);
			insertPs.setInt(2, drzavaId);
			insertPs.setString(3, ulica);
			insertPs.setString(4, brojUlice);
			insertPs.setString(5, postanskiBroj);
			insertPs.executeUpdate();

			ResultSet rs = insertPs.getGeneratedKeys();
			if (rs.next()) {
				int newId = rs.getInt(1);
				close(conn);
				return newId; // Vraćamo ID nove adrese
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close(conn);
		return null;
	}


	public Integer insertIzdavac(String ime, int godinaOsnivanja, Scanner scanner) {
		Connection conn = open();
		if (conn == null) return null;

		// Prvo tražimo adresu
		System.out.println("Unesite grad izdavača:");
		String grad = scanner.nextLine();
		System.out.println("Unesite državu izdavača:");
		String drzava = scanner.nextLine();
		System.out.println("Unesite ulicu izdavača:");
		String ulica = scanner.nextLine();
		System.out.println("Unesite broj ulice izdavača:");
		String brojUlice = scanner.nextLine();
		System.out.println("Unesite poštanski broj izdavača:");
		String postanskiBroj = scanner.nextLine();

		// Dobijamo ID adrese (ili je dodajemo ako ne postoji)
		Integer adresaId = getOrCreateAdresa(grad, drzava, ulica, brojUlice, postanskiBroj, scanner);
		if (adresaId == null) {
			System.out.println("Greška pri dodavanju adrese!");
			return null;
		}

		// SQL upit za dodavanje izdavača
		String sql = "INSERT INTO izdavac (ime, godina_osnivanja, adresa_id) VALUES (?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, ime);
			ps.setInt(2, godinaOsnivanja);
			ps.setInt(3, adresaId);
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				int id = rs.getInt(1);
				close(conn);
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close(conn);
		return null;
	}
	public Integer getOrCreateDrzava(String drzava, Scanner scanner) {
		Integer drzavaId = getIdByName("drzava", "naziv", drzava);
		if (drzavaId != null) return drzavaId;

		System.out.println("Država '" + drzava + "' ne postoji.");
		System.out.println("Dostupne države:");

		// Prikaz svih država
		prikaziSve("drzava");

		System.out.println("Unesite naziv države ili 'nova' za unos nove:");
		String novaDrzava = scanner.nextLine();

		if (!novaDrzava.equalsIgnoreCase("nova")) {
			return getIdByName("drzava", "naziv", novaDrzava);
		}

		System.out.println("Unesite naziv nove države:");
		String nazivNoveDrzave = scanner.nextLine();
		return insertEntitet("drzava", "naziv", nazivNoveDrzave);
	}

	public Integer insertAutor (String ime, String prezime, Scanner scanner) {
		Connection conn = open();
		if (conn == null) return null;

		String sql = "INSERT INTO autor (ime, prezime) VALUES (?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, ime);
			ps.setString(2, prezime);
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				int id = rs.getInt(1);
				close(conn);
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close(conn);
		return null;

	}
	public void dodajDistributeraIzKonzole() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Unesite ime distributera:");
		String ime = scanner.nextLine();

		// Provera da li već postoji distributer sa tim imenom
		Integer existingId = getIdByName("distributer", "ime", ime);
		if (existingId != null) {
			System.out.println("Distributer sa tim imenom već postoji (ID: " + existingId + ").");
			return;
		}

		// Ako ne postoji, traži se unos dodatnih podataka (npr. broj telefona)
		System.out.println("Unesite broj telefona distributera:");
		String brojTelefona = scanner.nextLine();

		// Dodavanje distributera metodom insertDistributer (koja uključuje i getOrCreateAdresa)
		Integer distributerId = insertDistributer(ime, brojTelefona, scanner);
		if (distributerId != null && distributerId > 0) {
			System.out.println("Distributer uspešno dodat (ID: " + distributerId + ").");
		} else {
			System.out.println("Greška prilikom dodavanja distributera.");
		}
	}

	public void dodajIzdavacaIzKonzole() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Unesite ime izdavača:");
		String ime = scanner.nextLine();

		// Provera da li izdavač već postoji
		Integer existingId = getIdByName("izdavac", "ime", ime);
		if (existingId != null) {
			System.out.println("Izdavač sa tim imenom već postoji (ID: " + existingId + ").");
			return;
		}

		// Ako izdavač ne postoji, traži se unos godine osnivanja
		System.out.println("Unesite godinu osnivanja izdavača:");
		int godinaOsnivanja = 0;
		try {
			godinaOsnivanja = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Neispravan unos za godinu osnivanja.");
			return;
		}

		// Dodavanje izdavača ako ne postoji
		Integer izdavacId = insertIzdavac(ime, godinaOsnivanja, scanner);
		if (izdavacId != null && izdavacId > 0) {
			System.out.println("Izdavač uspešno dodat (ID: " + izdavacId + ").");
		} else {
			System.out.println("Greška prilikom dodavanja izdavača.");
		}
	}

	public void dodajKupcaIzKonzole() {
		Scanner scanner = new Scanner(System.in);
		Connection conn = open();
		if (conn == null) return;

		try {
			conn.setAutoCommit(false); // Počinjemo transakciju

			// Unos osnovnih podataka
			System.out.println("Unesite ime kupca:");
			String ime = scanner.nextLine().trim();
			if (ime.isEmpty()) {
				System.out.println("Ime ne može biti prazno!");
				return;
			}

			System.out.println("Unesite prezime kupca:");
			String prezime = scanner.nextLine().trim();
			if (prezime.isEmpty()) {
				System.out.println("Prezime ne može biti prazno!");
				return;
			}

			System.out.println("Unesite broj telefona kupca:");
			String brojTelefona = scanner.nextLine().trim();
			if (!brojTelefona.matches("^[+\\d\\s()-]{6,}$")) {
				System.out.println("Nevalidan format telefona!");
				return;
			}

			// Provera postojanja kupca
			Integer existingKupacId = getIdByName("kupac", "broj_telefona", brojTelefona);
			if (existingKupacId != null) {
				System.out.println("Kupac već postoji (ID: " + existingKupacId + ")");
				return;
			}

			// Unos vrste kupca
			int vrstaKupcaId = 1;
			System.out.println("Vrsta kupca: unesite '1' za Standard, '2' za VIP:");
			String unosVrste = scanner.nextLine();
			if (unosVrste.equals("2")) {
				vrstaKupcaId = 2;
			}

			// Dodavanje kupca sa transakcionom kontrolom
			Integer kupacId = insertKupac(ime, prezime, brojTelefona, vrstaKupcaId, scanner);
			if (kupacId == null) {
				conn.rollback();
				System.out.println("Greška pri dodavanju kupca!");
				return;
			}

			// Kreiranje VIP kartice ako je potrebno
			if (vrstaKupcaId == 2) {
				if (!imaAktivnuKarticu(kupacId)) {
					if (!kreirajKarticuZaKupca(kupacId, scanner)) {
						conn.rollback();
						System.out.println("Greška pri kreiranju VIP kartice!");
						return;
					}
				} else {
					System.out.println("Kupac već ima aktivnu karticu.");
				}
			}

			// Ažuriranje broja transakcija
			updateBrojTransakcija(kupacId, 0); // Inicijalno 0 transakcija

			conn.commit(); // Uspešan kraj transakcije
			System.out.println("Kupac uspešno dodat! ID: " + kupacId);

		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			System.err.println("Greška u transakciji: " + e.getMessage());
		} finally {
			close(conn);
		}
	}


	/**
	 * Ubacuje kupca u tabelu "kupac" i vraća ID (auto-increment), ili -1 ako dođe do greške.
	 * Primer pretpostavlja da tabela kupac ima kolone:
	 *   id (PRIMARY KEY, auto-increment),
	 *   ime, prezime, broj_telefona, vrsta_kupca_id
	 */
	private Integer insertKupac(String ime, String prezime, String brojTelefona, int vrstaKupcaId, Scanner scanner) {
		Connection conn = open();
		if (conn == null) return null;

		try {
			conn.setAutoCommit(false); // Počni transakciju

			// Unos adrese
			System.out.println("Unesite grad:");
			String grad = scanner.nextLine();
			System.out.println("Unesite državu:");
			String drzava = scanner.nextLine();
			System.out.println("Unesite naziv ulice:");
			String ulica = scanner.nextLine();
			System.out.println("Unesite broj ulice:");
			String brojUlice = scanner.nextLine();
			System.out.println("Unesite poštanski za grad " + grad + ":");
			String postanskiBroj = scanner.nextLine();

			// Validacija
			if (grad.isBlank() || ulica.isBlank() || !postanskiBroj.matches("\\d+")) {
				System.out.println("Nevalidan unos adrese!");
				return null;
			}

			Integer adresaId = getOrCreateAdresa(grad, drzava, ulica, brojUlice, postanskiBroj, scanner);
			if (adresaId == null) {
				System.out.println("Greška pri dodavanju adrese!");
				conn.rollback();
				return null;
			}

			// Unos kupca
			String sql = "INSERT INTO kupac (ime, prezime, broj_telefona, vrsta_kupca_id, adresa_id) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, ime);
				ps.setString(2, prezime);
				ps.setString(3, brojTelefona);
				ps.setInt(4, vrstaKupcaId);
				ps.setInt(5, adresaId);
				ps.executeUpdate();

				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					conn.commit(); // Potvrdi transakciju
					return rs.getInt(1);
				} else {
					conn.rollback();
					return null;
				}
			}
		} catch (SQLException e) {
			try {
				conn.rollback(); // Rollback ako dodje do greške
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
			return null;
		} finally {
			close(conn);
		}
	}


	/**
	 * Metod koji kreira zapis u tabeli "kartica" za datog kupca (pretpostavljamo da je VIP).
	 */
	private boolean kreirajKarticuZaKupca(int kupacId, Scanner scanner) {
		System.out.println("Unesite popust za VIP karticu (%):");
		double popust;
		try {
			popust = Double.parseDouble(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Nevalidan unos popusta!");
			return false;
		}

		String sql = "INSERT INTO vip_kartica (kupac_id, datum_izdavanja, popust, jeAktivna) " +
				"VALUES (?, CURDATE(), ?, true)";

		try (Connection conn = open();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, kupacId);
			ps.setDouble(2, popust);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Greška pri kreiranju kartice: " + e.getMessage());
			return false;
		}
	}



	private boolean imaAktivnuKarticu(int kupacId) {
		Connection conn = open();
		if (conn == null) return false;
		String sql = "SELECT COUNT(*) AS cnt FROM vip_kartica WHERE kupac_id = ? AND jeAktivna = true";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, kupacId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("cnt") > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return false;
	}

	/**
	 * Pretpostavljena metoda koja vraća broj transakcija za kupca.
	 * Implementacija zavisi od postojeće DB strukture. Ovde je samo primer.
	 */
	public int getBrojTransakcija(int kupacId) {
		Connection conn = open();
		if (conn == null) return 0;
		String sql = "SELECT COUNT(*) AS tx_count FROM racun WHERE kupac_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, kupacId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("tx_count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return 0;
	}

	/**
	 * Ažurira broj transakcija za konkretnog kupca.
	 */
	private void updateBrojTransakcija(int kupacId, int brojTransakcija) {
		Connection conn = open();
		if (conn == null) return;
		String sql = "UPDATE kupac SET broj_transakcija = ? WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, brojTransakcija);
			ps.setInt(2, kupacId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}




	public void dodajAutoraIzKonzole() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Unesite ime autora:");
		String ime = scanner.nextLine();
		System.out.println("Unesite prezime autora:");
		String prezime = scanner.nextLine();

		// 1. Proverava se da li već postoji autor sa tim imenom i prezimenom
		Integer existingId = getAutorIdByImePrezime(ime, prezime);
		if (existingId != null) {
			System.out.println("Autor sa tim imenom i prezimenom već postoji (ID: " + existingId + ").");
			return;
		}

		// 2. Ako ne postoji, poziva se insertAutor (koji očekuje ime i prezime)
		Integer autorId = insertAutor(ime, prezime, scanner);
		if (autorId != null && autorId > 0) {
			System.out.println("Autor uspešno dodat (ID: " + autorId + ").");
		} else {
			System.out.println("Greška prilikom dodavanja autora.");
		}
	};

	// Metoda koja vraća ID autora na osnovu imena i prezimena ili null ako ne postoji
	public Integer getAutorIdByImePrezime(String ime, String prezime) {
		Connection conn = open();
		if (conn == null) return null;

		Integer foundId = null;
		String query = "SELECT id FROM autor WHERE ime = ? AND prezime = ? LIMIT 1";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, ime);
			ps.setString(2, prezime);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					foundId = rs.getInt("id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return foundId;
	}



	public Integer insertDistributer(String ime, String brojTelefona, Scanner scanner) {
		Connection conn = open();
		if (conn == null) return null;

		// Prvo tražimo adresu
		System.out.println("Unesite grad distributera:");
		String grad = scanner.nextLine();
		System.out.println("Unesite državu distributera:");
		String drzava = scanner.nextLine();
		System.out.println("Unesite ulicu distributera:");
		String ulica = scanner.nextLine();
		System.out.println("Unesite broj ulice distributera:");
		String brojUlice = scanner.nextLine();
		System.out.println("Unesite poštanski broj distributera:");
		String postanskiBroj = scanner.nextLine();

		// Dobijamo ID adrese (ili je dodajemo ako ne postoji)
		Integer adresaId = getOrCreateAdresa(grad, drzava, ulica, brojUlice, postanskiBroj, scanner);
		if (adresaId == null) {
			System.out.println("Greška pri dodavanju adrese!");
			return null;
		}

		// SQL upit za dodavanje distributera
		String sql = "INSERT INTO distributer (ime, broj_telefona, adresa_id) VALUES (?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, ime);
			ps.setString(2, brojTelefona);
			ps.setInt(3, adresaId);
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				int id = rs.getInt(1);
				close(conn);
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close(conn);
		return null;
	}



	public void prikaziEntitete(String tabela, String kolona) {
		Connection conn = open();
		if (conn == null) return;

		String sql = "SELECT " + kolona + " FROM " + tabela;
		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			System.out.println("\nDostupni " + tabela + ":");
			while (rs.next()) {
				System.out.println("- " + rs.getString(kolona));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close(conn);
	}
	public void prikaziGlavneKategorije() {
		Connection conn = open();
		if (conn == null) return;

		// SQL upit za prikazivanje glavnih kategorija
		String sql = "SELECT * FROM kategorija WHERE roditelj_id IS NULL";

		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			System.out.println("\nGlavne kategorije:");

			// Petlja za prikazivanje svih glavnih kategorija
			while (rs.next()) {
				int id = rs.getInt(1); // ID je obično u prvoj koloni
				String naziv = rs.getString(2); // Naziv je u drugoj koloni
				System.out.println(id + ". " + naziv);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}

	// Metoda koja prikazuje potkategorije za određenu glavnu kategoriju
	public void prikaziPotkategorije(int roditeljId) {
		Connection conn = open();
		if (conn == null) return;

		// SQL upit za prikazivanje potkategorija za odabranu glavnu kategoriju
		String sql = "SELECT * FROM kategorija WHERE roditelj_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, roditeljId); // Postavljamo roditelj_id na izabranu glavnu kategoriju

			try (ResultSet rs = ps.executeQuery()) {
				System.out.println("\nPotkategorije za glavnu kategoriju:");

				// Petlja za prikazivanje potkategorija
				while (rs.next()) {
					String nazivPotkategorije = rs.getString("naziv");
					int potkategorijaId = rs.getInt("id");

					// Prikazivanje potkategorija sa njihovim ID-evima
					System.out.println("- " + nazivPotkategorije);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}



	public Integer insertEntitet(String tabela, String kolona, String vrednost) {
		Connection conn = open();
		if (conn == null) return null;

		String sql = "INSERT INTO " + tabela + " (" + kolona + ") VALUES (?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, vrednost);
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				int id = rs.getInt(1);
				close(conn);
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close(conn);
		return null;
	}

	public Integer insertKnjiga(String ISBN, String naslov, int brojStranica, double cena,
								int distributerId, int izdavacId, int kategorijaId) {
		Connection conn = open();
		if (conn == null) return null;

		String sql = "INSERT INTO knjiga (ISBN, naslov, broj_stranica, cena, distributer_id, izdavac_id, kategorija_id) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, ISBN);
			ps.setString(2, naslov);
			ps.setInt(3, brojStranica);
			ps.setDouble(4, cena);
			ps.setInt(5, distributerId);
			ps.setInt(6, izdavacId);
			ps.setInt(7, kategorijaId);

			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return null;
	}

	public Integer getOrCreateEntitet(String tabela, String kolona, String vrednost, Scanner scanner) {
		Integer id = getIdByName(tabela, kolona, vrednost);
		if (id != null) return id;

		System.out.println(tabela + " sa imenom '" + vrednost + "' ne postoji.");
		System.out.println("Da li želite da ga dodate? (da/ne)");
		String odgovor = scanner.nextLine();

		if (odgovor.equalsIgnoreCase("da")) {
			if (tabela.equals("distributer")) { // Za distributera tražimo i broj telefona
				System.out.println("Unesite broj telefona za distributera:");
				String brojTelefona = scanner.nextLine();
				return insertDistributer(vrednost, brojTelefona, scanner);
			} else if (tabela.equals("izdavac")){
				System.out.println("Unesite godinu osnivanja za izdavaca:");
				Integer godinaOsnivanja = scanner.nextInt();
				scanner.nextLine();
				return insertIzdavac(vrednost, godinaOsnivanja, scanner);
			} else {
				return insertEntitet(tabela, kolona, vrednost);
			}
		} else {
			System.out.println("Molimo unesite drugo ime:");
			return getOrCreateEntitet(tabela, kolona, scanner.nextLine(), scanner);
		}
	}


	public boolean azurirajKnjigu(String ISBN, String naslov, int brojStranica, double cena, int distributerId, int izdavacId, int kategorijaId) {
		Connection conn = open();
		if (conn == null) return false;

		String sql = "UPDATE knjiga SET ISBN = ?, naslov = ?, broj_stranica = ?, cena = ?, distributer_id = ?, izdavac_id = ?, kategorija_id = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, ISBN);
			ps.setString(2, naslov);
			ps.setInt(3, brojStranica);
			ps.setDouble(4, cena);
			ps.setInt(5, distributerId);
			ps.setInt(6, izdavacId);
			ps.setInt(7, kategorijaId);

			int rowsAffected = ps.executeUpdate();
			close(conn);
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			close(conn);
			return false;
		}
	}
	public void azurirajKnjiguIzKonzole() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Unesite ISBN knjige koju želite da ažurirate:");
		String ISBN = scanner.nextLine();

		// Proverite da li knjiga postoji

		if (!knjigaPostoji(ISBN) ) {
			System.out.println("Knjiga ne postoji.");
			return;
		}

		System.out.println("Unesite naziv knjige:");
		String nazivKnjige = scanner.nextLine();
		System.out.println("Unesite broj stranica knjige:");
		int brojStranica = scanner.nextInt();
		System.out.println("Unesite cenu knjige:");
		double cena = scanner.nextDouble();
		scanner.nextLine();  // Clear the buffer

		// Odabir distributera, izdavača, kategorije putem getOrCreateEntitet
		System.out.println("Unesite ime distributera:");
		String distributerIme = scanner.nextLine();
		Integer distributerId = getOrCreateEntitet("distributer", "ime", distributerIme, scanner);

		System.out.println("Unesite ime izdavača:");
		String izdavacIme = scanner.nextLine();
		Integer izdavacId = getOrCreateEntitet("izdavac", "ime", izdavacIme, scanner);

		System.out.println("Unesite naziv kategorije:");
		String kategorijaNaziv = scanner.nextLine();
		Integer kategorijaId = getOrCreateEntitet("kategorija", "naziv", kategorijaNaziv, scanner);

		// Ažuriranje knjige sa novim podacima
		boolean uspeh = azurirajKnjigu(ISBN, nazivKnjige, brojStranica, cena, distributerId, izdavacId, kategorijaId);

		if (uspeh) {
			System.out.println("Podaci o knjizi su uspešno ažurirani.");
		} else {
			System.out.println("Došlo je do greške pri ažuriranju podataka.");
		}
	}
	public boolean azurirajAutora(String ime, String prezime) {
		Connection conn = open();
		if (conn == null) return false;

		String sql = "UPDATE autor SET ime = ?, prezime = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, ime);
			ps.setString(2, prezime);

			int rowsAffected = ps.executeUpdate();
			close(conn);
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			close(conn);
			return false;
		}
	}
	public boolean azurirajIzdavaca(String ime, int godinaOsnivanja, int adresaId) {
		Connection conn = open();
		if (conn == null) return false;

		String sql = "UPDATE izdavac SET ime = ?, godina_osnivanja = ?, adresa_id = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, ime);
			ps.setInt(2, godinaOsnivanja);
			ps.setInt(3, adresaId);

			int rowsAffected = ps.executeUpdate();
			close(conn);
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			close(conn);
			return false;
		}
	}
	public boolean azurirajDistributera(String ime, String brojTelefona, int adresaId) {
		Connection conn = open();
		if (conn == null) return false;

		String sql = "UPDATE distributer SET ime = ?, broj_telefona = ?, adresa_id = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, ime);
			ps.setString(2, brojTelefona);
			ps.setInt(3, adresaId);

			int rowsAffected = ps.executeUpdate();
			close(conn);
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			close(conn);
			return false;
		}
	}
	public boolean azurirajKupca(String ime, String prezime, String brojTelefona, int brojTransakcija, int vrstaKupcaId) {
		Connection conn = open();
		if (conn == null) return false;

		String sql = "UPDATE kupac SET ime = ?, prezime = ?, broj_telefona = ?, broj_transakcija = ?, vrsta_kupca_id = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, ime);
			ps.setString(2, prezime);
			ps.setString(3, brojTelefona);
			ps.setInt(4, brojTransakcija);
			ps.setInt(5, vrstaKupcaId);

			int rowsAffected = ps.executeUpdate();
			close(conn);
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			close(conn);
			return false;
		}
	}


	public void dodajKnjiguIzKonzole() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Unesite ISBN knjige:");
		String ISBN = scanner.nextLine();
		if (knjigaPostoji(ISBN)) {
			System.out.println("Knjiga sa ISBN-om '" + ISBN + "' već postoji u bazi. Nije moguće ponovo dodati.");
			return;
		}


		System.out.println("Unesite naslov knjige:");
		String naslov = scanner.nextLine();

		System.out.println("Unesite broj stranica:");
		int brojStranica;
		try {
			brojStranica = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Niste uneli validan broj stranica.");
			return;
		}

		scanner.nextLine();

		System.out.println("Unesite cenu knjige:");
		double cena;
		try {
			cena = Double.parseDouble(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Niste uneli validnu cenu.");
			return;
		}

		scanner.nextLine(); // Prazan unos zbog nextInt()

		prikaziEntitete("distributer", "ime");
		System.out.println("\nUnesite ime distributera:");
		String imeDistributera = scanner.nextLine();
		Integer distributerId = getOrCreateEntitet("distributer", "ime", imeDistributera, scanner);

		prikaziEntitete("izdavac", "ime");
		System.out.println("\nUnesite ime izdavača:");
		String imeIzdavaca = scanner.nextLine();
		Integer izdavacId = getOrCreateEntitet("izdavac", "ime", imeIzdavaca, scanner);

		prikaziGlavneKategorije();
		System.out.println("\nUnesite naziv glavne kategorije:");
		String nazivGlavneKategorije = scanner.nextLine();
		Integer glavaKategorijaId = getIdByName("kategorija", "naziv", nazivGlavneKategorije);
		prikaziPotkategorije(glavaKategorijaId);
		System.out.println("\nUnesite naziv potkategorije:");
		String nazivPotkategorije = scanner.nextLine();
		Integer kategorijaId = getIdByName("kategorija", "naziv", nazivPotkategorije);

		Integer bookId = insertKnjiga(ISBN, naslov, brojStranica, cena, distributerId, izdavacId, kategorijaId);

		if (bookId != null) {
			System.out.println("Knjiga uspešno dodata u bazu! ID: " + bookId);
			dodajAutoreZaKnjigu(bookId, scanner);
		} else {
			System.out.println("Greška prilikom dodavanja knjige.");
		}
	}

	public boolean knjigaPostoji(String ISBN) {
		Connection conn = open();
		if (conn == null) return false;

		String sql = "SELECT 1 FROM knjiga WHERE ISBN = ? LIMIT 1";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, ISBN);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return true; // Postoji zapis za traženi ISBN
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return false;
	}

	public void dodajKategorijuIzKonzole() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("\nDa li dodajete:");
		System.out.println("1) Glavnu kategoriju");
		System.out.println("2) Potkategoriju");
		System.out.print("Izbor: ");
		String tip = scanner.nextLine();
		if (!tip.equals("1") && !tip.equals("2")) {
			System.out.println("Neispravan izbor!");
			return;
		}

		System.out.print("Unesite naziv kategorije: ");
		String naziv = scanner.nextLine();


		Integer roditeljId = null;
		if (tip.equals("1")) { // Provera samo za glavnu kategoriju
			if (postojiGlavnaKategorija(naziv)) {
				System.out.println("Glavna kategorija sa tim nazivom već postoji!");
				return;
			}
		}


		if (tip.equals("2")) {
			// Ako se dodaje potkategorija, prikaži glavne kategorije za izbor
			prikaziGlavneKategorije();
			System.out.print("Unesite ID glavne kategorije: ");

			try {
				roditeljId = Integer.parseInt(scanner.nextLine());
				if (!proveriPostojanjeKategorije(roditeljId)) {
					System.out.println("Ne postoji kategorija sa tim ID!");
					return;
				}

			} catch (NumberFormatException e) {
				System.out.println("Neispravan unos ID-a!");
				return;
			}
		}

		// Ubacivanje u bazu
		Integer kategorijaId = insertKategorija(naziv, roditeljId);

		if (kategorijaId != null) {
			System.out.println("Kategorija uspešno dodata! ID: " + kategorijaId);
		} else {
			System.out.println("Greška pri dodavanju kategorije!");
		}
	}

	private Integer insertKategorija(String naziv, Integer roditeljId) {
		Connection conn = open();
		if (conn == null) return null;

		String sql = "INSERT INTO kategorija (naziv, roditelj_id) VALUES (?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, naziv);
			if (roditeljId != null) {
				ps.setInt(2, roditeljId);
			} else {
				ps.setNull(2, Types.INTEGER);
			}

			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("Greška pri unosu u bazu: " + e.getMessage());
		} finally {
			close(conn);
		}
		return null;
	}

	private boolean proveriPostojanjeKategorije(int id) {
		Connection conn = open();
		if (conn == null) return false;

		String sql = "SELECT id FROM kategorija WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(conn);
		}
	}

	private boolean postojiGlavnaKategorija(String naziv) {
		Connection conn = open();
		if (conn == null) return false;

		String sql = "SELECT id FROM kategorija WHERE naziv = ? AND roditelj_id IS NULL";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, naziv);
			ResultSet rs = ps.executeQuery();
			return rs.next(); // Ako postoji rezultat, glavna kategorija već postoji
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(conn);
		}
	}



	public void dodajAutoreZaKnjigu(int knjigaId, Scanner scanner) {
		boolean dodajJos = true;
		while (dodajJos) {
			System.out.println("Unesite ime autora:");
			String ime = scanner.nextLine();
			System.out.println("Unesite prezime autora:");
			String prezime = scanner.nextLine();

			Integer autorId = getAutorIdByImePrezime(ime, prezime);
			if (autorId == null) {
				System.out.println("Autor ne postoji. Da li želite da ga dodate? (da/ne)");
				String odluka = scanner.nextLine();
				if (odluka.equalsIgnoreCase("da")) {
					autorId = insertAutor(ime, prezime, scanner);
					if (autorId == null) {
						System.out.println("Greška pri dodavanju autora!");
						continue;
					}
				} else {
					continue;
				}
			}

			linkAutorToKnjiga(autorId, knjigaId);

			System.out.println("Da li želite da dodate još autora? (da/ne)");
			String odgovor = scanner.nextLine();
			dodajJos = odgovor.equalsIgnoreCase("da");
		}
	}

	private boolean linkAutorToKnjiga(int autorId, int knjigaId) {
		Connection conn = open();
		if (conn == null) return false;

		String checkSql = "SELECT COUNT(*) FROM autor_knjiga WHERE autor_id = ? AND knjiga_id = ?";
		String insertSql = "INSERT INTO autor_knjiga (autor_id, knjiga_id) VALUES (?, ?)";

		try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
			checkStmt.setInt(1, autorId);
			checkStmt.setInt(2, knjigaId);
			ResultSet rs = checkStmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				System.out.println("Autor je već povezan sa ovom knjigom.");
				return true; // Veza već postoji, ne treba unositi ponovo
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		// Ako veza ne postoji, tek tada pokušaj unos
		try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
			insertStmt.setInt(1, autorId);
			insertStmt.setInt(2, knjigaId);
			int affectedRows = insertStmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(conn);
		}
	}

	public Integer getIdByName(String table, String columnName, String value) {
		Connection conn = open();
		if (conn == null) return null;

		String sql = "SELECT id FROM " + table + " WHERE " + columnName + " = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, value);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				close(conn);
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close(conn);
		return null; // Ako ne pronađe ID, vrati null
	}
	public void prikaziSve(String tabela) {
		Connection conn = open();
		if (conn == null) return;

		String sql = "SELECT * FROM " + tabela;
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			System.out.println("Lista iz tabele: " + tabela);
			while (rs.next()) {
				int id = rs.getInt(1); // ID je obično u prvoj koloni
				String naziv = rs.getString(2); // Naziv je u drugoj koloni
				System.out.println(id + ". " + naziv);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}
	

	
	



}


package Knjizara;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	private void close(ResultSet rs) {
		try {
			if (rs != null) rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public Integer getOrCreateAdresa(String grad, String drzava, String ulica, String brojUlice, String postanskiBroj, Scanner scanner) {
		Integer drzavaId = getOrCreateEntitet("drzava", "naziv", drzava, scanner);
		if (drzavaId == null) return null;

		Connection conn = open();
		if (conn == null) return null;

		ResultSet rs = null;
		String checkSql = "SELECT id FROM adresa WHERE grad = ? AND drzava_id = ? AND naziv_ulice = ? AND broj_ulice = ? AND postanski_broj = ?";
		try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
			checkPs.setString(1, grad);
			checkPs.setInt(2, drzavaId);
			checkPs.setString(3, ulica);
			checkPs.setString(4, brojUlice);
			checkPs.setString(5, postanskiBroj);

			rs = checkPs.executeQuery();
			if (rs.next()) {
				return rs.getInt(1); // Return the existing address ID without closing prematurely
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			close(rs);
		}

		String insertSql = "INSERT INTO adresa (grad, drzava_id, naziv_ulice, broj_ulice, postanski_broj) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
			insertPs.setString(1, grad);
			insertPs.setInt(2, drzavaId);
			insertPs.setString(3, ulica);
			insertPs.setString(4, brojUlice);
			insertPs.setString(5, postanskiBroj);

			int affectedRows = insertPs.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = insertPs.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						return generatedKeys.getInt(1); // Return the new address ID
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			close(conn); // Close connection in the finally block
		}
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

	public Integer insertAutor (String ime, String prezime) {
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

		// Ako ne postoji, traži se unos dodatnih podataka
		System.out.println("Unesite broj telefona distributera:");
		String brojTelefona = scanner.nextLine().trim();
		if(brojTelefona.isBlank()){
			System.out.println("Broj telefona ne može biti prazan.");
			return;
		}

		// Dodavanje distributera
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
		int godinaOsnivanja;
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
			if (brojTelefona.isEmpty()) {
				System.out.println("Broj telefona ne može biti prazan.");
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
				System.out.println("Greška pri dodavanju kupca!");
				return;
			}

			// Kreiranje VIP kartice ako je potrebno
			if (vrstaKupcaId == 2) {
				if (!imaAktivnuKarticu(kupacId)) {
					if (!kreirajKarticuZaKupca(kupacId)) {
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

	private Integer insertKupac(String ime, String prezime, String brojTelefona, int vrstaKupcaId, Scanner scanner) {
		Connection conn = open();
		if (conn == null) return null;

		try {
			conn.setAutoCommit(false);

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
			if(validanBrojTelefona(brojTelefona)){
				System.out.println("Nevalidan format telefona!");
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
					conn.commit();
					return rs.getInt(1);
				} else {
					conn.rollback();
					return null;
				}
			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
			return null;
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
		Integer autorId = insertAutor(ime, prezime);
		if (autorId != null && autorId > 0) {
			System.out.println("Autor uspešno dodat (ID: " + autorId + ").");
		} else {
			System.out.println("Greška prilikom dodavanja autora.");
		}
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
		if(validanBrojTelefona(brojTelefona)){
			System.out.println("Nevalidan format telefona!");
			return null;
		}

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

		String sql = "SELECT * FROM kategorija WHERE roditelj_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, roditeljId);

			try (ResultSet rs = ps.executeQuery()) {
				System.out.println("\nPotkategorije za glavnu kategoriju:");

				// Petlja za prikazivanje potkategorija
				while (rs.next()) {
					String potkategorijaId = rs.getString(1);
					String nazivPotkategorije = rs.getString("naziv");

					// Prikazivanje potkategorija sa njihovim ID-evima
					System.out.println(potkategorijaId+". " + nazivPotkategorije);
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
			prepareStatementKnjiga(ISBN, naslov, brojStranica, cena, distributerId, izdavacId, kategorijaId, ps);

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
				int godinaOsnivanja = scanner.nextInt();
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
			prepareStatementKnjiga(ISBN, naslov, brojStranica, cena, distributerId, izdavacId, kategorijaId, ps);

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
		scanner.nextLine();

		// Odabir distributera, izdavača, kategorije
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

	public void azurirajAutora(String ime, String prezime, Scanner scanner) {
		// Provera da li autor postoji
		Integer autorId = getAutorIdByImePrezime(ime, prezime);
		if (autorId == null) {
			System.out.println("Autor sa imenom '" + ime + "' i prezimom '" + prezime + "' ne postoji.");
			return;
		}

		System.out.println("Unesite novo ime autora:");
		String novoIme = scanner.nextLine();
		System.out.println("Unesite novi prezime autora:");
		String noviPrezime = scanner.nextLine();

		String sql = "UPDATE autor SET ime = ?, prezime = ? WHERE id = ?";

		try (Connection conn = open(); //
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, novoIme);
			ps.setString(2, noviPrezime);
			ps.setInt(3, autorId);

			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Autor je uspešno ažuriran.");
			} else {
				System.out.println("Nije bilo promena u podacima autora.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean azurirajIzdavaca(String ime, Scanner scanner) {

		Integer izdavacId = getIdByName("izdavac", "ime", ime);
		if (izdavacId == null) {
			System.out.println("Izdavač sa imenom '" + ime + "' ne postoji.");
			return false;
		}

		System.out.println("Unesite novo ime izdavača:");
		String novoIme = scanner.nextLine();
		System.out.println("Unesite novu godinu osnivanja izdavača:");
		String novaGodinaOsnivanja = scanner.nextLine();
		if(novaGodinaOsnivanja.isEmpty()) {
			System.out.println("Godina osnivanja ne može biti prazna.");
			return false;
		}

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
			return false;
		}

		Connection conn = open();
		if (conn == null) return false;

		String sql = "UPDATE izdavac SET ime = ?, godina_osnivanja = ?, adresa_id = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, novoIme);
			ps.setString(2, novaGodinaOsnivanja);
			ps.setInt(3, adresaId);
			ps.setInt(4, izdavacId);

			int rowsAffected = ps.executeUpdate();
			close(conn);
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			close(conn);
			return false;
		}
	}
	public boolean azurirajDistributera(String ime, Scanner scanner) {

		Integer distributerId = getIdByName("distributer", "ime", ime);
		if (distributerId == null) {
			System.out.println("Distributer sa imenom '" + ime + "' ne postoji.");
			return false;
		}

		System.out.println("Unesite novo ime distributera:");
		String novoIme = scanner.nextLine();
		System.out.println("Unesite novi broj telefona za distributera:");
		String noviBrojTelefona = scanner.nextLine().trim();
		if(validanBrojTelefona(noviBrojTelefona)) {
			System.out.println("Niste uneli validan broj telefona za distributera.");
			return false;
		}

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
			return false;
		}

		Connection conn = open();
		if (conn == null) return false;

		String sql = "UPDATE distributer SET ime = ?, broj_telefona = ?, adresa_id = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, novoIme);
			ps.setString(2, noviBrojTelefona);
			ps.setInt(3, adresaId);
			ps.setInt(4, distributerId);

			int rowsAffected = ps.executeUpdate();
			close(conn);
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			close(conn);
			return false;
		}
	}
	public boolean azurirajKupca(String brojTelefona, Scanner scanner) {

		if (validanBrojTelefona(brojTelefona)) {
			System.out.println("Niste uneli validan broj telefona za kupca.");
			return false;
		}

		// Provera da li kupac postoji
		Integer kupacId = getIdByName("kupac", "broj_telefona", brojTelefona);
		if (kupacId == null) {
			System.out.println("Kupac sa brojem telefona: '" + brojTelefona + "' ne postoji.");
			return false;
		}

		// Unos novih podataka za kupca
		System.out.println("Unesite novo ime kupca:");
		String novoIme = scanner.nextLine();
		System.out.println("Unesite novo prezime kupca:");
		String novoPrezime = scanner.nextLine();
		System.out.println("Unesite vrstu kupca: ('1' za Standardni, '2' za VIP)");
		int novaVrstaKupca = scanner.nextInt();
		scanner.nextLine();

		if (novaVrstaKupca != 1 && novaVrstaKupca != 2) {
			System.out.println("Vrsta kupca mora biti '1' ili '2'.");
			return false;
		}

		// Ako kupac postaje VIP, proveri i ažuriraj VIP karticu
		if (novaVrstaKupca == 1) {
			if (imaAktivnuKarticu(kupacId)) {
				// Ako već ima aktivnu karticu, deaktiviraj je
				if (!deaktivirajKarticu(kupacId)) {
					System.out.println("Greška pri deaktivaciji stare VIP kartice.");
					return false;
				}
			}

		}else{
			if (!kreirajKarticuZaKupca(kupacId)) {
				System.out.println("Greška pri kreiranju nove VIP kartice.");
				return false;
			}
		}

		// Unos novog broja telefona
		System.out.println("Unesite novi broj telefona za kupca:");
		String noviBrojTelefona = scanner.nextLine().trim();
		if (validanBrojTelefona(noviBrojTelefona)) {
			System.out.println("Niste uneli validan novi broj telefona za kupca.");
			return false;
		}

		// Unos podataka za adresu
		System.out.println("Unesite grad kupca:");
		String grad = scanner.nextLine();
		System.out.println("Unesite državu kupca:");
		String drzava = scanner.nextLine();
		System.out.println("Unesite ulicu kupca:");
		String ulica = scanner.nextLine();
		System.out.println("Unesite broj ulice kupca:");
		String brojUlice = scanner.nextLine();
		System.out.println("Unesite poštanski broj kupca:");
		String postanskiBroj = scanner.nextLine();

		// Dobijamo ID adrese (ili je dodajemo ako ne postoji)
		Integer adresaId = getOrCreateAdresa(grad, drzava, ulica, brojUlice, postanskiBroj, scanner);
		if (adresaId == null) {
			System.out.println("Greška pri dodavanju adrese!");
			return false;
		}

		String sql = "UPDATE kupac SET ime = ?, prezime = ?, broj_telefona = ?, vrsta_kupca_id = ?, adresa_id = ? WHERE id = ?";

		try (Connection conn = open()) {
			if (conn == null) return false;

			// Provera da li su nove vrednosti različite od postojećih
			String proveraSql = "SELECT ime, prezime, broj_telefona, vrsta_kupca_id, adresa_id FROM kupac WHERE id = ?";
			try (PreparedStatement proveraPs = conn.prepareStatement(proveraSql)) {
				proveraPs.setInt(1, kupacId);
				ResultSet rs = proveraPs.executeQuery();
				if (rs.next()) {
					String staroIme = rs.getString("ime");
					String staroPrezime = rs.getString("prezime");
					String stariBrojTelefona = rs.getString("broj_telefona");
					int staraVrstaKupca = rs.getInt("vrsta_kupca_id");
					int stariAdresaId = rs.getInt("adresa_id");

					if (novoIme.equals(staroIme) && novoPrezime.equals(staroPrezime) && noviBrojTelefona.equals(stariBrojTelefona)
							&& novaVrstaKupca == staraVrstaKupca && adresaId == stariAdresaId) {
						System.out.println("Nema promena u podacima kupca.");
						return false;
					}
				}
			}
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, novoIme);
				ps.setString(2, novoPrezime);
				ps.setString(3, noviBrojTelefona);
				ps.setInt(4, novaVrstaKupca);
				ps.setInt(5, adresaId);
				ps.setInt(6, kupacId);

				int rowsAffected = ps.executeUpdate();
				return rowsAffected > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
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


		System.out.println("Unesite cenu knjige:");
		double cena;
		try {
			cena = Double.parseDouble(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Niste uneli validnu cenu.");
			return;
		}


		prikaziEntitete("distributer", "ime");
		System.out.println("\nUnesite ime distributera:");
		String imeDistributera = scanner.nextLine();
		Integer distributerId = getOrCreateEntitet("distributer", "ime", imeDistributera, scanner);

		prikaziEntitete("izdavac", "ime");
		System.out.println("\nUnesite ime izdavača:");
		String imeIzdavaca = scanner.nextLine();
		Integer izdavacId = getOrCreateEntitet("izdavac", "ime", imeIzdavaca, scanner);

		prikaziGlavneKategorije();
		System.out.println("\nUnesite ID glavne kategorije:");
		int glavaKategorijaId = scanner.nextInt();
		prikaziPotkategorije(glavaKategorijaId);
		System.out.println("\nUnesite ID potkategorije:");
		int kategorijaId = scanner.nextInt();

		Integer bookId = insertKnjiga(ISBN, naslov, brojStranica, cena, distributerId, izdavacId, kategorijaId);

		if (bookId != null) {
			System.out.println("Knjiga uspešno dodata u bazu! ID: " + bookId);
			dodajAutoreZaKnjigu(bookId);
		} else {
			System.out.println("Greška prilikom dodavanja knjige.");
		}
	}



	public void dodajKategorijuIzKonzole() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("\nDa li dodajete:");
		System.out.println("1) Glavnu kategoriju");
		System.out.println("2) Potkategoriju");
		System.out.print("Izbor: ");
		String tip = scanner.nextLine();
		if (!tip.equals("1") && !tip.equals("2")) {
			System.out.println("Nevalidan izbor!");
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

	public void prikaziSveKnjige() {
		Connection conn = open();
		if (conn == null) return;

		String sql = """
                SELECT\s
                    k.naslov,
                    GROUP_CONCAT(DISTINCT CONCAT(a.ime, ' ', a.prezime)) AS autori,
                    k.ISBN,
                    k.broj_stranica AS "Broj stranica",
                    k.cena AS "Cena €",
                    d.ime AS Distributer,
                    i.ime AS Izdavač,
                    kk.naziv as Kategorija,
                    pk.naziv as Potkategorija
                FROM\s
                    knjiga k
                JOIN\s
                    autor_knjiga ak ON k.id = ak.knjiga_id
                JOIN\s
                    autor a ON a.id = ak.autor_id
                JOIN\s
                    izdavac i ON k.izdavac_id = i.id
                JOIN\s
                    distributer d ON k.distributer_id = d.id
                LEFT JOIN\s
                    kategorija kk ON k.kategorija_id = kk.id
                left JOIN\s
                \tkategorija pk on kk.id = pk.roditelj_id
                
                GROUP BY\s
                    k.id, k.naslov, k.ISBN, k.broj_stranica, k.cena, d.id, i.id,kk.naziv
                    order by k.naslov asc;
                """;
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			System.out.println("Knjige na stanju: ");
			while (rs.next()) {
				String naslov = rs.getString("naslov");
				String autori = rs.getString("autori");
				String ISBN = rs.getString("ISBN");
				int brojStranica = rs.getInt("Broj stranica");
				double cena = rs.getDouble("Cena €");
				String distributer = rs.getString("Distributer");
				String izdavac = rs.getString("Izdavač");
				String kategorija = rs.getString("Kategorija");
				String potkategorija = rs.getString("Potkategorija");

				// Provera ako je potkategorija null i postavljanje podrazumevane vrednosti
				if (potkategorija == null) {
					potkategorija = "N/A";
				}

				// Ispis podataka za svaku knjigu
				System.out.println("Naslov: " + naslov);
				System.out.println("Autori: " + autori);
				System.out.println("ISBN: " + ISBN);
				System.out.println("Broj stranica: " + brojStranica);
				System.out.println("Cena: €" + cena);
				System.out.println("Distributer: " + distributer);
				System.out.println("Izdavač: " + izdavac);
				System.out.println("Kategorija: " + kategorija);
				System.out.println("Potkategorija: " + potkategorija);
				System.out.println("-----");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}

	public void prikaziKnjiguPoISBN() {
		Connection conn = open();
		if (conn == null) return;

		Scanner scanner = new Scanner(System.in);
		System.out.print("Unesite ISBN knjige: ");
		String isbnInput = scanner.nextLine();

		String sql = "SELECT " +
					 "k.naslov, " +
					 "GROUP_CONCAT(DISTINCT CONCAT(a.ime, ' ', a.prezime)) AS autori, " +
					 "k.ISBN, " +
					 "k.broj_stranica AS \"Broj stranica\", " +
					 "k.cena AS \"Cena €\", " +
					 "d.ime AS Distributer, " +
					 "i.ime AS Izdavač, " +
					 "kk.naziv AS Kategorija, " +
					 "gk.naziv AS GlavnaKategorija " +
					 "FROM " +
					 "knjiga k " +
					 "JOIN autor_knjiga ak ON k.id = ak.knjiga_id " +
					 "JOIN autor a ON a.id = ak.autor_id " +
					 "JOIN izdavac i ON k.izdavac_id = i.id " +
					 "JOIN distributer d ON k.distributer_id = d.id " +
					 "LEFT JOIN kategorija kk ON k.kategorija_id = kk.id " +
					 "LEFT JOIN kategorija gk ON kk.roditelj_id = gk.id " +
					 "WHERE " +
					 "k.ISBN = ? " +
					 "GROUP BY " +
					 "k.id, k.naslov, k.ISBN, k.broj_stranica, k.cena, d.id, i.id, kk.naziv, gk.naziv " +
					 "ORDER BY " +
					 "k.naslov ASC;";

		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, isbnInput);
			ResultSet rs = pst.executeQuery();

			boolean knjigaPronadjena = false;

			while (rs.next()) {
				knjigaPronadjena = true;

				String naslov = rs.getString("naslov");
				String autori = rs.getString("autori");
				String ISBN = rs.getString("ISBN");
				int brojStranica = rs.getInt("Broj stranica");
				double cena = rs.getDouble("Cena €");
				String distributer = rs.getString("Distributer");
				String izdavac = rs.getString("Izdavač");
				String kategorija = rs.getString("Kategorija");
				String glavnaKategorija = rs.getString("GlavnaKategorija");

				// Ispis podataka o knjizi
				System.out.println("\n--- Detalji o knjizi ---");
				System.out.println("Naslov: " + naslov);
				System.out.println("Autori: " + autori);
				System.out.println("ISBN: " + ISBN);
				System.out.println("Broj stranica: " + brojStranica);
				System.out.println("Cena: €" + cena);
				System.out.println("Distributer: " + distributer);
				System.out.println("Izdavač: " + izdavac);
				System.out.println("Kategorija: " + kategorija);
				System.out.println("Glavna kategorija: " + (glavnaKategorija != null ? glavnaKategorija : "N/A"));
				System.out.println("-------------------------");
			}

			if (!knjigaPronadjena) {
				System.out.println("Knjiga sa ISBN: " + isbnInput + " nije pronađena.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}




	public void dodajAutoreZaKnjigu(int knjigaId) {
		Scanner scanner = new Scanner(System.in);
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
					autorId = insertAutor(ime, prezime);
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

	private void linkAutorToKnjiga(int autorId, int knjigaId) {
		Connection conn = open();
		if (conn == null) return;

		String checkSql = "SELECT COUNT(*) FROM autor_knjiga WHERE autor_id = ? AND knjiga_id = ?";
		String insertSql = "INSERT INTO autor_knjiga (autor_id, knjiga_id) VALUES (?, ?)";

		try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
			checkStmt.setInt(1, autorId);
			checkStmt.setInt(2, knjigaId);
			ResultSet rs = checkStmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				System.out.println("Autor je već povezan sa ovom knjigom.");
				return; // Veza već postoji, ne treba unositi ponovo
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
			insertStmt.setInt(1, autorId);
			insertStmt.setInt(2, knjigaId);
        } catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}

	public void spojiDistributeraIIzdavaca() {
		Scanner scanner = new Scanner(System.in);
		boolean dodajJos = true;
		while (dodajJos) {
			System.out.println("Unesite ime distributera:");
			prikaziSve("distributer");
			String distributer = scanner.nextLine();
			System.out.println("Unesite ime izdavača:");
			prikaziSve("izdavac");
			String izdavac = scanner.nextLine();

			Integer distributerId = getOrCreateEntitet("distributer","ime",distributer,scanner);
			Integer izdavacId = getOrCreateEntitet("izdavac","ime",izdavac,scanner);
			if(linkDistributerToIzdavac(distributerId, izdavacId)){
				System.out.println(distributer + " "+izdavac +" su uspešno spojeni!");
			}

			System.out.println("Da li želite da dodate još? (da/ne)");
			String odgovor = scanner.nextLine();
			dodajJos = odgovor.equalsIgnoreCase("da");
		}
	}

	private boolean linkDistributerToIzdavac(int distributerId, int izdavacId) {
		Connection conn = open();
		if (conn == null) return false;

		String checkSql = "SELECT COUNT(*) FROM distributer_izdavac WHERE distributer_id = ? AND izdavac_id = ?";
		String insertSql = "INSERT INTO distributer_izdavac (distributer_id, izdavac_id) VALUES (?, ?)";

		try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
			checkStmt.setInt(1, distributerId);
			checkStmt.setInt(2, izdavacId);
			ResultSet rs = checkStmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				System.out.println("Distributer i izdavac su već povezani.");
				return true; // Veza već postoji, ne treba unositi ponovo
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
			insertStmt.setInt(1, distributerId);
			insertStmt.setInt(2, izdavacId);
			int affectedRows = insertStmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(conn);
		}
	}

	public void izdajRacunIzKonzole(Scanner scanner	) {
		Connection conn = open();
		if (conn == null) return;

		try {
			conn.setAutoCommit(false);

			// Unos broja telefona kupca
			System.out.println("Unesite broj telefona kupca:");
			String brojTelefona = scanner.nextLine().trim();

			// Provera postojanja kupca
			Integer kupacId = getIdByName("kupac", "broj_telefona", brojTelefona);
			if (kupacId == null) {
				System.out.println("Kupac ne postoji. Kreiranje novog kupca...");
				dodajKupcaIzKonzole();
				kupacId = getIdByName("kupac", "broj_telefona", brojTelefona);
			}

			// Provera VIP kartice i popusta
			BigDecimal vipPopust = BigDecimal.ZERO;
			String vipQuery = "SELECT popust FROM vip_kartica WHERE kupac_id = ? AND jeAktivna = TRUE";
			PreparedStatement vipStmt = conn.prepareStatement(vipQuery);
			vipStmt.setInt(1, kupacId);
			ResultSet vipRs = vipStmt.executeQuery();
			if (vipRs.next()) {
				vipPopust = vipRs.getBigDecimal("popust");
			}

			// Unos vrste kupovine
			System.out.println("Vrsta kupovine (1 - U prodavnici, 2 - Online):");
			int vrstaKupovineId = Integer.parseInt(scanner.nextLine().trim());

			// Ako je online kupovina, traži unos adrese
			Integer adresaZaDostavuId = null;
			Integer prodavacId = null;
			String adresaZaDostavu = "";
			String prodavacImePrezime = null;

			if (vrstaKupovineId == 2) {
				System.out.println("Unesite grad:");
				String grad = scanner.nextLine();
				System.out.println("Unesite državu:");
				String drzava = scanner.nextLine();
				System.out.println("Unesite naziv ulice:");
				String ulica = scanner.nextLine();
				System.out.println("Unesite broj ulice:");
				String brojUlice = scanner.nextLine();
				System.out.println("Unesite poštanski broj za grad " + grad + ":");
				String postanskiBroj = scanner.nextLine();

				if (grad.isBlank() || ulica.isBlank() || !postanskiBroj.matches("\\d+")) {
					System.out.println("Nevalidan unos adrese!");
					return;
				}

				Integer adresaId = getOrCreateAdresa(grad, drzava, ulica, brojUlice, postanskiBroj, scanner);
				if (adresaId == null) {
					System.out.println("Greška pri dodavanju adrese!");
					conn.rollback();
					return;
				}
				adresaZaDostavuId = adresaId;
				String adresaQuery = "SELECT grad, d.naziv as drzava, naziv_ulice, broj_ulice, postanski_broj FROM adresa join drzava d on d.id = drzava_id WHERE adresa.id = ?";
				PreparedStatement adresaStmt = conn.prepareStatement(adresaQuery);
				adresaStmt.setInt(1, adresaZaDostavuId);
				ResultSet adresaRs = adresaStmt.executeQuery();
				if (adresaRs.next()) {
					adresaZaDostavu = adresaRs.getString("naziv_ulice") + " " + adresaRs.getString("broj_ulice") + ", " +
							adresaRs.getString("grad") + ", " + adresaRs.getString("drzava") + ", " +
							adresaRs.getString("postanski_broj");
				}

			} else {
				System.out.println("Unesite ID prodavca:");
				prodavacId = Integer.parseInt(scanner.nextLine().trim());
				prodavacImePrezime = getImePrezimeById("prodavac", "id", prodavacId);
			}

			// Kreiranje računa
			String insertRacun = "INSERT INTO racun (datum, ukupna_cena, vrsta_kupovine_id, prodavac_id, kupac_id, adresaZaDostavu_id) VALUES (CURRENT_DATE, ?, ?, ?, ?, ?)";
			PreparedStatement racunStmt = conn.prepareStatement(insertRacun, Statement.RETURN_GENERATED_KEYS);
			racunStmt.setBigDecimal(1, BigDecimal.ZERO);
			racunStmt.setInt(2, vrstaKupovineId);
			if (prodavacId != null) {
				racunStmt.setInt(3, prodavacId);
			} else {
				racunStmt.setNull(3, Types.INTEGER);
			}
			racunStmt.setInt(4, kupacId);
			if (adresaZaDostavuId != null) {
				racunStmt.setInt(5, adresaZaDostavuId);
			} else {
				racunStmt.setNull(5, Types.INTEGER);
			}
			racunStmt.executeUpdate();

			// Dobijanje ID-a računa
			ResultSet rs = racunStmt.getGeneratedKeys();
			if (!rs.next()) throw new SQLException("Neuspešno kreiranje računa.");
			int racunId = rs.getInt(1);

			// Dodavanje knjiga u stavke
			BigDecimal ukupnaCena = BigDecimal.ZERO;
			String knjigaQuery = "SELECT id, cena, naslov FROM knjiga WHERE ISBN = ?";
			String insertStavka = "INSERT INTO stavka (kolicina, popust, jedinicna_cena, racun_id, knjiga_id) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement knjigaStmt = conn.prepareStatement(knjigaQuery);
			PreparedStatement stavkaStmt = conn.prepareStatement(insertStavka);

			List<String> isbnLista = new ArrayList<>();
			System.out.println("Unesite ISBN knjiga (unos '0' za završetak):");
			while (true) {
				String isbn = scanner.nextLine().trim();
				if (isbn.equalsIgnoreCase("0")) break;
				isbnLista.add(isbn);
			}

			for (String isbn : isbnLista) {
				knjigaStmt.setString(1, isbn);
				ResultSet knjigaRs = knjigaStmt.executeQuery();

				if (knjigaRs.next()) {
					int knjigaId = knjigaRs.getInt("id");
					BigDecimal cena = knjigaRs.getBigDecimal("cena");
					String naslov = knjigaRs.getString("naslov");

					// Unos količine
					int kolicina = 0; // Initial value
					while (kolicina <= 0) {
						System.out.println("Unesite količinu za knjigu: " + naslov);
						try {
							kolicina = Integer.parseInt(scanner.nextLine().trim());
							if (kolicina <= 0) {
								System.out.println("Greška: Količina mora biti pozitivan broj veći od 0. Pokušajte ponovo.");
							}
						} catch (NumberFormatException e) {
							System.out.println("Greška: Unos mora biti ceo broj. Pokušajte ponovo.");
						}
					}



					// Unos dodatnog popusta

					BigDecimal dodatniPopust; // Initialize the discount to zero
					while (true) { // Validation loop
						System.out.println("Unesite dodatni popust (%) za knjigu: " + naslov);
						try {
							dodatniPopust = new BigDecimal(scanner.nextLine().trim());

							// Validate that the discount is not negative
							if (dodatniPopust.compareTo(BigDecimal.ZERO) < 0) {
								System.out.println("Greška: Popust ne može biti manji od 0. Pokušajte ponovo.");
							} else {
								// Optional: Limit the discount to a maximum of 100%
								dodatniPopust = dodatniPopust.min(new BigDecimal(100));
								break; // Exit the loop if input is valid
							}
						} catch (NumberFormatException e) {
							System.out.println("Greška: Unos mora biti broj. Pokušajte ponovo.");
						}
					}

					// Obračun konačne cene sa popustom
					BigDecimal ukupniPopust = vipPopust.add(dodatniPopust).min(new BigDecimal(100));
					BigDecimal popustIznos = cena.multiply(ukupniPopust).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
					BigDecimal konacnaCena = cena.subtract(popustIznos).setScale(2, RoundingMode.HALF_UP);

					// Dodavanje u stavku
					stavkaStmt.setInt(1, kolicina);
					stavkaStmt.setBigDecimal(2, ukupniPopust);
					stavkaStmt.setBigDecimal(3, konacnaCena);
					stavkaStmt.setInt(4, racunId);
					stavkaStmt.setInt(5, knjigaId);
					stavkaStmt.executeUpdate();

					// Dodavanje u ukupnu cenu
					ukupnaCena = ukupnaCena.add(konacnaCena.multiply(new BigDecimal(kolicina)));
				} else {
					System.out.println("Knjiga sa ISBN " + isbn + " nije pronađena.");
				}
			}

			// Ažuriranje ukupne cene u računu
			String updateRacun = "UPDATE racun SET ukupna_cena = ? WHERE id = ?";
			PreparedStatement updateStmt = conn.prepareStatement(updateRacun);
			updateStmt.setBigDecimal(1, ukupnaCena);
			updateStmt.setInt(2, racunId);
			updateStmt.executeUpdate();

			String kupacImePrezime = getImePrezimeById("kupac", "id", kupacId);

			conn.commit();
			System.out.println("\n--- Izdati račun ---");
			System.out.println("Datum izdavanja: " + new java.sql.Date(System.currentTimeMillis()));
			System.out.println("Kupac: " + kupacImePrezime +"(" + brojTelefona + ")");
			if (vrstaKupovineId == 2) {
				System.out.println("Adresa za dostavu: " + adresaZaDostavu);
			} else {
				System.out.println("Prodavac: " +prodavacImePrezime);
			}
			System.out.println("\nStavke:");
			System.out.println("--------------------------------------------------------------------------------------------");
			System.out.printf("%-35s %15s %15s %20s\n", "Knjiga", "Količina", "Popust (%)", "Cena po komadu");
			System.out.println("--------------------------------------------------------------------------------------------");


			String stavkeQuery = "SELECT knjiga.naslov, stavka.kolicina, stavka.popust, stavka.jedinicna_cena FROM stavka " +
					"INNER JOIN knjiga ON stavka.knjiga_id = knjiga.id WHERE stavka.racun_id = ?";
			PreparedStatement stavkeStmt = conn.prepareStatement(stavkeQuery);
			stavkeStmt.setInt(1, racunId);
			ResultSet stavkeRs = stavkeStmt.executeQuery();

			while (stavkeRs.next()) {
				String naslov = stavkeRs.getString("naslov");
				int kolicina = stavkeRs.getInt("kolicina");
				BigDecimal popust = stavkeRs.getBigDecimal("popust");
				BigDecimal cenaPoKomadu = stavkeRs.getBigDecimal("jedinicna_cena");

				// Printing each row with centered or aligned values
				System.out.printf("%-35s %15d %15.2f %20.2f\n", naslov, kolicina, popust, cenaPoKomadu);
			}
			System.out.println("--------------------------------------------------------------------------------------------");



			System.out.println("Ukupna cena: €" + ukupnaCena);

			updateBrojTransakcija(kupacId, getBrojTransakcija(kupacId));

		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}

	public void prikaziRacuneSaCenomVecomOdProsecne() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Unesite broj telefona kupca za prikaz računa sa cenom većom od prosečne:");
		String brojTelefona = scanner.nextLine().trim();

		// Validacija broja telefona
		if (validanBrojTelefona(brojTelefona)) {
			System.out.println("Broj telefona nije validan!");
			return;
		}

		// Pronalaženje ID-a kupca na osnovu broja telefona
		Integer kupacId = getIdByName("kupac", "broj_telefona", brojTelefona);
		if (kupacId == null) {
			System.out.println("Kupac sa brojem telefona '" + brojTelefona + "' ne postoji.");
			return;
		}

		Connection conn = open();
		if (conn == null) return;

		try {
			String query = "SELECT r.id AS racun_id, r.datum, r.ukupna_cena, vk.naziv AS vrsta_kupovine, " +
					"p.ime AS prodavac_ime, p.prezime AS prodavac_prezime, a.grad AS adresa_grad, " +
					"a.naziv_ulice AS adresa_ulica, a.broj_ulice AS adresa_broj, " +
					"a.postanski_broj AS adresa_postanski_broj " +
					"FROM racun r " +
					"JOIN vrsta_kupovine vk ON r.vrsta_kupovine_id = vk.id " +
					"LEFT JOIN prodavac p ON r.prodavac_id = p.id " +
					"LEFT JOIN adresa a ON r.adresaZaDostavu_id = a.id " +
					"WHERE r.kupac_id = ? " +
					"AND r.ukupna_cena > (SELECT AVG(ukupna_cena) FROM racun WHERE kupac_id = ?) " +
					"ORDER BY r.datum DESC";

			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, kupacId);
			stmt.setInt(2, kupacId);
			ResultSet rs = stmt.executeQuery();

			System.out.println("\nRačuni kupca sa cenom većom od prosečne:");
			System.out.println("---------------------------------------------------------------------------------------------------");
			System.out.printf("%-10s %-15s %-15s %-20s %-20s %-30s\n", "Račun ID", "Datum", "Ukupna cena", "Vrsta kupovine", "Prodavac", "Adresa");
			System.out.println("---------------------------------------------------------------------------------------------------");

			boolean imaRacuna = false;

			while (rs.next()) {
				imaRacuna = true; // Postoji najmanje jedan rezultat

				int racunId = rs.getInt("racun_id");
				Date datum = rs.getDate("datum");
				BigDecimal ukupnaCena = rs.getBigDecimal("ukupna_cena");
				String vrstaKupovine = rs.getString("vrsta_kupovine");
				String prodavacIme = rs.getString("prodavac_ime");
				String prodavacPrezime = rs.getString("prodavac_prezime");
				String adresaGrad = rs.getString("adresa_grad");
				String adresaUlica = rs.getString("adresa_ulica");
				String adresaBroj = rs.getString("adresa_broj");
				String adresaPostanskiBroj = rs.getString("adresa_postanski_broj");

				String prodavac = (prodavacIme != null) ? prodavacIme + " " + prodavacPrezime : "N/A";
				String adresa = (adresaGrad != null) ? adresaUlica + " " + adresaBroj + ", " + adresaGrad + " " + adresaPostanskiBroj : "N/A";

				System.out.printf(
						"%-10d %-15s €%-14.2f %-20s %-20s %-30s\n",
						racunId,
						datum,
						ukupnaCena,
						vrstaKupovine,
						prodavac,
						adresa
				);
			}

			if (!imaRacuna) {
				System.out.println("Nema računa sa cenom većom od prosečne za ovog kupca.");
			}

			System.out.println("---------------------------------------------------------------------------------------------------");


		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}

	public void prikaziRacuneUMesecu() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Unesite broj telefona kupca:");
		String brojTelefona = scanner.nextLine().trim();

		// Validacija broja telefona
		if (validanBrojTelefona(brojTelefona)) {
			System.out.println("Broj telefona nije validan!");
			return;
		}

		Integer kupacId = getIdByName("kupac", "broj_telefona", brojTelefona);
		if (kupacId == null) {
			System.out.println("Kupac sa brojem telefona '" + brojTelefona + "' ne postoji.");
			return;
		}

		System.out.println("Unesite mesec (1-12):");
		int mesec = scanner.nextInt();
		System.out.println("Unesite godinu (pr. 2023):");
		int godina = scanner.nextInt();
		scanner.nextLine();

		Connection conn = open();
		if (conn == null) return;

		try {
			String query = "SELECT r.id AS racun_id, r.datum, r.ukupna_cena, vk.naziv AS vrsta_kupovine, " +
					"p.ime AS prodavac_ime, p.prezime AS prodavac_prezime, a.grad AS adresa_grad, " +
					"a.naziv_ulice AS adresa_ulica, a.broj_ulice AS adresa_broj, " +
					"a.postanski_broj AS adresa_postanski_broj " +
					"FROM racun r " +
					"JOIN vrsta_kupovine vk ON r.vrsta_kupovine_id = vk.id " +
					"LEFT JOIN prodavac p ON r.prodavac_id = p.id " +
					"LEFT JOIN adresa a ON r.adresaZaDostavu_id = a.id " +
					"WHERE r.kupac_id = ? " +
					"AND MONTH(r.datum) = ? " +
					"AND YEAR(r.datum) = ? " +
					"ORDER BY r.datum DESC";

			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, kupacId);
			stmt.setInt(2, mesec);
			stmt.setInt(3, godina);
			ResultSet rs = stmt.executeQuery();

			boolean imaRacuna = false;

			while (rs.next()) {
				imaRacuna = true;

				// Informacije o jednom računu
				int racunId = rs.getInt("racun_id");
				Date datum = rs.getDate("datum");
				String vrstaKupovine = rs.getString("vrsta_kupovine");
				String prodavacIme = rs.getString("prodavac_ime");
				String prodavacPrezime = rs.getString("prodavac_prezime");
				String adresaGrad = rs.getString("adresa_grad");
				String adresaUlica = rs.getString("adresa_ulica");
				String adresaBroj = rs.getString("adresa_broj");
				String adresaPostanskiBroj = rs.getString("adresa_postanski_broj");

				String prodavac = (prodavacIme != null) ? prodavacIme + " " + prodavacPrezime : "N/A";
				String adresa = (adresaGrad != null)
						? adresaUlica + " " + adresaBroj + ", " + adresaGrad + ", " + adresaPostanskiBroj
						: "N/A";

				// Račun header
				System.out.println("\n--- Izdati račun ---");
				System.out.printf("Račun ID: %-47d\n", racunId);
				System.out.printf("Datum: %-50s\n", datum);
				System.out.printf("Vrsta kupovine: %-42s\n", vrstaKupovine);
				if (vrstaKupovine.equalsIgnoreCase("Online")) {
					System.out.printf("Adresa dostave: %-40s\n", adresa);
				} else {
					System.out.printf("Prodavac: %-46s\n", prodavac);
				}

				// Dobavljanje stavki za trenutni račun
				String stavkeQuery = "SELECT knjiga.naslov, stavka.kolicina, stavka.popust, stavka.jedinicna_cena " +
						"FROM stavka " +
						"INNER JOIN knjiga ON stavka.knjiga_id = knjiga.id " +
						"WHERE stavka.racun_id = ?";
				PreparedStatement stavkeStmt = conn.prepareStatement(stavkeQuery);
				stavkeStmt.setInt(1, racunId);
				ResultSet stavkeRs = stavkeStmt.executeQuery();

				// Formatirani prikaz stavki
				System.out.println("Stavke:");
				System.out.println("--------------------------------------------------------------------------------------------");
				System.out.printf("%-40s %10s %18s %20s\n", "Knjiga", "Količina", "Popust (%)", "Cena po komadu");
				System.out.println("--------------------------------------------------------------------------------------------");

				BigDecimal ukupnaCenaStavki = BigDecimal.ZERO;

				while (stavkeRs.next()) {
					String naslov = stavkeRs.getString("naslov");
					int kolicina = stavkeRs.getInt("kolicina");
					BigDecimal popust = stavkeRs.getBigDecimal("popust");
					BigDecimal cenaPoKomadu = stavkeRs.getBigDecimal("jedinicna_cena");

					// Ispis jedne stavke
					System.out.printf("%-40s %10d %18.2f %20.2f\n", naslov, kolicina, popust, cenaPoKomadu);

					// Ukupna cena kroz stavke
					ukupnaCenaStavki = ukupnaCenaStavki.add(cenaPoKomadu.multiply(new BigDecimal(kolicina)));
				}

				System.out.println("--------------------------------------------------------------------------------------------");
				System.out.printf("Ukupna cena: €%.2f\n", ukupnaCenaStavki);
				System.out.println("--------------------------------------------------------------------------------------------");
			}

			if (!imaRacuna) {
				System.out.println("----------------------------------------------------");
				System.out.println("Nema računa za ovog kupca u mesecu " + mesec + "/" + godina + ".");
				System.out.println("----------------------------------------------------");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}
	public void prikaziRacunePoMesecima() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Unesite broj telefona kupca:");
		String brojTelefona = scanner.nextLine().trim();

		// Validacija broja telefona
		if (validanBrojTelefona(brojTelefona)) {
			System.out.println("Broj telefona nije validan!");
			return;
		}

		// Pronalaženje ID-a kupca na osnovu broja telefona
		Integer kupacId = getIdByName("kupac", "broj_telefona", brojTelefona);
		if (kupacId == null) {
			System.out.println("Kupac sa brojem telefona '" + brojTelefona + "' ne postoji.");
			return;
		}

		Connection conn = open();
		if (conn == null) return;

		try {
			// SQL upit za prikaz broja računa po mesecima
			String query = "SELECT DATE_FORMAT(r.datum, '%Y-%m') AS mesec, COUNT(r.id) AS broj_racuna " +
					"FROM racun r " +
					"WHERE r.kupac_id = ? " +
					"GROUP BY DATE_FORMAT(r.datum, '%Y-%m') " +
					"HAVING broj_racuna > 1"; // Prikazuje samo mesece sa više od 1 računa

			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, kupacId);
			ResultSet rs = stmt.executeQuery();

			System.out.println("\nRačuni po mesecima (sa više od 1 računa):");
			System.out.println("----------------------------------------------------");
			System.out.println("Mesec\t\tBroj računa");
			System.out.println("----------------------------------------------------");

			boolean imaRacuna = false;
			while (rs.next()) {
				imaRacuna = true; // Postoji barem jedan mesec sa računima

				String mesec = rs.getString("mesec");
				int brojRacuna = rs.getInt("broj_racuna");

				System.out.println(mesec + "\t\t" + brojRacuna);
			}

			if (!imaRacuna) {
				System.out.println("Nema meseci sa više od 1 računa za ovog kupca.");
			}

			System.out.println("----------------------------------------------------");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}

	public void obrisiNeaktivneVipKartice() {
		Connection conn = open();
		if (conn == null) return;

		try {
			// SQL upit za brisanje neaktivnih VIP kartica
			String deleteQuery = "DELETE FROM vip_kartica WHERE jeAktivna = false";

			// Priprema i izvršavanje upita
			try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
				int brojObrisanih = stmt.executeUpdate(); // Broj obrisanih redova

				if (brojObrisanih > 0) {
					System.out.println("Uspešno obrisano " + brojObrisanih + " neaktivnih VIP kartica.");
				} else {
					System.out.println("Nema neaktivnih VIP kartica za brisanje.");
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}

	public void ukloniKnjigu() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Unesite ISBN knjige koju zelite ukloniti:");
		String isbn = scanner.nextLine().trim();
		Integer knjigaId = getIdByName("knjiga", "isbn", isbn);
		Connection conn = open();
		if (conn == null) return;

		try {
			conn.setAutoCommit(false);


			//  Brisanje povezanih autora iz tabele `autor_knjiga`
			String deleteAutorKnjigaQuery = "DELETE FROM autor_knjiga WHERE knjiga_id = ?";
			try (PreparedStatement autorKnjigaStmt = conn.prepareStatement(deleteAutorKnjigaQuery)) {
				autorKnjigaStmt.setInt(1, knjigaId);
				autorKnjigaStmt.executeUpdate();
			}

			// Brisanje knjige iz tabele `knjiga`
			String deleteKnjigaQuery = "DELETE FROM knjiga WHERE id = ?";
			try (PreparedStatement knjigaStmt = conn.prepareStatement(deleteKnjigaQuery)) {
				knjigaStmt.setInt(1, knjigaId);
				int brojObrisanih = knjigaStmt.executeUpdate();

				if (brojObrisanih > 0) {
					System.out.println("Knjiga sa ID " + knjigaId + " je uspešno obrisana.");
				} else {
					System.out.println("Knjiga sa ID " + knjigaId + " nije pronađena.");
				}
			}

			conn.commit();

		} catch (SQLException e) {
			try {
				conn.rollback(); // Vrati transakciju u slučaju greške
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true); // Vrati auto-commit na default
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}
	}

	public void importKnjigaIzFajla(Scanner scanner	) {
		System.out.println("Unesite naziv fajla sa kojim se importuju knjige:");
		String putanjaDoFajla = scanner.nextLine().trim();
        Connection conn = open();
        if (conn == null) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(putanjaDoFajla));
            String linija;

            // Preskoči header
            reader.readLine();

            String insertKnjigaQuery = "INSERT INTO knjiga (ISBN, naslov, broj_stranica, cena, distributer_id, izdavac_id, kategorija_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement knjigaStmt = conn.prepareStatement(insertKnjigaQuery, Statement.RETURN_GENERATED_KEYS);

            String insertAutorKnjigaQuery = "INSERT INTO autor_knjiga (autor_id, knjiga_id) VALUES (?, ?)";
            PreparedStatement autorKnjigaStmt = conn.prepareStatement(insertAutorKnjigaQuery);

            while ((linija = reader.readLine()) != null) {
                String[] podaci = linija.split(",");

                if (podaci.length != 8) {
                    System.out.println("Nevažeći format linije: " + linija);
                    continue;
                }

                // Parsiranje podataka iz CSV-a
                String isbn = podaci[0].trim();
                String naslov = podaci[1].trim();
                int brojStranica = Integer.parseInt(podaci[2].trim());
                BigDecimal cena = new BigDecimal(podaci[3].trim());
                String distributerNaziv = podaci[4].trim();
                String izdavacNaziv = podaci[5].trim();
                String kategorijaNaziv = podaci[6].trim();
                String autoriString = podaci[7].trim();

                Integer distributerId = getIdByName("distributer", "ime", distributerNaziv);
                Integer izdavacId = getIdByName("izdavac", "ime", izdavacNaziv);
                Integer kategorijaId = getIdByName("kategorija", "naziv", kategorijaNaziv);

                if (distributerId == null || izdavacId == null || kategorijaId == null) {
                    System.out.println("Nije moguće pronaći ID za distributera, izdavača ili kategoriju u liniji: " + linija);
                    continue;
                }

                // Unos knjige
                knjigaStmt.setString(1, isbn);
                knjigaStmt.setString(2, naslov);
                knjigaStmt.setInt(3, brojStranica);
                knjigaStmt.setBigDecimal(4, cena);
                knjigaStmt.setInt(5, distributerId);
                knjigaStmt.setInt(6, izdavacId);
                knjigaStmt.setInt(7, kategorijaId);
                knjigaStmt.executeUpdate();

                ResultSet rs = knjigaStmt.getGeneratedKeys();
                int knjigaId = -1;
                if (rs.next()) {
                    knjigaId = rs.getInt(1);
                }

                // Ako knjiga nije uspešno dodata, preskoči dodavanje autora
                if (knjigaId == -1) {
                    System.out.println("Greška pri unosu knjige: " + naslov);
                    continue;
                }

                // Parsiranje i dodavanje više autora
                String[] autori = autoriString.split(";");
                for (String autor : autori) {
                    autor = autor.trim().replace("\"", ""); // Ukloni nepotrebne navodnike

                    // Proveri da li je format validan
                    String[] imePrezime = autor.split(" ");
                    if (imePrezime.length < 2) {
                        System.out.println("Nevažeći format imena autora: " + autor);
                        continue;
                    }

                    // Spajanje prvog imena i prezimena ako autor ima više od dva dela u imenu
                    String ime = imePrezime[0].trim();
                    String prezime = String.join(" ", Arrays.copyOfRange(imePrezime, 1, imePrezime.length)).trim();

                    Integer autorId = getAutorIdByImePrezime(ime, prezime);
                    if (autorId == null) {
                        System.out.println("Autor '" + autor + "' nije pronađen u bazi.");
                        continue;
                    }

                    // Dodavanje autora u autor_knjiga tabelu
                    autorKnjigaStmt.setInt(1, autorId);
                    autorKnjigaStmt.setInt(2, knjigaId);
                    autorKnjigaStmt.executeUpdate();
                }
            }

            System.out.println("Knjige su uspešno uvezene iz fajla.");

            reader.close();
            knjigaStmt.close();
            autorKnjigaStmt.close();

        } catch (FileNotFoundException e) {
            System.out.println("Fajl nije pronađen: " + putanjaDoFajla);
        } catch (IOException e) {
            System.out.println("Greška pri čitanju fajla: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Greška pri unosu podataka u bazu: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Nevažeći format broja u fajlu: " + e.getMessage());
        } finally {
            close(conn);
        }
	}

    public void obrisiKnjigeIzFajla() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Unesite naziv fajla za brisanje knjiga:");
        String putanjaDoFajla = scanner.nextLine();
        Connection conn = open();
        if (conn == null) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(putanjaDoFajla));
            String linija;

            String deleteAutorKnjigaQuery = "DELETE FROM autor_knjiga WHERE knjiga_id IN (SELECT id FROM knjiga WHERE ISBN = ?)";
            PreparedStatement autorKnjigaStmt = conn.prepareStatement(deleteAutorKnjigaQuery);

            String deleteKnjigaQuery = "DELETE FROM knjiga WHERE ISBN = ?";
            PreparedStatement knjigaStmt = conn.prepareStatement(deleteKnjigaQuery);

            while ((linija = reader.readLine()) != null) {
                String isbn = linija.trim();

                if (isbn.isEmpty()) {
                    System.out.println("Preskočena prazna linija.");
                    continue;
                }

                if (!knjigaPostoji(isbn)) {
                    System.out.println("Knjiga sa ISBN " + isbn + " ne postoji u bazi.");
                    continue;
                }

                // Prvo se briše iz tabele autor_knjiga
                autorKnjigaStmt.setString(1, isbn);
                autorKnjigaStmt.executeUpdate();

                // Zatim se brise knjiga
                knjigaStmt.setString(1, isbn);
                int rowsDeleted = knjigaStmt.executeUpdate();

                if (rowsDeleted > 0) {
                    System.out.println("Knjiga sa ISBN " + isbn + " uspešno obrisana.");
                } else {
                    System.out.println("Greška pri brisanju knjige sa ISBN " + isbn);
                }
            }

            System.out.println("Brisanje knjiga iz fajla završeno.");

            reader.close();
            autorKnjigaStmt.close();
            knjigaStmt.close();

        } catch (FileNotFoundException e) {
            System.out.println("Fajl nije pronađen: " + putanjaDoFajla);
        } catch (IOException e) {
            System.out.println("Greška pri čitanju fajla: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Greška pri brisanju podataka iz baze: " + e.getMessage());
        } finally {
            close(conn);
        }
    }

	public void obrisiIzdavaca() {
		Connection conn = open();
		if (conn == null) return;

		Scanner scanner = new Scanner(System.in);
		System.out.print("Unesite ime izdavača za brisanje: ");
		String izdavacIme = scanner.nextLine().trim();

		try {
			// Provera da li izdavač postoji i dobijanje ID-a
			Integer izdavacId = getIdByName("izdavac", "ime", izdavacIme);
			if (izdavacId == null) {
				System.out.println("Izdavač '" + izdavacIme + "' ne postoji u bazi.");
				return;
			}

			// Brisanje knjiga povezanih sa izdavačem
			String deleteAutorKnjigaQuery = "DELETE FROM autor_knjiga WHERE knjiga_id IN (SELECT id FROM knjiga WHERE izdavac_id = ?)";
			PreparedStatement autorKnjigaStmt = conn.prepareStatement(deleteAutorKnjigaQuery);
			autorKnjigaStmt.setInt(1, izdavacId);
			autorKnjigaStmt.executeUpdate();

			String deleteKnjigaQuery = "DELETE FROM knjiga WHERE izdavac_id = ?";
			PreparedStatement knjigaStmt = conn.prepareStatement(deleteKnjigaQuery);
			knjigaStmt.setInt(1, izdavacId);
			knjigaStmt.executeUpdate();

			// Brisanje veze izdavača iz distributer_izdavac
			String deleteDistributerIzdavacQuery = "DELETE FROM distributer_izdavac WHERE izdavac_id = ?";
			PreparedStatement distributerIzdavacStmt = conn.prepareStatement(deleteDistributerIzdavacQuery);
			distributerIzdavacStmt.setInt(1, izdavacId);
			distributerIzdavacStmt.executeUpdate();

			// Brisanje izdavača
			String deleteIzdavacQuery = "DELETE FROM izdavac WHERE id = ?";
			PreparedStatement izdavacStmt = conn.prepareStatement(deleteIzdavacQuery);
			izdavacStmt.setInt(1, izdavacId);
			int rowsDeleted = izdavacStmt.executeUpdate();

			if (rowsDeleted > 0) {
				System.out.println("Izdavač '" + izdavacIme + "' uspešno obrisan iz baze.");
			} else {
				System.out.println("Došlo je do greške pri brisanju izdavača.");
			}
			autorKnjigaStmt.close();
			knjigaStmt.close();
			distributerIzdavacStmt.close();
			izdavacStmt.close();

		} catch (SQLException e) {
			System.out.println("Greška pri brisanju izdavača: " + e.getMessage());
		} finally {
			close(conn);
		}
	}




    //-------------------------- Pomoćne metode

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
	public boolean kreirajKarticuZaKupca(int kupacId) {
		Scanner scanner = new Scanner(System.in);
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
	public boolean imaAktivnuKarticu(int kupacId) {
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

	public static boolean validanBrojTelefona(String brojTelefona) {

        return !brojTelefona.matches("^[+\\d\\s()-]{6,}$");
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

	private static void prepareStatementKnjiga(String ISBN, String naslov, int brojStranica, double cena, int distributerId, int izdavacId, int kategorijaId, PreparedStatement ps) throws SQLException {
		ps.setString(1, ISBN);
		ps.setString(2, naslov);
		ps.setInt(3, brojStranica);
		ps.setDouble(4, cena);
		ps.setInt(5, distributerId);
		ps.setInt(6, izdavacId);
		ps.setInt(7, kategorijaId);
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

	public boolean deaktivirajKarticu(int kupacId) {
		// SQL upit za deaktivaciju VIP kartice
		String sql = "UPDATE vip_kartica SET jeAktivna = false WHERE kupac_id = ?";

		// Otvaranje konekcije i rad sa bazom
		try (Connection conn = open()) {
			if (conn == null) return false;

			// Izvršavanje upita za deaktivaciju kartice
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, kupacId);

				int rowsAffected = ps.executeUpdate();
				if (rowsAffected > 0) {
					System.out.println("VIP kartica je uspešno deaktivirana.");
					return true;
				} else {
					System.out.println("Nije pronađena aktivna VIP kartica za ovog kupca.");
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Integer getKupacByBroj(String brojTelefona){
		Connection conn = open();
		if (conn == null) return null;

		String sql = "SELECT id FROM  kupac  WHERE broj_telefona = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, brojTelefona);
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

	public String getImePrezimeById(String table, String idColumnName, int id) {
		Connection conn = open();
		if (conn == null) return null;

		String sql = "SELECT ime, prezime FROM " + table + " WHERE " + idColumnName + " = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String imePrezime = rs.getString("ime") + " " + rs.getString("prezime");
				close(conn);
				return imePrezime;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close(conn);
		return null; // Ako ne pronađe ime i prezime, vrati null
	}



}
	

	
	






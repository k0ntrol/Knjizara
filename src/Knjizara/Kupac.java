package Knjizara;

public class Kupac {

	private Integer id;
    private String ime;
    private String prezime;
    private String brojTelefona;
    private Integer brojTransakcija;
    private Integer vrstaKupcaId;

    public Kupac() {}

	public Kupac(Integer id, String ime, String prezime, String brojTelefona, Integer brojTransakcija,
			Integer vrstaKupcaId) {
		super();
		this.id = id;
		this.ime = ime;
		this.prezime = prezime;
		this.brojTelefona = brojTelefona;
		this.brojTransakcija = brojTransakcija;
		this.vrstaKupcaId = vrstaKupcaId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public String getBrojTelefona() {
		return brojTelefona;
	}

	public void setBrojTelefona(String brojTelefona) {
		this.brojTelefona = brojTelefona;
	}

	public Integer getBrojTransakcija() {
		return brojTransakcija;
	}

	public void setBrojTransakcija(Integer brojTransakcija) {
		this.brojTransakcija = brojTransakcija;
	}

	public Integer getVrstaKupcaId() {
		return vrstaKupcaId;
	}

	public void setVrstaKupcaId(Integer vrstaKupcaId) {
		this.vrstaKupcaId = vrstaKupcaId;
	}
    
    
}

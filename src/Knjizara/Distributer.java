package Knjizara;

public class Distributer {

	private Integer id;
    private String ime;
    private String brojTelefona;
    private Integer adresaId;

    public Distributer() {}

    public Distributer(Integer id, String ime, String brojTelefona, Integer adresaId) {
        this.id = id;
        this.ime = ime;
        this.brojTelefona = brojTelefona;
        this.adresaId = adresaId;
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

	public String getBrojTelefona() {
		return brojTelefona;
	}

	public void setBrojTelefona(String brojTelefona) {
		this.brojTelefona = brojTelefona;
	}

	public Integer getAdresaId() {
		return adresaId;
	}

	public void setAdresaId(Integer adresaId) {
		this.adresaId = adresaId;
	}
    
}

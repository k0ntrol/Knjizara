package Knjizara;

public class Izdavac {
    private Integer id;
    private String ime;
    private Integer godinaOsnivanja;
    private Integer adresaId;

    public Izdavac() {}

    public Izdavac(Integer id, String ime, Integer godinaOsnivanja, Integer adresaId) {
        this.id = id;
        this.ime = ime;
        this.godinaOsnivanja = godinaOsnivanja;
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

	public Integer getGodinaOsnivanja() {
		return godinaOsnivanja;
	}

	public void setGodinaOsnivanja(Integer godinaOsnivanja) {
		this.godinaOsnivanja = godinaOsnivanja;
	}

	public Integer getAdresaId() {
		return adresaId;
	}

	public void setAdresaId(Integer adresaId) {
		this.adresaId = adresaId;
	}

    
}

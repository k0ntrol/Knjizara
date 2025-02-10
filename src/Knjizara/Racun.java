package Knjizara;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Racun {

	private Integer id;
    private LocalDateTime datum;
    private BigDecimal ukupnaCena;
    private Integer vrstaKupovineId;
    private Integer prodavacId;
    private Integer kupacId;
    private Integer adresaZaDostavuId;

    public Racun() {}

	public Racun(Integer id, LocalDateTime datum, BigDecimal ukupnaCena, Integer vrstaKupovineId, Integer prodavacId,
			Integer kupacId, Integer adresaZaDostavuId) {
		super();
		this.id = id;
		this.datum = datum;
		this.ukupnaCena = ukupnaCena;
		this.vrstaKupovineId = vrstaKupovineId;
		this.prodavacId = prodavacId;
		this.kupacId = kupacId;
		this.adresaZaDostavuId = adresaZaDostavuId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getDatum() {
		return datum;
	}

	public void setDatum(LocalDateTime datum) {
		this.datum = datum;
	}

	public BigDecimal getUkupnaCena() {
		return ukupnaCena;
	}

	public void setUkupnaCena(BigDecimal ukupnaCena) {
		this.ukupnaCena = ukupnaCena;
	}

	public Integer getVrstaKupovineId() {
		return vrstaKupovineId;
	}

	public void setVrstaKupovineId(Integer vrstaKupovineId) {
		this.vrstaKupovineId = vrstaKupovineId;
	}

	public Integer getProdavacId() {
		return prodavacId;
	}

	public void setProdavacId(Integer prodavacId) {
		this.prodavacId = prodavacId;
	}

	public Integer getKupacId() {
		return kupacId;
	}

	public void setKupacId(Integer kupacId) {
		this.kupacId = kupacId;
	}

	public Integer getAdresaZaDostavuId() {
		return adresaZaDostavuId;
	}

	public void setAdresaZaDostavuId(Integer adresaZaDostavuId) {
		this.adresaZaDostavuId = adresaZaDostavuId;
	}
    
    
}

package Knjizara;

import java.math.BigDecimal;

public class Stavka {

	private Integer id;
    private Integer kolicina;
    private BigDecimal popust;
    private BigDecimal jedinicnaCena;
    private Integer racunId;
    private Integer knjigaId;

    public Stavka() {}

	public Stavka(Integer id, Integer kolicina, BigDecimal popust, BigDecimal jedinicnaCena, Integer racunId,
			Integer knjigaId) {
		super();
		this.id = id;
		this.kolicina = kolicina;
		this.popust = popust;
		this.jedinicnaCena = jedinicnaCena;
		this.racunId = racunId;
		this.knjigaId = knjigaId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getKolicina() {
		return kolicina;
	}

	public void setKolicina(Integer kolicina) {
		this.kolicina = kolicina;
	}

	public BigDecimal getPopust() {
		return popust;
	}

	public void setPopust(BigDecimal popust) {
		this.popust = popust;
	}

	public BigDecimal getJedinicnaCena() {
		return jedinicnaCena;
	}

	public void setJedinicnaCena(BigDecimal jedinicnaCena) {
		this.jedinicnaCena = jedinicnaCena;
	}

	public Integer getRacunId() {
		return racunId;
	}

	public void setRacunId(Integer racunId) {
		this.racunId = racunId;
	}

	public Integer getKnjigaId() {
		return knjigaId;
	}

	public void setKnjigaId(Integer knjigaId) {
		this.knjigaId = knjigaId;
	}
    
    
}

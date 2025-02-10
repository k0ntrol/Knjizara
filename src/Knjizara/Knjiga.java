package Knjizara;
import java.math.*;

public class Knjiga {
	private Integer id;
    private String ISBN;
    private String naslov;
    private Integer brojStranica;
    private BigDecimal cena;
    private Integer distributerId;
    private Integer izdavacId;
    private Integer kategorijaId;

    public Knjiga() {}

	public Knjiga(Integer id, String iSBN, String naslov, Integer brojStranica, BigDecimal cena, Integer distributerId,
			Integer izdavacId, Integer kategorijaId) {
		super();
		this.id = id;
		ISBN = iSBN;
		this.naslov = naslov;
		this.brojStranica = brojStranica;
		this.cena = cena;
		this.distributerId = distributerId;
		this.izdavacId = izdavacId;
		this.kategorijaId = kategorijaId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public String getNaslov() {
		return naslov;
	}

	public void setNaslov(String naslov) {
		this.naslov = naslov;
	}

	public Integer getBrojStranica() {
		return brojStranica;
	}

	public void setBrojStranica(Integer brojStranica) {
		this.brojStranica = brojStranica;
	}

	public BigDecimal getCena() {
		return cena;
	}

	public void setCena(BigDecimal cena) {
		this.cena = cena;
	}

	public Integer getDistributerId() {
		return distributerId;
	}

	public void setDistributerId(Integer distributerId) {
		this.distributerId = distributerId;
	}

	public Integer getIzdavacId() {
		return izdavacId;
	}

	public void setIzdavacId(Integer izdavacId) {
		this.izdavacId = izdavacId;
	}

	public Integer getKategorijaId() {
		return kategorijaId;
	}

	public void setKategorijaId(Integer kategorijaId) {
		this.kategorijaId = kategorijaId;
	}
    
    
	    
	    
}

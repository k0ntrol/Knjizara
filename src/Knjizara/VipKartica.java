package Knjizara;
import java.time.LocalDate;
import java.math.BigDecimal;

public class VipKartica {

	 private Integer id;
	    private LocalDate datumIzdavanja;
	    private BigDecimal popust;
	    private Boolean jeAktivna;
	    private Integer kupacId;

	    public VipKartica() {}

		public VipKartica(Integer id, LocalDate datumIzdavanja, BigDecimal popust, Boolean jeAktivna, Integer kupacId) {
			super();
			this.id = id;
			this.datumIzdavanja = datumIzdavanja;
			this.popust = popust;
			this.jeAktivna = jeAktivna;
			this.kupacId = kupacId;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public LocalDate getDatumIzdavanja() {
			return datumIzdavanja;
		}

		public void setDatumIzdavanja(LocalDate datumIzdavanja) {
			this.datumIzdavanja = datumIzdavanja;
		}

		public BigDecimal getPopust() {
			return popust;
		}

		public void setPopust(BigDecimal popust) {
			this.popust = popust;
		}

		public Boolean getJeAktivna() {
			return jeAktivna;
		}

		public void setJeAktivna(Boolean jeAktivna) {
			this.jeAktivna = jeAktivna;
		}

		public Integer getKupacId() {
			return kupacId;
		}

		public void setKupacId(Integer kupacId) {
			this.kupacId = kupacId;
		}
	    
	    
    
    
}

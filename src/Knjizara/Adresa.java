package Knjizara;


public class Adresa {
    private Integer id;
    private String grad;
    private String brojUlice;
    private String nazivUlice;
    private String postanskiBroj;
    private Integer drzavaId;

    public Adresa() {}

    public Adresa(Integer id, String grad, String brojUlice, String nazivUlice, 
                 String postanskiBroj, Integer drzavaId) {
        this.id = id;
        this.grad = grad;
        this.brojUlice = brojUlice;
        this.nazivUlice = nazivUlice;
        this.postanskiBroj = postanskiBroj;
        this.drzavaId = drzavaId;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGrad() {
		return grad;
	}

	public void setGrad(String grad) {
		this.grad = grad;
	}

	public String getBrojUlice() {
		return brojUlice;
	}

	public void setBrojUlice(String brojUlice) {
		this.brojUlice = brojUlice;
	}

	public String getNazivUlice() {
		return nazivUlice;
	}

	public void setNazivUlice(String nazivUlice) {
		this.nazivUlice = nazivUlice;
	}

	public String getPostanskiBroj() {
		return postanskiBroj;
	}

	public void setPostanskiBroj(String postanskiBroj) {
		this.postanskiBroj = postanskiBroj;
	}

	public Integer getDrzavaId() {
		return drzavaId;
	}

	public void setDrzavaId(Integer drzavaId) {
		this.drzavaId = drzavaId;
	}
    
    
}

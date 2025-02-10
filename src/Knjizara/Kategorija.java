package Knjizara;

public class Kategorija {

	private Integer id;
    private String naziv;
    private Integer roditeljId;

    public Kategorija() {}

    public Kategorija(Integer id, String naziv, Integer roditeljId) {
        this.id = id;
        this.naziv = naziv;
        this.roditeljId = roditeljId;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public Integer getRoditeljId() {
		return roditeljId;
	}

	public void setRoditeljId(Integer roditeljId) {
		this.roditeljId = roditeljId;
	}

    
}

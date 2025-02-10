package Knjizara;

public class AutorKnjiga {

	 	private Integer autorId;
	    private Integer knjigaId;

	    public AutorKnjiga() {}

	    public AutorKnjiga(Integer autorId, Integer knjigaId) {
	        this.autorId = autorId;
	        this.knjigaId = knjigaId;
	    }

		public Integer getAutorId() {
			return autorId;
		}

		public void setAutorId(Integer autorId) {
			this.autorId = autorId;
		}

		public Integer getKnjigaId() {
			return knjigaId;
		}

		public void setKnjigaId(Integer knjigaId) {
			this.knjigaId = knjigaId;
		}
	    
	    
}

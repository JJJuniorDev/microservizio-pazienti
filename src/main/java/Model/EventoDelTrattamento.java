package Model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "EVENTO_DEL_TRATTAMENTO")
public class EventoDelTrattamento {

	 @Id
	    private ObjectId id;

	    @Field(name = "PIANO_TRATTAMENTO_ID")
	    private ObjectId pianoTrattamentoId; // ID del piano a cui appartiene

	    @Field(name = "DESCRIZIONE")
	    private String descrizione;

	    @Field(name = "DATA_SCADE")
	    private String dataScade; // Data in cui la milestone deve essere raggiunta

	    @Field(name = "COMPLETATA")
	    private boolean completata; // Stato della milestone

	    @Field(name = "TIPOLOGIA")
	    private String tipologia;
	    
		public ObjectId getId() {
			return id;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}

		public ObjectId getPianoTrattamentoId() {
			return pianoTrattamentoId;
		}

		public void setPianoTrattamentoId(ObjectId pianoTrattamentoId) {
			this.pianoTrattamentoId = pianoTrattamentoId;
		}

		public String getDescrizione() {
			return descrizione;
		}

		public void setDescrizione(String descrizione) {
			this.descrizione = descrizione;
		}

		public String getDataScade() {
			return dataScade;
		}

		public void setDataScade(String dataScade) {
			this.dataScade = dataScade;
		}

		public boolean isCompletata() {
			return completata;
		}

		public void setCompletata(boolean completata) {
			this.completata = completata;
		}

		public String getTipologia() {
			return tipologia;
		}

		public void setTipologia(String tipologia) {
			this.tipologia = tipologia;
		}
	 
	    
}

package DTO;

public class EventoDelTrattamentoDTO {
private String id;
private String pianoTrattamentoId;
private String descrizione;
private String dataScade;
private boolean completata;
private String tipologia;

public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getPianoTrattamentoId() {
	return pianoTrattamentoId;
}
public void setPianoTrattamentoId(String pianoTrattamentoId) {
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

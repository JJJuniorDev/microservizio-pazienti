package DTO;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;

public class AppuntamentoDTO {
private String id;
private LocalDateTime dataEOrario;
private String codiceFiscale;
private String trattamento;
private String note;
private ObjectId pazienteId;
private ObjectId dottoreId;
private String stato;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public LocalDateTime getDataEOrario() {
	return dataEOrario;
}
public void setDataEOrario(LocalDateTime dataEOrario) {
	this.dataEOrario = dataEOrario;
}
public String getCodiceFiscale() {
	return codiceFiscale;
}
public void setCodiceFiscale(String codiceFiscale) {
	this.codiceFiscale = codiceFiscale;
}
public String getTrattamento() {
	return trattamento;
}
public void setTrattamento(String trattamento) {
	this.trattamento = trattamento;
}
public String getNote() {
	return note;
}
public void setNote(String note) {
	this.note = note;
}

public ObjectId getPazienteId() {
	return pazienteId;
}
public void setPazienteId(ObjectId pazienteId) {
	this.pazienteId = pazienteId;
}
public ObjectId getDottoreId() {
	return dottoreId;
}
public void setDottoreId(ObjectId dottoreId) {
	this.dottoreId = dottoreId;
}
public String getStato() {
	return stato;
}
public void setStato(String stato) {
	this.stato = stato;
}


}

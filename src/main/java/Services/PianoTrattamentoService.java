package Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.security.auth.login.AccountNotFoundException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import DTO.AppuntamentoDTO;
import DTO.EventoDelTrattamentoDTO;
import DTO.PianoTrattamentoDTO;
import Helpers.IdHelper;
import Model.EventoDelTrattamento;
import Model.PianoTrattamento;
import Repository.EventoDelTrattamentoRepository;
import Repository.PianoTrattamentoRepository;

@Service
public class PianoTrattamentoService {

	 @Value("${appuntamento.service.url}")
	    private String appuntamentoServiceUrl;
	
	@Autowired
	private PianoTrattamentoRepository pianoTrattamentoRepository;

	@Autowired
	private EventoDelTrattamentoRepository eventoTrattamentoRepository;
	
	@Autowired
	private IdHelper idHelper;
	
	private final RestTemplate restTemplate;
	
	public PianoTrattamentoService (PianoTrattamentoRepository pianoTrattamentoRepository,
			IdHelper idHelper, RestTemplate restTemplate) {
		this.pianoTrattamentoRepository=pianoTrattamentoRepository;
		this.idHelper= idHelper;
		this.restTemplate=restTemplate;
	}
	
	public PianoTrattamentoDTO creaPiano(String pazienteId, PianoTrattamentoDTO pianoDTO) {
		  // Convertire PianoTrattamentoDTO in PianoTrattamento
	    PianoTrattamento piano = new PianoTrattamento();
	    piano.setPazienteId(idHelper.stringToObjectId(pazienteId));
	    piano.setNomePiano(pianoDTO.getNomePiano());
	    piano.setAttivo(pianoDTO.isAttivo());
	    piano.setDataInizio(pianoDTO.getDataInizio());
	    piano.setDataFine(pianoDTO.getDataFine());
        piano.setEventiTrattamentoIds(new ArrayList<>());
	    // Salva il piano
	    piano = pianoTrattamentoRepository.save(piano);

	    
	    for (EventoDelTrattamentoDTO eventoDTO : pianoDTO.getEventi()) {
	        EventoDelTrattamento evento = new EventoDelTrattamento();
	        evento.setPianoTrattamentoId(piano.getId());
	        evento.setDescrizione(eventoDTO.getDescrizione());
	        evento.setDataScade(eventoDTO.getDataScade());
	        evento.setCompletata(eventoDTO.isCompletata());
	    

	      
	        EventoDelTrattamento eventoSalvato = eventoTrattamentoRepository.save(evento);
	        piano.getEventiTrattamentoIds().add(eventoSalvato.getId().toHexString());
	    }

	    // Salva il piano aggiornato
	    piano = pianoTrattamentoRepository.save(piano);

	    // Convertire PianoTrattamento in PianoTrattamentoDTO
	    return convertToDTO(piano);
	}
	
	public List<PianoTrattamentoDTO> getPianiTrattamentoByPazienteId(String pazienteId) {
	    ObjectId objectId = idHelper.stringToObjectId(pazienteId); // Converte String in ObjectId  

	    // 1-> PRENDO TUTTI I PIANI
	    return pianoTrattamentoRepository.findByPazienteId(objectId).stream()
	        .map(piano -> {
	        	//1.1 CONVERTO PIANO IN DTO
	            PianoTrattamentoDTO dto = convertToDTO(piano);
                System.out.println("PIANO TROVATO: "+piano.getNomePiano());
             // 1.2 GESTIONE EVENTI
	            List<EventoDelTrattamentoDTO> eventi = piano.getEventiTrattamentoIds().stream()
		                .map(eventoId -> {
		                
		                  
		                    EventoDelTrattamento evento = eventoTrattamentoRepository.findById(idHelper.stringToObjectId(eventoId))
		                        .orElseThrow(() -> new IllegalArgumentException("evento non trovata per ID: " + eventoId));
		               	 System.out.println("evento TROVATA CON ID: "+evento.getId());
		                 
		               	 EventoDelTrattamentoDTO eventoDTO = convertEventoToDTO(evento);
		                 
	           
	           
	            return eventoDTO;
	            })
		        .collect(Collectors.toList());
	            dto.setEventi(eventi);
	            
	            // 1.3 GESTIONE APPUNTAMENTI
	            if (piano.getAppuntamentiIds() != null && !piano.getAppuntamentiIds().isEmpty()) {
	                List<AppuntamentoDTO> appuntamenti = fetchAppuntamentiFromMicroservice(piano.getAppuntamentiIds());
	                System.out.println("Appuntamenti trovati per il piano: " + appuntamenti.size());
	                dto.setAppuntamenti(appuntamenti);
	            }

	            return dto;
	        })
	        .collect(Collectors.toList());
	}
	
	private List<AppuntamentoDTO> fetchAppuntamentiFromMicroservice(List<String> appuntamentiIds) {
	    String url = appuntamentoServiceUrl+"/api/appuntamenti/by-ids"; // Endpoint remoto del microservizio

	    try {
	        // Preparo la richiesta con RestTemplate
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        HttpEntity<List<String>> requestEntity = new HttpEntity<>(appuntamentiIds, headers);

	        // Effettuo una POST al microservizio
	        ResponseEntity<AppuntamentoDTO[]> responseEntity = restTemplate.exchange(
	            url,
	            HttpMethod.POST,
	            requestEntity,
	            AppuntamentoDTO[].class
	        );

	        // Converto l'array ricevuto in una lista
	        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
	            return Arrays.asList(responseEntity.getBody());
	        } else {
	            System.err.println("Errore nella risposta del microservizio Appuntamenti: " + responseEntity.getStatusCode());
	            return Collections.emptyList();
	        }
	    } catch (Exception e) {
	        System.err.println("Errore durante la chiamata al microservizio Appuntamenti: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}
	 /*
     * ************************************************************************************************************
     *              INIZIO METODI DI EVENTI DEL PIANO                                                                                             *
     * ************************************************************************************************************
     * 
     */
	
	@Transactional
	public PianoTrattamento aggiungiEventoAlPiano(String pazienteId, String planId, EventoDelTrattamentoDTO eventoDTO) throws AccountNotFoundException {
	    ObjectId planIdObjectId = idHelper.stringToObjectId(planId);
	    ObjectId pazienteIdObjectId = idHelper.stringToObjectId(pazienteId);

	    PianoTrattamento piano = pianoTrattamentoRepository.findByIdAndPazienteId(planIdObjectId, pazienteIdObjectId)
	            .orElseThrow(() -> new RuntimeException("Piano non trovato"));

	    // Creare una nuova milestone dal DTO
	    EventoDelTrattamento newEvento = new EventoDelTrattamento();
	    newEvento.setDescrizione(eventoDTO.getDescrizione());
	    newEvento.setDataScade(eventoDTO.getDataScade());
	    newEvento.setCompletata(eventoDTO.isCompletata());
	    newEvento.setPianoTrattamentoId(planIdObjectId);
        newEvento.setTipologia(eventoDTO.getTipologia());
	    // Salva la milestone nel repository
	    EventoDelTrattamento eventoSalvato = eventoTrattamentoRepository.save(newEvento);

	    // Aggiungere l'ID della milestone al piano di trattamento
	    piano.getEventiTrattamentoIds().add(eventoSalvato.getId().toString());

	    // Salva il piano aggiornato
	    return pianoTrattamentoRepository.save(piano);
	}
	
	public PianoTrattamento aggiungiAppuntamentoAlPiano(String pazienteId, String planId,
			String appuntamentoId) {
		ObjectId planIdObjectId = idHelper.stringToObjectId(planId);
	    ObjectId pazienteIdObjectId = idHelper.stringToObjectId(pazienteId);
	    
	    PianoTrattamento piano = pianoTrattamentoRepository.findByIdAndPazienteId(planIdObjectId, pazienteIdObjectId)
	            .orElseThrow(() -> new RuntimeException("Piano non trovato"));
	    
	   piano.getAppuntamentiIds().add(appuntamentoId);
	   return pianoTrattamentoRepository.save(piano);
	}
	
    /*
     * ************************************************************************************************************
     *              INIZIO METODI DI CONVERSIONE DEI DTO                                                                                             *
     * ************************************************************************************************************
     * 
     */
    
 // Metodo di conversione (da Model a DTO) DI PIANOTRATTAMENTO
   	private PianoTrattamentoDTO convertToDTO(PianoTrattamento piano) {
    	    PianoTrattamentoDTO dto = new PianoTrattamentoDTO();
    	    dto.setId(idHelper.objectIdToString(piano.getId()));
    	    dto.setPazienteId(piano.getPazienteId().toHexString());
    	    dto.setNomePiano(piano.getNomePiano());
    	    dto.setAttivo(piano.isAttivo());
    	    dto.setDataInizio(piano.getDataInizio());
    	    dto.setDataFine(piano.getDataFine());

    	  
    	    
    	    List<EventoDelTrattamentoDTO> eventiDTO = piano.getEventiTrattamentoIds().stream()
        	        .map(id -> new ObjectId(id)) // Converte ogni String in ObjectId
        	        .map(eventoTrattamentoRepository::findById) // Cerca nel repository (restituisce Optional)
        	        .flatMap(Optional::stream) // Filtra i valori vuoti e prende quelli presenti
        	        .map(this::convertEventoToDTO) // Converte in DTO
        	        .collect(Collectors.toList());

        	    dto.setEventi(eventiDTO);
    	    return dto;
    	}

 
    
    private EventoDelTrattamentoDTO convertEventoToDTO(EventoDelTrattamento evento) {
    	EventoDelTrattamentoDTO dto = new EventoDelTrattamentoDTO();
        dto.setId(idHelper.objectIdToString(evento.getId()));
        dto.setPianoTrattamentoId(idHelper.objectIdToString(evento.getPianoTrattamentoId()));
        dto.setDescrizione(evento.getDescrizione());
        dto.setDataScade(evento.getDataScade());
        dto.setCompletata(evento.isCompletata());
        dto.setTipologia(evento.getTipologia());
        return dto;
    }

	

	

	
	
    /*
     * ************************************************************************************************************
     *             FINE METODI DI CONVERSIONE DEI DTO                                                                                             *
     * ************************************************************************************************************
     * 
     */
}

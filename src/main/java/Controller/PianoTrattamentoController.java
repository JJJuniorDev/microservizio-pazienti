package Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import DTO.AppuntamentoDTO;
import DTO.EventoDelTrattamentoDTO;
import DTO.PianoTrattamentoDTO;
import Model.PianoTrattamento;
import Services.PianoTrattamentoService;

import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/api/pazienti")
public class PianoTrattamentoController {

    private final PianoTrattamentoService pianoTrattamentoService;

    public PianoTrattamentoController(PianoTrattamentoService pianoTrattamentoService) {
        this.pianoTrattamentoService = pianoTrattamentoService;
    }

    @PostMapping("/{pazienteId}/piani")
    public PianoTrattamentoDTO creaPiano(@PathVariable String pazienteId, @RequestBody PianoTrattamentoDTO pianoDTO) {
        return pianoTrattamentoService.creaPiano(pazienteId, pianoDTO);
    }

    @GetMapping("/{pazienteId}/piani")
    public List<PianoTrattamentoDTO> getPianiByPazienteId(@PathVariable String pazienteId) {
    	System.out.println("SIAMO NELLA GETPIANI");
    	 return pianoTrattamentoService.getPianiTrattamentoByPazienteId(pazienteId);
    }
    
    @PostMapping("/{pazienteId}/piani/{planId}/appointments/addAppointment")
    public ResponseEntity<PianoTrattamento> addAppointmentToPlan(
            @PathVariable String pazienteId,
            @PathVariable String planId,
            @RequestBody String appointmentId) throws AccountNotFoundException {
        try {
            // Chiama il servizio per aggiungere la tappa
            PianoTrattamento pianoAggiornato = pianoTrattamentoService.aggiungiAppuntamentoAlPiano(pazienteId, planId, appointmentId);
            return ResponseEntity.ok(pianoAggiornato);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{pazienteId}/piani/{planId}/events/addEvent")
    public ResponseEntity<PianoTrattamento> addEventToPlan(
            @PathVariable String pazienteId,
            @PathVariable String planId,
            @RequestBody EventoDelTrattamentoDTO eventoDTO) {
        try {
            // Chiama il servizio per aggiungere la tappa
            PianoTrattamento pianoAggiornato = pianoTrattamentoService.aggiungiEventoAlPiano(pazienteId, planId, eventoDTO);
            return ResponseEntity.ok(pianoAggiornato);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
  
}

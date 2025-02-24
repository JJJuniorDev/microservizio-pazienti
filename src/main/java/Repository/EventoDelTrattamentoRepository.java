package Repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import Model.EventoDelTrattamento;


public interface EventoDelTrattamentoRepository extends MongoRepository <EventoDelTrattamento, ObjectId>{

}

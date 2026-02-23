package br.com.barionic.webcrud.dao;

import br.com.barionic.webcrud.entity.Hiperlink;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HiperlinkDAO extends GenericDAO<Hiperlink>{

    public HiperlinkDAO(){
        super(Hiperlink.class);
    }
}

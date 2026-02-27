package br.com.barionic.webcrud.dao;

import br.com.barionic.webcrud.entity.Cor;
import br.com.barionic.webcrud.entity.Hiperlink;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class HiperlinkDAO extends GenericDAO<Hiperlink>{

    public HiperlinkDAO(){
        super(Hiperlink.class);
    }

    @PersistenceContext(unitName = "webcrudPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean existeOutroComMesmoNome(String nome, Long idAtual){
        String jpql = "SELECT COUNT(h) FROM Hiperlink h WHERE h.name = :nome";
        if(idAtual != null){
            jpql += " AND h.id <> :idAtual";
        }
        var query = getEntityManager().createQuery(jpql, Long.class).setParameter("nome", nome);
        if(idAtual != null){
            query.setParameter("idAtual", idAtual);
        }
        return query.getSingleResult()>0;
    }

    public List<Hiperlink> findAllOrdenado(){
        return em.createQuery("SELECT h FROM Hiperlink h ORDER BY h.ordem ASC", Hiperlink.class).getResultList();
    }

    public List<Hiperlink> findByGrupoOrdenado(Long grupoId){
        return em.createQuery("SELECT h FROM Hiperlink h WHERE h.grupo.id = :grupoId ORDER BY h.ordem", Hiperlink.class)
                .setParameter("grupoId", grupoId)
                .getResultList();
    }

    public List<Hiperlink> findNoGrupo(){
        return em.createQuery("SELECT h FROM Hiperlink h WHERE h.grupo IS NULL ORDER BY h.ordem", Hiperlink.class).getResultList();
    }

    public Hiperlink buscarVizinho(Long grupoId, boolean semGrupo, Integer ordemAtual, boolean anterior){
        String operador = anterior ? "<" : ">";
        String direcao = anterior ? "DESC" : "ASC";

        StringBuilder jpql = new StringBuilder("SELECT h FROM Hiperlink h WHERE h.ordem " + operador + " :ordem");

        if (semGrupo){
            jpql.append(" AND h.grupo IS NULL");
        }
        else if(grupoId != null){
            jpql.append(" AND h.grupo.id = :grupoId");
        }
        jpql.append(" ORDER BY h.ordem ").append(direcao);
        var query = em.createQuery(jpql.toString(), Hiperlink.class).setParameter("ordem", ordemAtual).setMaxResults(1);
        if (!semGrupo && grupoId != null){
            query.setParameter("grupoId", grupoId);
        }
        return query.getResultStream().findFirst().orElse(null);
    }

    public Integer buscarMaiorOrdem(){
        return em.createQuery("SELECT MAX(h.ordem) FROM Hiperlink h", Integer.class).getSingleResult();
    }

    public List<Hiperlink> buscarComFiltro(String nome, Long grupoId, Long tagId, Cor cor){
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT h FROM Hiperlink h ");
        if (tagId != null){
            jpql.append("JOIN h.tags t ");
        }
        jpql.append("WHERE 1=1 ");
        if (nome != null && !nome.isBlank()){
            jpql.append("AND LOWER(h.name) LIKE LOWER(:nome) ");
        }
        if (grupoId != null){
            if(grupoId.equals(-1L)){
                jpql.append("AND h.grupo IS NULL ");
            }else {
                jpql.append("AND h.grupo.id = :grupoId ");
            }
        }
        if (tagId != null){
            if(tagId.equals(-1L)){
                jpql.append("AND h.tags IS EMPTY ");
            }else {
                jpql.append("AND t.id = :tagId ");
            }
        }
        if (cor != null){
            jpql.append("AND h.color = :cor ");
        }
        var query = em.createQuery(jpql.toString(), Hiperlink.class);
        if (nome != null && !nome.isBlank()){
            query.setParameter("nome", "%" + nome + "%");
        }
        if(grupoId != null && !grupoId.equals(-1L)){
            query.setParameter("grupoId", grupoId);
        }
        if(tagId != null && !tagId.equals(-1L)){
            query.setParameter("tagId", tagId);
        }
        if(cor != null){
            query.setParameter("cor", cor);
        }
        return query.getResultList();
    }

}

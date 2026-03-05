package br.com.barionic.webcrud.dao;

import br.com.barionic.webcrud.entity.Cor;
import br.com.barionic.webcrud.entity.Hiperlink;
import br.com.barionic.webcrud.util.Constantes;
import jakarta.ejb.Stateless;
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

    public Integer buscarMaiorOrdem(){
        return em.createQuery("SELECT MAX(h.ordem) FROM Hiperlink h", Integer.class).getSingleResult();
    }

    public List<Hiperlink> buscarPorIds(List<Long> ids){
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return em.createQuery("SELECT h FROM Hiperlink h WHERE h.id IN :ids", Hiperlink.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    public List<Hiperlink> buscarPorPrefixos(String prefixo){
        return em.createQuery(
                "SELECT h FROM Hiperlink h WHERE LOWER(h.name) LIKE :p", Hiperlink.class)
                .setParameter("p", prefixo.toLowerCase() + "%")
                .setMaxResults(5)
                .getResultList();
    }

    public List<Hiperlink> buscarComFiltro(String nome, Long grupoId, Long tagId, Cor cor){
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT DISTINCT h FROM Hiperlink h ");
        boolean joinTag = tagId != null && !Constantes.SEM_TAG.equals(tagId);
        if (joinTag) { jpql.append("JOIN h.tags t ");}
        jpql.append("WHERE 1=1 ");
        // Nome
        if (nome != null && nome.trim().length() >= 2) {
            jpql.append("AND LOWER(h.name) LIKE :nome ");
        }
        // Grupo
        if (grupoId != null) {
            if (Constantes.SEM_GRUPO.equals(grupoId)) {
                jpql.append("AND h.grupo IS NULL ");
            } else {
                jpql.append("AND h.grupo.id = :grupoId ");
            }
        }
        // Tag
        if (tagId != null) {
            if (Constantes.SEM_TAG.equals(tagId)) {
                jpql.append("AND h.tags IS EMPTY ");
            } else {
                jpql.append("AND t.id = :tagId ");
            }
        }
        // Cor
        if (cor != null) {
            jpql.append("AND h.color = :cor ");
        }
        // Ordenação padrão
        jpql.append("ORDER BY h.ordem ASC ");
        TypedQuery<Hiperlink> query = em.createQuery(jpql.toString(), Hiperlink.class);

        //====== SETANDO PARÂMETROS ======
        if (nome != null && nome.trim().length() >= 2) {
            query.setParameter("nome",
                    "%" + nome.toLowerCase().trim() + "%");
        }
        if (grupoId != null && !Constantes.SEM_GRUPO.equals(grupoId)) {
            query.setParameter("grupoId", grupoId);
        }
        if (tagId != null && !Constantes.SEM_TAG.equals(tagId)) {
            query.setParameter("tagId", tagId);
        }
        if (cor != null) {
            query.setParameter("cor", cor);
        }
        return query.getResultList();
    }

}

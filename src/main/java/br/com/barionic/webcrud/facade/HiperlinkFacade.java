package br.com.barionic.webcrud.facade;

import br.com.barionic.webcrud.dao.GrupoDAO;
import br.com.barionic.webcrud.dao.HiperlinkDAO;
import br.com.barionic.webcrud.dao.TagDAO;
import br.com.barionic.webcrud.entity.Cor;
import br.com.barionic.webcrud.entity.Hiperlink;
import br.com.barionic.webcrud.exception.RegraNegocioException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class HiperlinkFacade {

    @EJB
    private HiperlinkDAO dao;

    @EJB
    private GrupoDAO grupoDAO;

    @EJB
    private TagDAO tagDAO;

    public void salvar(Hiperlink hiperlink, Long grupoId, List<Long> tagIds){
        normalizarUrl(hiperlink);
        validarNomeUnico(hiperlink);
        associarGrupo(hiperlink, grupoId);
        associarTags(hiperlink, tagIds);

        if(hiperlink.getId() == null){
            Integer ultimaOrdem = dao.buscarMaiorOrdem();
            hiperlink.setOrdem(ultimaOrdem == null ? 1 : ultimaOrdem + 1);
        }

        if (hiperlink.getId() == null){
            dao.create(hiperlink);
        }else{
            dao.update(hiperlink);
        }
    }

    private void normalizarUrl(Hiperlink hiperlink){
        String url = hiperlink.getUrl();
        if (url != null && !url.isBlank()){
            url = url.trim();
            if (!url.startsWith("http://") && !url.startsWith("https://")){
                url = "https://" + url;
            }
            hiperlink.setUrl(url);
        }
    }

    private void validarNomeUnico(Hiperlink hiperlink){
        if (dao.existeOutroComMesmoNome(hiperlink.getName(), hiperlink.getId())){
            throw new RegraNegocioException("Já existe um hiperlink com esse nome.");
        }
    }

    private void associarGrupo(Hiperlink hiperlink, Long grupoId){
        if (grupoId != null){
            var grupo = grupoDAO.find(grupoId);
            hiperlink.setGrupo(grupo);
        }
    }

    private void associarTags(Hiperlink hiperlink, List<Long> tagIds){
        if (tagIds != null && !tagIds.isEmpty()){
            var tags = tagDAO.buscarPorIds(tagIds);
            hiperlink.setTags(tags);
        }
    }

    public List<Hiperlink> buscarComFiltro(String nome, Long grupoId, Long tagId, Cor cor){
        return dao.buscarComFiltro(nome, grupoId, tagId, cor);
    }

    public void remover(Long id){
        Hiperlink hiperlink = dao.find(id);
        if(hiperlink != null){
            dao.remove(hiperlink);
        }
    }

    public Hiperlink buscarPorId(Long id){ return dao.find(id); }

    public List<Hiperlink> listarTodos(){
        return dao.findAllOrdenado();
    }

    public List<Hiperlink> listarPorGrupoOrdenado(Long grupoId){
        return dao.findByGrupoOrdenado(grupoId);
    }

    public void subir(Long id, Long groupoId){
        Hiperlink atual = dao.find(id);
        Hiperlink anterior;
        if(groupoId != null){
            anterior = dao.buscarAnteriorPorGrupo(groupoId, atual.getOrdem());
        }else{
            anterior = dao.buscarAnterior(atual.getOrdem());
        }

        if(anterior != null){
            Integer temp = atual.getOrdem();
            atual.setOrdem(anterior.getOrdem());
            anterior.setOrdem(temp);
        }
    }

    public void descer(Long id, Long grupoId){
        Hiperlink atual = dao.find(id);
        Hiperlink proximo;
        if (grupoId != null){
            proximo = dao.buscarProximoPorGrupo(grupoId, atual.getOrdem());
        }else{
            proximo = dao.buscarProximo(atual.getOrdem());
        }

        if (proximo != null){
            Integer temp = atual.getOrdem();
            atual.setOrdem(proximo.getOrdem());
            proximo.setOrdem(temp);
        }
    }


}

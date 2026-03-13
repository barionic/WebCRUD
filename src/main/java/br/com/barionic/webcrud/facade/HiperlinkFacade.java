package br.com.barionic.webcrud.facade;

import static br.com.barionic.webcrud.util.Constantes.SEM_GRUPO;
import br.com.barionic.webcrud.dao.GrupoDAO;
import br.com.barionic.webcrud.dao.HiperlinkDAO;
import br.com.barionic.webcrud.dao.TagDAO;
import br.com.barionic.webcrud.dto.LinkPreviewDTO;
import br.com.barionic.webcrud.entity.Cor;
import br.com.barionic.webcrud.entity.Hiperlink;
import br.com.barionic.webcrud.entity.Tag;
import br.com.barionic.webcrud.exception.RegraNegocioException;
import br.com.barionic.webcrud.util.NotaItem;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        boolean novo = hiperlink.getId() == null;

        if(novo){
            Integer ultimaOrdem = dao.buscarMaiorOrdem();
            hiperlink.setOrdem(ultimaOrdem == null ? 1 : ultimaOrdem + 1);
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
            if(grupo == null){
                throw new RegraNegocioException("Grupo Inválido");
            }
            hiperlink.setGrupo(grupo);
        }
    }

    private void associarTags(Hiperlink hiperlink, List<Long> tagIds){
        if (tagIds != null && !tagIds.isEmpty()){
            var tags = tagDAO.buscarPorIds(tagIds);
            if (tags.size() != tagIds.size()){
                throw new RegraNegocioException("Uma ou mais Tags Inválidas");
            }
            hiperlink.setTags(tags);
        }else{
            hiperlink.setTags(null);
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

    public List<Hiperlink> listarSemGrupoOrdenado(){
        return dao.findNoGrupo();
    }

    public void atualizarLista(List<Hiperlink> lista){
        for (Hiperlink h : lista){
            dao.update(h);
        }
    }

    public void atualizarOrdem(List<Long> listaIds){
        var hiperlinks = dao.buscarPorIds(listaIds);
        Map<Long, Hiperlink> mapa = hiperlinks.stream().collect(Collectors.toMap(Hiperlink::getId, h -> h));
        for (int i=0; i<listaIds.size(); i++){
            Hiperlink h = mapa.get(listaIds.get(i));
            if (h != null){
                h.setOrdem(i+1);
            }
        }
    }

    public List<Hiperlink> listarPorSelecao(Long grupoSelecionado){
        if (grupoSelecionado == null){
            return listarTodos();
        }else if(SEM_GRUPO.equals(grupoSelecionado)){
            return listarSemGrupoOrdenado();
        }else{
            return listarPorGrupoOrdenado(grupoSelecionado);
        }
    }

    public List<Hiperlink> buscarPorPrefixo(String prefixo){
        return dao.buscarPorPrefixo(prefixo);
    }

    public List<Hiperlink> buscarBacklinks(String nome){
        return dao.buscarReferencias(nome);
    }

    public LinkPreviewDTO toPreviewDTO(Hiperlink h){

        String grupo = h.getGrupo() != null
                ? h.getGrupo().getGrupoName()
                : "Sem Grupo";

        List<String> tags = h.getTags() == null
                ? List.of()
                : h.getTags()
                    .stream()
                    .limit(3)
                    .map(Tag::getTagName)
                    .collect(Collectors.toList());

        List<String> notas = h.getNotasChecklist() == null
                ? List.of()
                : h.getNotasChecklist()
                    .stream()
                    .limit(3)
                    .map(NotaItem::getTexto)
                    .collect(Collectors.toList());

        return new LinkPreviewDTO(
                h.getId(),
                h.getName(),
                h.getUrl(),
                grupo,
                tags,
                notas
        );
    }

}

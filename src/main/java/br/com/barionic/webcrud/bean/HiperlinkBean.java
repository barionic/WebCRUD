package br.com.barionic.webcrud.bean;

import br.com.barionic.webcrud.dto.LinkPreviewDTO;
import br.com.barionic.webcrud.entity.Cor;
import br.com.barionic.webcrud.entity.Grupo;
import br.com.barionic.webcrud.entity.Hiperlink;
import br.com.barionic.webcrud.entity.Tag;
import br.com.barionic.webcrud.exception.RegraNegocioException;
import br.com.barionic.webcrud.facade.GrupoFacade;
import br.com.barionic.webcrud.facade.HiperlinkFacade;
import br.com.barionic.webcrud.facade.TagFacade;
import br.com.barionic.webcrud.util.NotaItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

//Imports javax são usados unica e exclusivamente em trustAllCertificates()
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Named("hiperlinkBean")
@ViewScoped
public class HiperlinkBean implements Serializable {
    private Hiperlink hiperlink;
    private List<Hiperlink> lista;
    private List<Long> listaIds;
    private List<Grupo> grupos;
    private Long grupoId;
    private List<Tag> tags;
    private List<Long> tagIds;
    private List<NotaItem> notes = new ArrayList<>();

    private String filtroNome;
    private Long filtroGrupoId;
    private Long filtroTagId;
    private Cor filtroCor;

    private Long grupoSelecionado;
    private boolean mostrarLog = false;
    private boolean modoEdicao = false;
    private Map<Long, String> mapaNomes;
    private final ObjectMapper mapper = new ObjectMapper();
    private Map<Long, List<Hiperlink>> backlinksCache = new HashMap<>();
    private boolean recentesPrimeiro;
    private String nomeSugerido;

    @Inject
    private HiperlinkFacade facade;

    @Inject
    private GrupoFacade grupoFacade;

    @Inject
    private TagFacade tagFacade;

    @PostConstruct
    public void init() {
        trustAllCertificates();
        hiperlink = new Hiperlink();
        lista = facade.listarTodos();
        grupos = grupoFacade.listarTodos();
        tags = tagFacade.listarTodos();

        notes.add(new NotaItem(false, ""));

        carregarLista();
    }

    public void salvar() {
        String notasTexto = notes.stream()
                .filter(n -> n.getTexto() != null && !n.getTexto().trim().isEmpty())
                .map(n -> (n.isConcluido() ? "[x] " : "[] ") + n.getTexto().trim())
                .collect(Collectors.joining("\n"));

        hiperlink.setNotes(notasTexto);

        try {
            facade.salvar(hiperlink, grupoId, tagIds);
            String msg = modoEdicao ? "Link Editado com Sucesso!" : "Link Salvo com Sucesso!";

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));

            cancelarEdicao();//Limpar Registros
            carregarLista();
        } catch (RegraNegocioException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            e.getMessage(), null));
        }
    }

    public void editar(Hiperlink hiperlink) {
        this.hiperlink = hiperlink;
        this.grupoId = hiperlink.getGrupo() != null ? hiperlink.getGrupo().getId() : null;

        if (hiperlink.getNotes() != null) {
            notes = Arrays.stream(hiperlink.getNotes().split("\\r?\\n"))
                    .map(String::trim)
                    .filter(l -> !l.isBlank())
                    .map(l -> {
                        boolean done = l.startsWith("[x]");
                        String texto = l.replace("[x]", "")
                                .replace("[ ]", "")
                                .trim();
                        return new NotaItem(done, texto);
                    }).collect(Collectors.toList());
        } else {
            notes = new ArrayList<>();
        }
        if (notes.isEmpty()) {
            notes.add(new NotaItem(""));
        }

        if (hiperlink.getTags() != null) {
            this.tagIds = hiperlink.getTags().stream().map(Tag::getId).collect(Collectors.toList());
        }
        modoEdicao = true;
    }

    public void remover(Hiperlink h) {
        facade.remover(h.getId());
        lista = facade.listarTodos();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Link removido com sucesso!", null));
    }

    public void cancelarEdicao() {
        hiperlink = new Hiperlink();
        grupoId = null;
        tagIds = null;
        modoEdicao = false;

        notes = new ArrayList<>();
        notes.add(new NotaItem(false, ""));
    }

    public void buscar() {
        lista = facade.buscarComFiltro(filtroNome, filtroGrupoId, filtroTagId, filtroCor);
        if (recentesPrimeiro && lista != null) {
            lista.sort((a, b) -> {
                LocalDateTime dataA = a.getDataAtualizacao() != null ? a.getDataAtualizacao() : a.getDataCriacao();
                LocalDateTime dataB = b.getDataAtualizacao() != null ? b.getDataAtualizacao() : b.getDataCriacao();
                return dataB.compareTo(dataA);
            });
        }
        /*
        if (filtroNome != null && filtroNome.length() == 1) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Type at least 2 characters to search by name.", null));

        }
        */
    }

    public void limparFiltro() {
        filtroNome = null;
        filtroCor = null;
        filtroGrupoId = null;
        filtroTagId = null;
        carregarLista();
    }

    public void carregarLista() {
        backlinksCache.clear();
        lista = facade.listarPorSelecao(grupoSelecionado);
        if (lista == null) {
            lista = new ArrayList<>();
        }
        listaIds = lista.stream().map(Hiperlink::getId).collect(Collectors.toList());
        mapaNomes = lista.stream().collect(Collectors.toMap(Hiperlink::getId, Hiperlink::getName));
    }

    public String buscarNomePorId(Long id) {
        //if (mapaNomes == null) return "";
        return mapaNomes.getOrDefault(id, "");
    }

    public void salvarNovaOrdem() {
        facade.atualizarOrdem(listaIds);
        carregarLista();
    }

    public void adicionarNota() {
        notes.add(new NotaItem(""));
    }

    public void removeNota(NotaItem nota) {
        notes.remove(nota);
    }

    public List<String> buscarSugestoes(String query) {
        if (query == null) return List.of();
        int pos = query.lastIndexOf("@");
        if (pos == -1) return List.of();
        String termo = query.substring(pos + 1).toLowerCase();
        if (termo.isBlank()) return List.of();

        return facade.listarTodos()
                .stream()
                .map(Hiperlink::getName)
                .filter(nome -> nome.toLowerCase().contains(termo))
                .limit(8)
                .collect(Collectors.toList());
    }

    public void carregarNotas() {
        notes = new ArrayList<>();
        if (hiperlink.getNotes() == null || hiperlink.getNotes().isEmpty()) {
            notes.add(new NotaItem(false, ""));
            return;
        }
        String[] linhas = hiperlink.getNotes().split("\n");
        for (String linha : linhas) {
            boolean concluido = linha.startsWith("[x]");
            String texto = linha.replace("[x]", "").replace("[]", "").trim();
            notes.add(new NotaItem(concluido, texto));
        }
    }

    public String formatarNota(String texto) {
        if (texto == null) return "";

        List<String> nomes = facade.listarTodos()
                .stream()
                .map(Hiperlink::getName)
                .sorted((a,b) -> Integer.compare(b.length(), a.length()))
                .collect(Collectors.toList());

        for (String nome : nomes){
            texto = texto.replaceAll(
                    "@" + Pattern.quote(nome) + "\\b",
                    "<a href='javascript:void(0)' class='nota-card-link' onclick=\"abrirLink('" + nome + "')\" onmouseover=\"mostrarPreview(event,this,'" + nome + "')\" onmouseout=\"esconderPreview(this)\">@" + nome + "</a>"
            );
        }

        //@([a-zA-Z0-9À-ÿ _-]+)
        //texto = texto.replaceAll("@([^@]+?)(?=@|$)", "<a href='javascript:void(0)' class='nota-card-link' onclick=\"abrirLink('$1')\" onmouseover=\"mostrarPreview(event,this,'$1')\" onmouseout=\"esconderPreview(this)\">@$1</a>");
        texto = texto.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        texto = texto.replaceAll("`(.*?)`", "<code>$1</code>");

        return texto;
    }

    public List<Hiperlink> buscarBacklinks(Hiperlink link){
        if (backlinksCache.containsKey(link.getId())){
            return backlinksCache.get(link.getId());
        }
        List<Hiperlink> lista = facade.buscarBacklinks(link.getName());
        backlinksCache.put(link.getId(), lista);
        return lista;
    }

    public String getListaIdsJSON() {
        return lista.stream()
                .map(l -> l.getId().toString())
                .collect(Collectors.joining(",", "[", "]"));
    }

    public String getListaLinksJSON() {
        try {

            List<LinkPreviewDTO> listaDTO =
                    lista.stream()
                            .map(facade::toPreviewDTO)
                            .collect(Collectors.toList());
            return mapper.writeValueAsString(listaDTO);
        } catch (Exception e) {
            throw new RegraNegocioException("Erro ao gerar JSON dos links.");
        }
    }

    public String getTodosLinksJSON() {
        return facade.listarTodos()
                .stream()
                .map(Hiperlink::getName)
                .map(n -> "\"" + n.replace("\"","\\\"") + "\"")
                .collect(Collectors.joining(",", "[", "]"));
    }

    public void onMentionSelect(org.primefaces.event.SelectEvent<String> event){
        //impedir comportamento padrão
    }

    public void sugerirTitulo() {
        System.out.println("URL recebida: " + hiperlink.getUrl());
        if (hiperlink.getUrl() == null || hiperlink.getUrl().isBlank()){
            nomeSugerido = null;
            return;
        }
        try {
            String url = hiperlink.getUrl().trim();
            if (!url.startsWith("http")){
                url = "https://" + url;
            }
            System.out.println("URL final: " + url);
            Document doc;
            try {
                // 1) Tentativa Direta
                doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .referrer("https://www.google.com")
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Connection", "keep-alive")
                        .timeout(3000)
                        .maxBodySize(0)
                        .ignoreContentType(true)
                        .followRedirects(true)
                        .get();
            } catch (Exception e) {
                // 2) fallback mobile
                //String previewUrl = "https://textise.net/showtext.aspx?strURL=" + url;
                doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Linux; Android 10)")
                        .timeout(8000)
                        .get();
            }
            // 3) Tentar OpenGraph
            String titulo = doc.select("meta[property=og:title]").attr("content");

            // 4) Fallback para <title>
            if (titulo == null || titulo.isBlank()){
                titulo = doc.title();
            }

            // 5) Fallback StackOverflow / páginas simples
            if (titulo == null || titulo.isBlank()) {
                titulo = doc.select("h1").first() != null
                        ? doc.select("h1").first().text()
                        : null;
            }

            // 6) Fallback Final
            if (titulo == null || titulo.isBlank()){
                nomeSugerido = "Sem Sugestão Disponível";
            }else{
                nomeSugerido = titulo.trim();
            }

        } catch (Exception e){
            e.printStackTrace();
            nomeSugerido = "Sem Sugestão Disponível";
        }
    }

    public String getDominio() {
        if (hiperlink == null || hiperlink.getUrl() == null){
            return null;
        }
        try {
            String url = hiperlink.getUrl();
            if(!url.startsWith("http")){
                url = "https://" + url;
            }
            java.net.URI uri = new java.net.URI(url);
            return uri.getHost();
        } catch (Exception e){
            return null;
        }
    }

    public String getFaviconUrl() {
        String dominio = getDominio();
        if (dominio == null){
            return null;
        }
        return "https://www.google.com/s2/favicons?sz=32&domain=" + dominio;
    }

    public void usarNomeSugerido() {
        if (nomeSugerido != null){
            hiperlink.setName(nomeSugerido);
        }
    }

    public boolean isTemSugestao(){
        return nomeSugerido != null && !nomeSugerido.equals("Sem Sugestão Disponível");
    }

    public static void trustAllCertificates() {
        //ATENÇÃO: isso desliga a verificação de segurança HTTPS. Está seguro porque só fazemos get(), nao enviamos dados.
        try{
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {return null;}
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception ignored) {}
    }

    // ==== Getters & Setters ====
    public Hiperlink getHiperlink() {
        return hiperlink;
    }

    public void setHiperlink(Hiperlink hiperlink) {
        this.hiperlink = hiperlink;
    }

    public List<Hiperlink> getLista() {
        return lista;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Cor[] getColors() {
        return Cor.values();
    }

    public boolean isMostrarLog() {
        return mostrarLog;
    }

    public void setMostrarLog(boolean mostrarLog) {
        this.mostrarLog = mostrarLog;
    }


    public String getFiltroNome() {
        return filtroNome;
    }

    public Long getFiltroGrupoId() {
        return filtroGrupoId;
    }

    public Long getFiltroTagId() {
        return filtroTagId;
    }

    public Cor getFiltroCor() {
        return filtroCor;
    }

    public void setFiltroCor(Cor filtroCor) {
        this.filtroCor = filtroCor;
    }

    public void setFiltroNome(String filtroNome) {
        this.filtroNome = filtroNome;
    }

    public void setFiltroTagId(Long filtroTagId) {
        this.filtroTagId = filtroTagId;
    }

    public void setFiltroGrupoId(Long filtroGrupoId) {
        this.filtroGrupoId = filtroGrupoId;
    }

    public Long getGrupoSelecionado() {
        return grupoSelecionado;
    }

    public void setGrupoSelecionado(Long grupoSelecionado) {
        this.grupoSelecionado = grupoSelecionado;
    }


    public boolean isModoEdicao() {
        return modoEdicao;
    }

    public void setModoEdicao(boolean modoEdicao) {
        this.modoEdicao = modoEdicao;
    }

    public List<Long> getListaIds() {
        return listaIds;
    }

    public void setListaIds(List<Long> listaIds) {
        this.listaIds = listaIds;
    }

    public List<NotaItem> getNotes() {
        if (notes == null) {
            notes = new ArrayList<>();
            notes.add(new NotaItem(""));
        }
        return notes;
    }

    public void setNotes(List<NotaItem> notes) {
        this.notes = notes;
    }

    public boolean isRecentesPrimeiro() { return recentesPrimeiro; }

    public void setRecentesPrimeiro(boolean recentesPrimeiro) { this.recentesPrimeiro = recentesPrimeiro; }

    public String getNomeSugerido() { return nomeSugerido; }

    public void setNomeSugerido(String nomeSugerido) { this.nomeSugerido = nomeSugerido; }
}
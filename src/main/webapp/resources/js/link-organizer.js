let destinoCardId = null;
let selectedIndex = -1;
let sugestoesAtuais = [];

function abrirLink(nome){
    const nomeNormalizado = nome.replace("@","").trim().toLowerCase();
    const link = listaLinks.find(l =>
        l.nome.toLowerCase() === nomeNormalizado
    );
    if(!link){
        console.warn("Link não encontrado:", nome);
        return;
    }
    const id = link.id;
    const index = indexPorId[id];
    if(index === undefined) return;
    const page = Math.floor(index / 10);
    destinoCardId = id;
    PF('dataViewLinks').getPaginator().setPage(page);
    scrollParaDestino();
}

function scrollParaDestino(){
    if(!destinoCardId) return;
    const card = [...document.querySelectorAll(".card-link")]
        .find(el => el.dataset.id == destinoCardId);
    if(!card) return;
    card.scrollIntoView({
        behavior: "smooth",
        block: "center"
    });
    card.classList.add("card-highlight");
    setTimeout(() => {
        card.classList.remove("card-highlight");
    }, 2000);
    destinoCardId = null;
}

$(document).on("pfAjaxComplete", function () {
    document.querySelectorAll(".link-preview")
        .forEach(p => p.remove());
    initDropdownToggles();
});

function mostrarPreview(event, element, nome){
    const nomeNormalizado = nome.replace("@","").trim().toLowerCase();
    esconderPreview(element);
    const link = listaLinks.find(l =>
        l.nome.toLowerCase() === nomeNormalizado
    );
    if(!link) return;
    const preview = document.createElement("div");
    preview.className = "link-preview";
    let html = `<strong>${link.nome}</strong><br>`;
    if(link.url){
        try{
            const urlObj = new URL(link.url);
            const favicon = urlObj.origin + "/favicon.ico";
            html += `<img src="${favicon}"
                     onerror="this.style.display='none'"
                     style="width:14px;height:14px;margin-right:6px;vertical-align:middle;">`;

        }catch(e){}
        html += `<small>${link.url}</small><br>`;
    }
    if(link.tags && link.tags.length > 0){
        const tags = link.tags.slice(0,3);
        html += "<em>Tags:</em> ";
        tags.forEach(t => {
            html += `#${t} `;
        });
        html += "<br>";
    }
    if (link.grupo && link.grupo != "Sem Grupo"){
        html+= `<em>Grupo: ${link.grupo}</em><br>`;
    }
    if(link.notas && link.notas.length > 0){
        const notas = link.notas.slice(0,3)
        html += "<hr>";
        html += "<strong>Notas Adicionadas:</strong><br>";
        notas.forEach(n => {
            html += `⬤ ${n}<br>`;
        });
    }
    preview.innerHTML = html;
    document.body.appendChild(preview);
    preview.style.left = (event.pageX + 10) + "px";
    preview.style.top = (event.pageY + 10) + "px";
    element.previewElement = preview;
}

function esconderPreview(element){
    if(!element.previewElement) return;
    element.previewElement.remove();
    element.previewElement = null;
}

let mentionMenu = null;
let currentTextarea = null;
document.addEventListener("DOMContentLoaded", function(){
    mentionMenu = document.getElementById("mention-menu");
    initDropdownToggles();
    document.addEventListener("input", function(e){
        if (!e.target.classList.contains("mention-editor")) return;
        const textarea = e.target;
        const valor = textarea.value;
        const pos = textarea.selectionStart;
        const antes = valor.substring(0, pos);
        const match = antes.match(/@([\wÀ-ÿ ]*)$/);
        if (!match){
            mentionMenu.style.display="none";
            return;
        }
        const termo = match[1].toLowerCase();
        sugestoesAtuais = todosLinks
            .filter(nome => nome.toLowerCase().includes(termo))
            .slice(0,8);
        if (sugestoesAtuais.length === 0){
            mentionMenu.style.display="none";
            return;
        }
        mentionMenu.innerHTML="";
        selectedIndex = -1;
        sugestoesAtuais.forEach((nome)=>{
            const item=document.createElement("div");
            item.className="mention-item";
            item.innerText="@"+nome;
            item.onclick=()=>{
                inserirMention(textarea, valor, antes, pos, nome);
            };
            item.onmouseenter = (event) =>{
                mostrarPreview(event, item, nome);
            }
            item.onmouseleave = ()=>{
                esconderPreview(item);
            }
            mentionMenu.appendChild(item);
        });
        const rect = textarea.getBoundingClientRect();
        mentionMenu.style.left = rect.left+"px";
        mentionMenu.style.top = rect.bottom+"px";
        mentionMenu.style.display = "block";
        currentTextarea = textarea;
    });

    document.addEventListener("keydown", function(e){
        if(!mentionMenu || mentionMenu.style.display !== "block") return;
        const items = mentionMenu.querySelectorAll(".mention-item");
        if(items.length === 0) return;
        if(e.key === "ArrowDown"){
            e.preventDefault();
            selectedIndex = (selectedIndex + 1) % items.length;
        }
        else if(e.key === "ArrowUp"){
            e.preventDefault();
            selectedIndex = (selectedIndex - 1 + items.length) % items.length;
        }
        else if(e.key === "Enter"){
            if(selectedIndex >= 0){
                e.preventDefault();
                const nome = sugestoesAtuais[selectedIndex];
                inserirMention(
                    currentTextarea,
                    currentTextarea.value,
                    currentTextarea.value.substring(0,currentTextarea.selectionStart),
                    currentTextarea.selectionStart,
                    nome
                );
            }
        }
        else if(e.key === "Escape"){
            mentionMenu.style.display="none";
        }
        items.forEach((item,i)=>{
            item.classList.toggle("mention-selected", i === selectedIndex);
        });
    });
});

function inserirMention(textarea, valor, antes, pos, nome){
    const inicio = antes.replace(/@([\wÀ-ÿ ]*)$/, "@"+nome+" ");
    textarea.value = inicio + valor.substring(pos);
    mentionMenu.style.display="none";
    textarea.focus();
}

function initDropdownToggles(){
    document.querySelectorAll(".toggle-header").forEach(header => {
        header.onclick = () => {
            const lista = header.nextElementSibling;
            const arrow = header.querySelector(".toggle-arrow");
            if (!lista) return;
            lista.classList.toggle("open");
            if (arrow){
                arrow.textContent = lista.classList.contains("open") ? "▲" : "▼";
            }
        };
    });
}
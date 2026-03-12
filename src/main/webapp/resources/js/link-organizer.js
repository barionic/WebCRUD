let destinoCardId = null;

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
});

function mostrarPreview(event, element, nome){
    esconderPreview(element);
    const preview = document.createElement("div");
    preview.className = "link-preview";
    preview.innerText = nome;
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

        const lista = todosLinks
            .filter(nome => nome.toLowerCase().includes(termo))
            .slice(0,8);

        if (lista.lenght===0){
            mentionMenu.style.display="none";
            return;
        }

        mentionMenu.innerHTML="";

        lista.forEach(nome =>{
            const item = document.createElement("div");
            item.className="mention-item";
            item.innerText="@"+nome;

            item.onclick=()=>{
                const inicio = antes.replace(/@([\wÀ-ÿ ]*)$/,"@"+nome+" ");
                textarea.value = inicio + valor.substring(pos);
                mentionMenu.style.display="none";
                textarea.focus();
            };
            mentionMenu.appendChild(item);
        });

        const rect = textarea.getBoundingClientRect();

        mentionMenu.style.left = rect.left+"px";
        mentionMenu.style.top = rect.bottom+"px";

        mentionMenu.style.display = "block";

        currentTextarea = textarea;

    });
});

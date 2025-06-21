import tkinter as tk
from tkinter import messagebox, ttk
import requests
from datetime import datetime
from config import BASE_URL, SESSION, close_windows, kill_windows


# CLIENTES
def menu_cliente(root):
    root.withdraw()
    janela_cliente = tk.Toplevel(root)
    janela_cliente.title("Menu Cliente")
    janela_cliente.geometry("300x200")

    tk.Button(
        janela_cliente,
        text="➕ Inserir cliente",
        command=lambda: cadastro_cliente(janela_cliente),
    ).pack(pady=5)
    tk.Button(
        janela_cliente,
        text="🗑️ Deletar cliente",
        bg="red",
        fg="white",
        command=lambda: excluir_cliente_selecionado(janela_cliente),
    ).pack(pady=5)
    tk.Button(
        janela_cliente,
        text="✏️ Alterar cliente",
        command=lambda: alterar_cliente_selecionado(janela_cliente),
    ).pack(pady=5)

    tk.Button(
        janela_cliente,
        text="❌ Fechar",
        command=lambda: close_windows(root, janela_cliente),
    ).pack(pady=10)
    kill_windows(root, janela_cliente)


def _get_clientes():
    try:
        response = SESSION.get(f"{BASE_URL}/client")

        response.raise_for_status()
        dados = response.json()
        return dados.get("content", [])

    except requests.RequestException as e:
        print("Erro ao buscar clientes:", e)
        return []


def _atualizar_dropdown(combo, clientes):
    nomes = [cliente["name"] for cliente in clientes]
    combo["values"] = nomes
    combo.set("Selecione um cliente")


def _janela_insercao_alteracao(
    janela_pai,
    cliente=None,
    titulo="",
    confirmar=False,
    acao=(),  # função: inserir_cliente ou alterar_cliente
):
    janela_pai.withdraw()
    janela = tk.Toplevel(janela_pai)
    janela.title(titulo)
    janela.geometry("350x250")

    nome_padrao = cliente["name"] if cliente else ""
    email_padrao = cliente["email"] if cliente else ""
    telefone_padrao = cliente["phone"] if cliente else ""

    tk.Label(janela, text="Nome:").pack()
    entry_nome = tk.Entry(janela, width=40)
    entry_nome.pack()
    entry_nome.insert(0, nome_padrao)

    tk.Label(janela, text="Email:").pack()
    entry_email = tk.Entry(janela, width=40)
    entry_email.pack()
    entry_email.insert(0, email_padrao)

    tk.Label(janela, text="Telefone:").pack()
    entry_telefone = tk.Entry(janela, width=40)
    entry_telefone.pack()
    entry_telefone.insert(0, telefone_padrao)

    tk.Label(janela, text="Senha:").pack()
    entry_password = tk.Entry(janela, width=40)
    entry_password.pack()
    entry_password.insert(0, "")

    def ao_confirmar():
        dados = {
            "name": entry_nome.get(),
            "email": entry_email.get(),
            "password": entry_password.get(),
            "phone": entry_telefone.get(),
        }

        if confirmar:
            nome = cliente["name"] if cliente else "este cliente"
            confirm = messagebox.askyesno(
                "Confirmação", f"Deseja alterar os dados de {nome}?", parent=janela
            )
            if not confirm:
                return

        acao(cliente["id"], dados, janela) if cliente else acao(dados, janela)
        close_windows(janela_pai, janela)

    texto_botao = "✏️ Alterar" if cliente else "➕ Inserir"
    tk.Button(janela, text=texto_botao, command=ao_confirmar).pack(pady=10)
    kill_windows(janela_pai, janela)


def inserir_cliente(dados, janela_pai):
    try:
        response = SESSION.post(f"{BASE_URL}/client", json=dados)
        response.raise_for_status()
        messagebox.showinfo(
            "Sucesso", "Cliente inserido com sucesso!", parent=janela_pai
        )
    except requests.RequestException as e:
        messagebox.showerror(
            "Erro", f"Erro ao inserir cliente:\n{e}", parent=janela_pai
        )


def cadastro_cliente(janela_pai):
    _janela_insercao_alteracao(
        janela_pai, titulo="Inserir Cliente", confirmar=False, acao=inserir_cliente
    )


def excluir_cliente(cliente_id, janela_pai):
    try:
        response = SESSION.delete(f"{BASE_URL}/client/{cliente_id}")
        if response.status_code == 204:
            messagebox.showinfo(
                "Sucesso", "Cliente excluído com sucesso!", parent=janela_pai
            )
        else:
            messagebox.showerror(
                "Erro", f"Erro ao excluir: {response.text}", parent=janela_pai
            )
    except requests.RequestException as e:
        messagebox.showerror("Erro", f"Erro de conexão: {e}", parent=janela_pai)


def excluir_cliente_selecionado(janela_cliente):
    janela_cliente.withdraw()
    janela_exc_cliente = tk.Toplevel(janela_cliente)
    janela_exc_cliente.title("Excluir Cliente")
    janela_exc_cliente.geometry("300x200")

    clientes = _get_clientes()

    combo = ttk.Combobox(janela_exc_cliente, state="readonly", width=30)
    combo.pack(pady=10)
    _atualizar_dropdown(combo, clientes)

    def confirmar_exclusao():
        nome = combo.get()
        if nome == "Selecione um cliente" or not nome:
            messagebox.showwarning(
                "Aviso", "Selecione um cliente válido.", parent=janela_exc_cliente
            )
            return

        cliente = next((c for c in clientes if c["name"] == nome), None)
        if cliente:
            confirmacao = messagebox.askyesno(
                "Confirmar", f"Deseja excluir {nome}?", parent=janela_exc_cliente
            )
            if confirmacao:
                excluir_cliente(cliente["id"], janela_exc_cliente)
                close_windows(janela_cliente, janela_exc_cliente)

    tk.Button(janela_exc_cliente, text="🗑️ Excluir", command=confirmar_exclusao).pack(
        pady=10
    )
    kill_windows(janela_cliente, janela_exc_cliente)


def alterar_cliente(cliente_id, dados, janela_pai):
    try:
        response = SESSION.put(f"{BASE_URL}/client/{cliente_id}", json=dados)
        response.raise_for_status()
        messagebox.showinfo(
            "Sucesso", "Cliente alterado com sucesso!", parent=janela_pai
        )
    except requests.RequestException as e:
        messagebox.showerror("Erro", f"Erro ao alterar cliente: {e}", parent=janela_pai)


def alterar_cliente_selecionado(janela_cliente):
    janela_cliente.withdraw()
    janela_alt_cliente = tk.Toplevel(janela_cliente)
    janela_alt_cliente.title("Alterar Cliente")
    janela_alt_cliente.geometry("300x200")

    clientes = _get_clientes()

    combo = ttk.Combobox(janela_alt_cliente, state="readonly", width=30)
    combo.pack(pady=10)
    _atualizar_dropdown(combo, clientes)

    def abrir_janela_edicao(event):
        nome = combo.get()
        if nome == "Selecione um cliente" or not nome:
            return
        cliente = next((c for c in clientes if c["name"] == nome), None)
        if cliente:
            _janela_insercao_alteracao(
                janela_alt_cliente,
                cliente=cliente,
                titulo=f"Alterar Cliente: {nome}",
                confirmar=True,
                acao=alterar_cliente,
            )
            close_windows(janela_cliente, janela_alt_cliente)

    combo.bind("<<ComboboxSelected>>", abrir_janela_edicao)
    kill_windows(janela_cliente, janela_alt_cliente)


# QUARTOS
def menu_quarto(root):
    root.withdraw()
    janela_quarto = tk.Toplevel(root)
    janela_quarto.title("Menu Quartos")
    janela_quarto.geometry("300x200")

    tk.Button(
        janela_quarto,
        text="➕ Inserir quarto",
        command=lambda: cadastro_quarto(janela_quarto),
    ).pack(pady=5)
    tk.Button(
        janela_quarto,
        text="🗑️ Deletar quarto",
        bg="red",
        fg="white",
        command=lambda: excluir_quarto_selecionado(janela_quarto),
    ).pack(pady=5)
    tk.Button(
        janela_quarto,
        text="✏️ Alterar quarto",
        command=lambda: alterar_quarto_selecionado(janela_quarto),
    ).pack(pady=5)

    tk.Button(
        janela_quarto,
        text="❌ Fechar",
        command=lambda: close_windows(root, janela_quarto),
    ).pack(pady=10)
    kill_windows(root, janela_quarto)


def _get_quartos():
    try:
        response = SESSION.get(f"{BASE_URL}/room")
        response.raise_for_status()
        dados = response.json()
        return dados.get("content", [])
    except requests.RequestException as e:
        print("Erro ao buscar quartos:", e)
        return []


def _atualizar_dropdown_quartos(combo, quartos):
    descricoes = [quarto["description"] for quarto in quartos]
    combo["values"] = descricoes
    combo.set("selecione um quarto")


def _janela_insercao_alteracao_quarto(
    janela_pai,
    quarto=None,
    titulo="",
    confirmar=False,
    acao=(),  # função: inserir_quarto ou alterar_quarto
):
    janela_pai.withdraw()
    janela = tk.Toplevel(janela_pai)
    janela.title(titulo)
    janela.geometry("400x300")

    descricao_padrao = quarto["description"] if quarto else ""
    preco_padrao = str(quarto["price"]) if quarto else ""
    imagem_padrao = quarto["imageUrl"] if quarto else ""

    tk.Label(janela, text="Descrição:").pack()
    entry_descricao = tk.Entry(janela, width=45)
    entry_descricao.pack()
    entry_descricao.insert(0, descricao_padrao)

    tk.Label(janela, text="Preço:").pack()
    entry_preco = tk.Entry(janela, width=45)
    entry_preco.pack()
    entry_preco.insert(0, preco_padrao)

    tk.Label(janela, text="URL da Imagem:").pack()
    entry_imagem = tk.Entry(janela, width=45)
    entry_imagem.pack()
    entry_imagem.insert(0, imagem_padrao)

    def ao_confirmar():
        try:
            dados = {
                "description": entry_descricao.get(),
                "price": float(entry_preco.get()),
                "imageUrl": entry_imagem.get(),
            }
        except ValueError:
            messagebox.showerror("Erro", "Preço inválido.", parent=janela)
            return

        if confirmar:
            desc = quarto["description"] if quarto else "este quarto"
            confirm = messagebox.askyesno(
                "Confirmação", f"Deseja alterar os dados de {desc}?", parent=janela
            )
            if not confirm:
                return

        acao(quarto["id"], dados, janela) if quarto else acao(dados, janela)
        close_windows(janela_pai, janela)

    texto_botao = "✏️ Alterar" if quarto else "➕ Inserir"
    tk.Button(janela, text=texto_botao, command=ao_confirmar).pack(pady=10)
    kill_windows(janela_pai, janela)


def inserir_quarto(dados, janela_pai):
    try:
        response = SESSION.post(f"{BASE_URL}/room", json=dados)
        response.raise_for_status()
        messagebox.showinfo(
            "Sucesso", "Quarto inserido com sucesso!", parent=janela_pai
        )
    except requests.RequestException as e:
        messagebox.showerror("Erro", f"Erro ao inserir quarto:\n{e}", parent=janela_pai)


def cadastro_quarto(janela_pai):
    _janela_insercao_alteracao_quarto(
        janela_pai, titulo="Inserir Quarto", confirmar=False, acao=inserir_quarto
    )


def excluir_quarto(quarto_id, janela_pai):
    try:
        response = SESSION.delete(f"{BASE_URL}/room/{quarto_id}")
        if response.status_code == 204:
            messagebox.showinfo(
                "Sucesso", "Quarto excluído com sucesso!", parent=janela_pai
            )
        else:
            messagebox.showerror(
                "Erro", f"Erro ao excluir: {response.text}", parent=janela_pai
            )
    except requests.RequestException as e:
        messagebox.showerror("Erro", f"Erro de conexão: {e}", parent=janela_pai)


def excluir_quarto_selecionado(janela_quarto):
    janela_quarto.withdraw()
    janela_exc_quarto = tk.Toplevel(janela_quarto)
    janela_exc_quarto.title("Excluir Quarto")
    janela_exc_quarto.geometry("500x250")

    quartos = _get_quartos()

    combo = ttk.Combobox(janela_exc_quarto, state="readonly", width=80)
    combo.pack(pady=10)
    _atualizar_dropdown_quartos(combo, quartos)

    def confirmar_exclusao():
        descricao = combo.get()
        if descricao == "Selecione um quarto" or not descricao:
            messagebox.showwarning(
                "Aviso", "Selecione um quarto válido.", parent=janela_exc_quarto
            )
            return

        quarto = next((q for q in quartos if q["description"] == descricao), None)
        if quarto:
            confirmacao = messagebox.askyesno(
                "Confirmar", f"Deseja excluir '{descricao}'?", parent=janela_exc_quarto
            )
            if confirmacao:
                excluir_quarto(quarto["id"], janela_exc_quarto)
                close_windows(janela_quarto, janela_exc_quarto)

    tk.Button(janela_exc_quarto, text="🗑️ Excluir", command=confirmar_exclusao).pack(
        pady=10
    )
    kill_windows(janela_quarto, janela_exc_quarto)


def alterar_quarto(quarto_id, dados, janela_pai):
    try:
        response = SESSION.put(f"{BASE_URL}/room/{quarto_id}", json=dados)
        response.raise_for_status()
        messagebox.showinfo(
            "Sucesso", "Quarto alterado com sucesso!", parent=janela_pai
        )
    except requests.RequestException as e:
        messagebox.showerror("Erro", f"Erro ao alterar quarto: {e}", parent=janela_pai)


def alterar_quarto_selecionado(janela_quarto):
    janela_quarto.withdraw()
    janela_alt_quarto = tk.Toplevel(janela_quarto)
    janela_alt_quarto.title("Alterar Quarto")
    janela_alt_quarto.geometry("500x250")

    quartos = _get_quartos()

    combo = ttk.Combobox(janela_alt_quarto, state="readonly", width=80)
    combo.pack(pady=10)
    _atualizar_dropdown_quartos(combo, quartos)

    def abrir_janela_edicao(event):
        descricao = combo.get()
        if descricao == "Selecione um quarto" or not descricao:
            return
        quarto = next((q for q in quartos if q["description"] == descricao), None)
        if quarto:
            _janela_insercao_alteracao_quarto(
                janela_alt_quarto,
                quarto=quarto,
                titulo=f"Alterar Quarto: {descricao}",
                confirmar=True,
                acao=alterar_quarto,
            )

    combo.bind("<<ComboboxSelected>>", abrir_janela_edicao)
    kill_windows(janela_quarto, janela_alt_quarto)


# ESTADIAS
def menu_estadia(root):
    root.withdraw()
    janela_estadia = tk.Toplevel(root)
    janela_estadia.title("Menu Estadias")
    janela_estadia.geometry("300x200")

    tk.Button(
        janela_estadia,
        text="➕ Cadastrar estadia",
        command=lambda: cadastro_estadia(janela_estadia),
    ).pack(pady=5)
    tk.Button(
        janela_estadia,
        text="🗑️ Deletar estadia",
        bg="red",
        fg="white",
        command=lambda: excluir_estadia_selecionada(janela_estadia),
    ).pack(pady=5)
    tk.Button(
        janela_estadia,
        text="✏️ Alterar estadia",
        command=lambda: alterar_estadia_selecionada(janela_estadia),
    ).pack(pady=5)

    tk.Button(
        janela_estadia,
        text="❌ Fechar",
        command=lambda: close_windows(root, janela_estadia),
    ).pack(pady=10)
    kill_windows(root, janela_estadia)


def _get_estadias():
    try:
        response = SESSION.get(f"{BASE_URL}/daily")
        response.raise_for_status()
        return response.json().get("content", [])
    except requests.RequestException as e:
        print("Erro ao buscar estadias:", e)
        return []


def _atualizar_dropdown_estadias(combo, estadias):
    valores = []

    for e in estadias:
        cliente = e["client"]
        quarto = e["room"]

        nome_cliente = cliente["name"] if cliente else f"Cliente {e['client']['id']}"
        desc_quarto = quarto["description"] if quarto else f"Quarto {e['room']['id']}"

        texto = f"{e['id']} - Cliente - {nome_cliente} | Quarto - {desc_quarto} | {e['dailyDate']}"
        valores.append(texto)

    combo["values"] = valores
    combo.set("Selecione uma estadia")


def _janela_insercao_alteracao_estadia(
    janela_pai,
    estadia=None,
    clientes=[],
    quartos=[],
    titulo="",
    confirmar=False,
    acao=(),
):
    janela_pai.withdraw()
    janela = tk.Toplevel(janela_pai)
    janela.title(titulo)
    janela.geometry("400x350")

    # Dados padrão (preencher se for alteração)
    id_cliente_padrao = estadia["client"] if estadia else None
    id_quarto_padrao = estadia["room"] if estadia else None
    data_padrao = (
        estadia.get("date", "") if estadia else datetime.now().strftime("%d/%m/%Y")
    )

    # Label e combo para Cliente
    tk.Label(janela, text="Cliente:").pack(pady=(10, 0))
    nomes_clientes = [c["name"] for c in clientes] if clientes else []
    combo_clientes = ttk.Combobox(
        janela, values=nomes_clientes, state="readonly", width=40
    )
    combo_clientes.pack()
    if id_cliente_padrao:
        cliente_padrao = next(
            (c for c in clientes if c["id"] == id_cliente_padrao), None
        )
        if cliente_padrao:
            combo_clientes.set(cliente_padrao["name"])
    else:
        combo_clientes.set("Selecione um cliente")

    # Label e combo para Quarto
    tk.Label(janela, text="Quarto:").pack(pady=(10, 0))
    descricoes_quartos = [q["description"] for q in quartos] if quartos else []
    combo_quartos = ttk.Combobox(
        janela, values=descricoes_quartos, state="readonly", width=40
    )
    combo_quartos.pack()
    if id_quarto_padrao:
        quarto_padrao = next((q for q in quartos if q["id"] == id_quarto_padrao), None)
        if quarto_padrao:
            combo_quartos.set(quarto_padrao["description"])
    else:
        combo_quartos.set("Selecione um quarto")

    # Campo de data
    tk.Label(janela, text="Data da Estadia (dd/mm/aaaa):").pack(pady=(10, 0))
    entry_data = tk.Entry(janela, width=40)
    entry_data.pack()
    entry_data.insert(0, data_padrao)

    def ao_confirmar():
        nome_cliente = combo_clientes.get()
        descricao_quarto = combo_quartos.get()
        data_estadia = entry_data.get().strip()

        if nome_cliente == "Selecione um cliente" or not nome_cliente:
            messagebox.showwarning(
                "Aviso", "Selecione um cliente válido.", parent=janela
            )
            return
        if descricao_quarto == "Selecione um quarto" or not descricao_quarto:
            messagebox.showwarning(
                "Aviso", "Selecione um quarto válido.", parent=janela
            )
            return
        if not data_estadia:
            messagebox.showwarning("Aviso", "Informe a data da estadia.", parent=janela)
            return

        try:
            # Converte data de dd/mm/aaaa para ISO (aaaa-mm-ddTHH:MM:SS)
            data_iso = datetime.strptime(data_estadia, "%d/%m/%Y").isoformat()
        except ValueError:
            messagebox.showerror(
                "Erro", "Data inválida. Use o formato dd/mm/aaaa.", parent=janela
            )
            return

        cliente_sel = next((c for c in clientes if c["name"] == nome_cliente), None)
        quarto_sel = next(
            (q for q in quartos if q["description"] == descricao_quarto), None
        )

        if not cliente_sel or not quarto_sel:
            messagebox.showerror("Erro", "Cliente ou quarto inválido.", parent=janela)
            return

        dados = {
            "client": cliente_sel["id"],
            "room": quarto_sel["id"],
            "dailyDate": data_iso,
        }

        if confirmar:
            confirm = messagebox.askyesno(
                "Confirmação",
                f"Deseja alterar a estadia do cliente {nome_cliente} no quarto {descricao_quarto}?",
                parent=janela,
            )
            if not confirm:
                return

        if estadia:
            acao(estadia["id"], dados, janela)
        else:
            acao(dados, janela)

        close_windows(janela_pai, janela)

    texto_botao = "✏️ Alterar" if estadia else "➕ Inserir"
    tk.Button(janela, text=texto_botao, command=ao_confirmar).pack(pady=15)
    kill_windows(janela_pai, janela)


def inserir_estadia(dados, janela_pai):
    try:
        response = SESSION.post(f"{BASE_URL}/daily", json=dados)
        response.raise_for_status()
        messagebox.showinfo(
            "Sucesso", "Estadia inserida com sucesso!", parent=janela_pai
        )
    except requests.RequestException as e:
        messagebox.showerror(
            "Erro", f"Erro ao inserir estadia:\n{e}", parent=janela_pai
        )


def cadastro_estadia(janela_pai):
    clientes = _get_clientes()
    quartos = _get_quartos()

    _janela_insercao_alteracao_estadia(
        janela_pai,
        titulo="Inserir Estadia",
        confirmar=False,
        acao=inserir_estadia,
        clientes=clientes,
        quartos=quartos,
    )


def excluir_estadia(estadia_id, janela_pai):
    try:
        response = SESSION.delete(f"{BASE_URL}/daily/{estadia_id}")
        if response.status_code == 204:
            messagebox.showinfo(
                "Sucesso", "Estadia excluída com sucesso!", parent=janela_pai
            )
        else:
            messagebox.showerror(
                "Erro", f"Erro ao excluir: {response.text}", parent=janela_pai
            )
    except requests.RequestException as e:
        messagebox.showerror("Erro", f"Erro de conexão: {e}", parent=janela_pai)


def excluir_estadia_selecionada(janela_pai):
    estadias = _get_estadias()
    if not estadias:
        messagebox.showinfo("Info", "Nenhuma estadia cadastrada.", parent=janela_pai)
        return

    janela_pai.withdraw()

    janela = tk.Toplevel(janela_pai)
    janela.title("Excluir Estadia")
    janela.geometry("800x350")

    texto_estadias = [
        f"{e['id']} - Cliente {e['client']['name']} | Quarto {e['room']['description']} | {e['dailyDate']}"
        for e in estadias
    ]
    combo = ttk.Combobox(janela, state="readonly", width=180)
    combo["values"] = texto_estadias
    combo.set("Selecione uma estadia")
    combo.pack(pady=10)

    def confirmar():
        selecao = combo.get()
        if "Selecione" in selecao or not selecao:
            messagebox.showwarning(
                "Aviso", "Selecione uma estadia válida.", parent=janela
            )
            return
        estadia_id = int(selecao.split(" - ")[0])
        if messagebox.askyesno("Confirmar", "Deseja excluir esta estadia?"):
            excluir_estadia(estadia_id, janela_pai)
            close_windows(janela_pai, janela)

    tk.Button(janela, text="🗑️ Excluir", command=confirmar).pack(pady=10)
    kill_windows(janela_pai, janela)


def alterar_estadia(estadia_id, dados, janela_pai):
    try:
        response = SESSION.put(f"{BASE_URL}/daily/{estadia_id}", json=dados)
        response.raise_for_status()
        messagebox.showinfo(
            "Sucesso", "Estadia alterada com sucesso!", parent=janela_pai
        )
    except requests.RequestException as e:
        messagebox.showerror("Erro", f"Erro ao alterar estadia: {e}", parent=janela_pai)


def alterar_estadia_selecionada(janela_estadia):
    janela_estadia.withdraw()
    janela_alt_estadia = tk.Toplevel(janela_estadia)
    janela_alt_estadia.title("Alterar Estadia")
    janela_alt_estadia.geometry("800x350")

    estadias = _get_estadias()
    clientes = _get_clientes()
    quartos = _get_quartos()

    combo = ttk.Combobox(janela_alt_estadia, state="readonly", width=180)
    combo.pack(pady=10)
    _atualizar_dropdown_estadias(combo, estadias)

    def abrir_janela_edicao(event):
        selecao = combo.get()
        if selecao == "Selecione uma estadia" or not selecao:
            return

        id_estadia = selecao.split(" - ")[0]
        estadia = next((e for e in estadias if str(e["id"]) == id_estadia), None)
        if estadia:
            _janela_insercao_alteracao_estadia(
                janela_alt_estadia,
                estadia=estadia,
                clientes=clientes,
                quartos=quartos,
                titulo=f"Alterar Estadia {estadia['id']}",
                confirmar=True,
                acao=alterar_estadia,
            )

    combo.bind("<<ComboboxSelected>>", abrir_janela_edicao)
    kill_windows(janela_estadia, janela_alt_estadia)

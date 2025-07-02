import tkinter as tk
from tkinter import messagebox, ttk
from config import BASE_URL, SESSION, close_windows, kill_windows
import requests

from cruds import _get_clientes, _atualizar_dropdown


def listar_clientes(janela_pai):
    try:
        r = SESSION.get(f"{BASE_URL}/client")
        r.raise_for_status()
        dados = r.json()
        clientes = dados.get("content", [])

        if not clientes:
            messagebox.showinfo(
                "Clientes", "Nenhum cliente encontrado.", parent=janela_pai
            )
            return

        janela_pai.withdraw()
        janela_tabela = tk.Toplevel(janela_pai)
        janela_tabela.title("Lista de Clientes")
        janela_tabela.geometry("600x400")

        colunas = ("ID", "Nome", "Email", "Telefone")
        tree = ttk.Treeview(janela_tabela, columns=colunas, show="headings")

        # Definir cabeçalhos
        for col in colunas:
            tree.heading(col, text=col)
            tree.column(col, width=140, anchor="center")

        # Inserir dados
        for cliente in clientes:
            tree.insert(
                "",
                "end",
                values=(
                    cliente["id"],
                    cliente["name"],
                    cliente["email"],
                    cliente["phone"],
                ),
            )

        # Scrollbar vertical
        scrollbar = ttk.Scrollbar(janela_tabela, orient="vertical", command=tree.yview)
        tree.configure(yscrollcommand=scrollbar.set)

        # Posicionamento
        tree.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")
        kill_windows(janela_pai, janela_tabela)

    except Exception as e:
        messagebox.showerror(response.json()['error'], response.json()['message'], parent=janela_pai)


def listar_quartos(janela_pai):
    try:
        r = SESSION.get(f"{BASE_URL}/room")
        if r.status_code == 200:
            dados = r.json()
            quartos = dados.get("content", [])
            if not quartos:
                messagebox.showinfo(
                    "Quartos", "Nenhum quarto encontrado.", parent=janela_pai
                )
                return

            janela_pai.withdraw()
            janela = tk.Toplevel(janela_pai)
            janela.title("Lista de Quartos")
            janela.geometry("600x300")

            tree = ttk.Treeview(
                janela, columns=("ID", "Descrição", "Preço", "Imagem"), show="headings"
            )
            tree.heading("ID", text="ID")
            tree.column("ID", anchor="center")

            tree.heading("Descrição", text="Descrição")
            tree.column("Descrição", anchor="center")

            tree.heading("Preço", text="Preço")
            tree.column("Preço", anchor="center")

            tree.heading("Imagem", text="Imagem")
            tree.column("Imagem", anchor="center")

            for quarto in quartos:
                tree.insert(
                    "",
                    "end",
                    values=(
                        quarto["id"],
                        quarto["description"],
                        f"R$ {quarto['price']:.2f}",
                        quarto["imageUrl"],
                    ),
                )

            tree.pack(side="left", fill="both", expand=True)

            scrollbar = ttk.Scrollbar(janela, orient="vertical", command=tree.yview)
            tree.configure(yscrollcommand=scrollbar.set)
            scrollbar.pack(side="right", fill="y")
            kill_windows(janela_pai, janela)

        else:
            messagebox.showerror("Erro", "Erro ao listar quartos.", parent=janela_pai)
    except Exception as e:
        messagebox.showerror(response.json()['error'], response.json()['message'], parent=janela_pai)


def listar_estadias(janela_pai):
    try:
        # Buscar estadias
        r_estadias = SESSION.get(f"{BASE_URL}/daily")
        if r_estadias.status_code != 200:
            messagebox.showerror(response.json()['error'], response.json()['message'], parent=janela_pai)
            return
        estadias = r_estadias.json().get("content", [])
        if not estadias:
            messagebox.showinfo(
                "Estadias", "Nenhuma estadia encontrada.", parent=janela_pai
            )
            return

        # Buscar clientes
        r_clientes = SESSION.get(f"{BASE_URL}/client")
        if r_clientes.status_code != 200:
            messagebox.showerror(response.json()['error'], response.json()['message'], parent=janela_pai)
            return
        clientes = r_clientes.json().get("content", [])

        # Buscar quartos
        r_quartos = SESSION.get(f"{BASE_URL}/room")
        if r_quartos.status_code != 200:
            messagebox.showerror(response.json()['error'], response.json()['message'], parent=janela_pai)
            return
        quartos = r_quartos.json().get("content", [])

        # Dicionários para lookup rápido
        clientes_dict = {c["id"]: c["name"] for c in clientes}
        quartos_dict = {q["id"]: q["description"] for q in quartos}

        # Criar janela com tabela
        janela_pai.withdraw()
        janela = tk.Toplevel(janela_pai)
        janela.title("Lista de Estadias")
        janela.geometry("900x300")

        tree = ttk.Treeview(
            janela, columns=("ID", "Cliente", "Quarto", "Data"), show="headings"
        )
        tree.heading("ID", text="ID")
        tree.column("ID", anchor="center")

        tree.heading("Cliente", text="Cliente")
        tree.column("Cliente", anchor="center")

        tree.heading("Quarto", text="Quarto")
        tree.column("Quarto", anchor="center")

        tree.heading("Data", text="Data")
        tree.column("Data", anchor="center")

        for est in estadias:
            cliente_id = est.get("clientId")
            nome_cliente = clientes_dict.get(cliente_id, f"(ID {cliente_id})")

            quarto = est.get("room", {})
            descricao_quarto = quartos_dict.get(quarto.get("id"), f"(ID {quarto.get('id')})")
            tree.insert(
                "",
                "end",
                values=(est["id"], nome_cliente, descricao_quarto, est["dailyDate"]),
            )

        tree.pack(side="left", fill="both", expand=True)

        scrollbar = ttk.Scrollbar(janela, orient="vertical", command=tree.yview)
        tree.configure(yscrollcommand=scrollbar.set)
        scrollbar.pack(side="right", fill="y")
        kill_windows(janela_pai, janela)

    except Exception as e:
        messagebox.showerror(response.json()['error'], response.json()['message'], parent=janela_pai)


def _get_estadias_cliente(id, janela_pai):
    global response
    try:
        response = SESSION.get(f"{BASE_URL}/daily/client/{id}")

        response.raise_for_status()
        dados = response.json()
        return dados.get("content", [])

    except Exception as e:
        messagebox.showerror(response.json()['error'], response.json()['message'], parent=janela_pai)
        return []


def export_nota_fiscal(janela_pai):
    janela_pai.withdraw()
    janela_nota_fiscal = tk.Toplevel(janela_pai)
    janela_nota_fiscal.title("Gerar Nota fiscal")
    janela_nota_fiscal.geometry("300x200")

    clientes = _get_clientes()

    combo = ttk.Combobox(janela_nota_fiscal, state="readonly", width=30)
    combo.pack(pady=10)
    _atualizar_dropdown(combo, clientes)

    def escolha_cliente():
        global response
        nome = combo.get()
        if nome == "Selecione um cliente" or not nome:
            messagebox.showwarning(
                "Aviso", "Selecione um cliente válido.", parent=janela_nota_fiscal
            )
            return

        cliente = next((c for c in clientes if c["name"] == nome), None)
        if cliente:
            try:
                response = SESSION.get(
                    f"{BASE_URL}/invoice/client/{cliente['id']}",
                    stream=True,
                )
                response.raise_for_status()

                nome_arquivo = f"nota_fiscal_{cliente['name'].replace(' ', '_')}.pdf"
                with open(nome_arquivo, "wb") as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)

                messagebox.showinfo(
                    "Sucesso", f"Nota fiscal salva como: {nome_arquivo}", parent=janela_nota_fiscal
                )
                close_windows(janela_pai, janela_nota_fiscal)
            except requests.RequestException as e:
                messagebox.showerror(response.json()['error'], response.json()['message'], parent=janela_nota_fiscal)

    tk.Button(janela_nota_fiscal, text="⚙ GERAR", command=escolha_cliente).pack(pady=10)
    kill_windows(janela_pai, janela_nota_fiscal)

def gerador_relatorio(janela_pai, cliente):
    janela_pai.withdraw()
    janela_relatorios = tk.Toplevel(janela_pai)
    janela_relatorios.title("Escolha o relatório")
    tk.Label(janela_relatorios, text=f"Cliente: {cliente['name']}").pack(pady=(5, 10))
    janela_relatorios.geometry("300x200")

    def menor_estadia():

        confirmado = messagebox.askyesno(
            title="Confirmar geração",
            message="Deseja gerar este relatório?",
            parent=janela_relatorios
        )
        if not confirmado:
            return

        global response
        try:
            response = SESSION.get(f"{BASE_URL}/daily/client-cheaper/{cliente['id']}")

            response.raise_for_status()
            dados = response.json()

            messagebox.showinfo(
                title="Estadia de maior valor",
                message=f"Estadia de maior valor\nEstadia: {dados['room']['description']} | Valor: R${dados['room']['price']}",
                parent=janela_relatorios,
            )
        except Exception as e:
            messagebox.showerror(response.json()['error'], response.json()['message'], parent=janela_pai)
            return []

    def maior_estadia():

        confirmado = messagebox.askyesno(
            title="Confirmar geração",
            message="Deseja gerar este relatório?",
            parent=janela_relatorios
        )
        if not confirmado:
            return

        global response
        try:
            response = SESSION.get(f"{BASE_URL}/daily/client-expensive/{cliente['id']}")

            response.raise_for_status()
            dados = response.json()

            messagebox.showinfo(
                title="Estadia de maior valor",
                message=f"Estadia de maior valor\nEstadia: {dados['room']['description']} | Valor: R${dados['room']['price']}",
                parent=janela_relatorios,
            )
        except Exception as e:
            messagebox.showerror(response.json()['error'], response.json()['message'], parent=janela_pai)
            return []

    def somatorio_etadias():

        confirmado = messagebox.askyesno(
            title="Confirmar geração",
            message="Deseja gerar este relatório?",
            parent=janela_relatorios
        )
        if not confirmado:
            return

        estadias = _get_estadias_cliente(cliente["id"], janela_pai)

        if estadias:
            valor = sum(e["room"]["price"] for e in estadias)
            messagebox.showinfo(
                title="Valor total de estadias do cliente",
                message=f"Valor total de estadias do cliente: R${valor}",
                parent=janela_relatorios,
            )

    tk.Button(
        janela_relatorios,
        text="📈 Maior valor da estadia do cliente",
        command=maior_estadia,
    ).pack(pady=10)
    tk.Button(
        janela_relatorios,
        text="📉 Menor valor da estadia do cliente",
        command=menor_estadia,
    ).pack(pady=10)
    tk.Button(
        janela_relatorios,
        text="📊 Valor total das estadias do cliente",
        command=somatorio_etadias,
    ).pack(pady=10)
    tk.Button(
        janela_relatorios,
        text="❌ Sair",
        command=lambda: close_windows(janela_pai, janela_relatorios),
    ).pack(pady=20)
    kill_windows(janela_pai, janela_relatorios)


def relatorios(janela_pai):
    janela_pai.withdraw()
    janela_relatorios = tk.Toplevel(janela_pai)
    janela_relatorios.title("Gerar relatorios")
    janela_relatorios.geometry("300x200")

    clientes = _get_clientes()

    combo = ttk.Combobox(janela_relatorios, state="readonly", width=30)
    combo.pack(pady=10)
    _atualizar_dropdown(combo, clientes)

    def escolha_cliente():
        nome = combo.get()
        if nome == "Selecione um cliente" or not nome:
            messagebox.showwarning(
                "Aviso", "Selecione um cliente válido.", parent=janela_relatorios
            )
            return

        cliente = next((c for c in clientes if c["name"] == nome), None)
        if cliente:
            gerador_relatorio(janela_relatorios,cliente)

    tk.Button(janela_relatorios, text="⚙ GERAR", command=escolha_cliente).pack(pady=10)
    tk.Button(
        janela_relatorios,
        text="❌ Sair",
        command=lambda: close_windows(janela_pai, janela_relatorios),
    ).pack(pady=20)
    kill_windows(janela_pai, janela_relatorios)


def relatorio_cliente_pre_select(janela_pai, cliente):
    estadias = _get_estadias_cliente(cliente["id"], janela_pai)
    if estadias:
        gerador_relatorio(
            janela_pai,
            cliente,
            estadias,
        )
    else:
        messagebox.showerror(
            "Erro",
            f"Erro ao buscar as estadias do cliente: {cliente['name']}",
            parent=janela_pai,
        )

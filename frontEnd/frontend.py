import tkinter as tk
import jwt

from tkinter import messagebox, simpledialog
from cruds import (
    signUp_cliente,
    cadastro_estadia_cliente_pre_select,
    menu_cliente,
    menu_estadia,
    menu_quarto,
)
from relatorios import (
    export_nota_fiscal,
    listar_clientes,
    listar_estadias,
    listar_quartos,
    relatorio_cliente_pre_select,
    relatorios,
)
from config import (
    BASE_URL,
    ROLE_ADMIN,
    SESSION,
    CLIENT_ID,
    CLIENT_PASS,
    close_windows,
    kill_windows,
)


def get_logged_client(email):
    try:
        r = SESSION.get(f"{BASE_URL}/client/email/{email}")
        dados = r.json()
        return dados
    except Exception as e:
        messagebox.showerror("Erro", str(e))


def deletar_banco(janela_pai):
    confirmacao = simpledialog.askstring(
        "Confirmação", "Digite CONFIRMAR para continuar:"
    )
    if confirmacao != "CONFIRMAR":
        messagebox.showwarning(
            "Cancelado", "Confirmação inválida! Cancelando...", parent=janela_pai
        )
        return

    try:
        r = SESSION.get(
            f"{BASE_URL}/database/delete", params={"confirmation": "CONFIRM_DELETE"}
        )
        dados = r.json()
        if r.status_code == 200:
            messagebox.showinfo("Sucesso", dados["message"], parent=janela_pai)
        else:
            messagebox.showerror("Erro", dados["message"], parent=janela_pai)
    except Exception as e:
        messagebox.showerror("Erro", str(e))


# Interface
def menu_principal(root, authorities, cliente):
    root.withdraw()
    menus = tk.Toplevel(root)
    menus.title("Painel Administrativo - Hotel")
    menus.geometry("350x550")

    tk.Label(menus, text="HOTEL BAO", font=("Arial", 14)).pack(pady=10)

    if ROLE_ADMIN in authorities:
        tk.Button(
            menus, text="☰ Menu de Clientes", command=lambda: menu_cliente(menus)
        ).pack(pady=5)
        tk.Button(
            menus, text="☰ Menu de Quarto", command=lambda: menu_quarto(menus)
        ).pack(pady=5)
        tk.Button(
            menus, text="☰ Menu de Estadia", command=lambda: menu_estadia(menus)
        ).pack(pady=5)
        tk.Button(
            menus,
            text="📋 Listar dados dos clientes",
            command=lambda: listar_clientes(menus),
        ).pack(pady=5)
    tk.Button(
        menus, text="📋 Listar dados dos quartos", command=lambda: listar_quartos(menus)
    ).pack(pady=5)
    if ROLE_ADMIN in authorities:
        tk.Button(
            menus,
            text="📋 Listar estadias cadastradas",
            command=lambda: listar_estadias(menus),
        ).pack(pady=5)
        tk.Button(
            menus,
            text="📜 Emitir nota fiscal",
            command=lambda: export_nota_fiscal(menus),
        ).pack(pady=5)
        tk.Button(
            menus,
            text="🗑️ Limpar banco de dados",
            bg="red",
            fg="white",
            command=lambda: deletar_banco(menus),
        ).pack(pady=5)
        tk.Button(
            menus,
            text="📈 Relatorios por cliente",
            command=lambda: relatorios(menus),
        ).pack(pady=5)
    else:
        tk.Button(
            menus,
            text="🕒 Reservar",
            command=lambda: cadastro_estadia_cliente_pre_select(cliente, menus),
        ).pack(pady=5)
        tk.Button(
            menus,
            text="📈 Relatorios",
            command=lambda: relatorio_cliente_pre_select(menus, cliente),
        ).pack(pady=5)

    tk.Button(menus, text="❌ Sair", command=lambda: close_windows(root, menus)).pack(
        pady=20
    )
    kill_windows(root, menus)


def login(email, senha, janela_pai):
    try:
        url = f"{BASE_URL}/oauth2/token"

        # Payload (formulário)
        data = {"username": email, "password": senha, "grant_type": "password"}

        # Envia POST com basic auth + body
        r = SESSION.post(url, data=data, auth=(CLIENT_ID, CLIENT_PASS))
        r.raise_for_status()

        dados = r.json()

        if r.status_code == 200:
            token = dados.get("access_token")
            return token
        else:
            erro = (
                dados.get("error_description")
                or dados.get("message")
                or "Erro desconhecido"
            )
            messagebox.showerror("Erro", erro, parent=janela_pai)
            return None

    except Exception as e:
        messagebox.showerror("Erro", str(e), parent=janela_pai)

def abrir_janela_codigo(janela_pai):
    janela_pai.withdraw()
    janela_codigo = tk.Toplevel()
    janela_codigo.title("Verificar Código")
    janela_codigo.geometry("300x250")

    tk.Label(janela_codigo, text="Digite o código de 6 dígitos:").pack(pady=10)
    entrada = tk.Entry(janela_codigo, font=("Arial", 14), justify="center")
    entrada.pack(pady=5)

    tk.Label(janela_codigo, text="Nova senha:").pack(pady=5)
    entry_senha = tk.Entry(janela_codigo)
    entry_senha.pack()

    def verificar_codigo():
        codigo = entrada.get().strip()
        if not codigo.isdigit() or len(codigo) != 6:
            messagebox.showwarning("Código inválido", "Digite um código válido de 6 dígitos.")
            return
        try:
            url = f"{BASE_URL}/auth/reset-password"
            data = {
                    "newPassword": entry_senha.get(),
                    "token": entrada.get()
                }

            r = SESSION.post(url, json=data)
            r.raise_for_status()

            if r.status_code == 204:
                messagebox.showinfo(
                    "Senha alterada", "Sua senha foi alterada com sucesso!", parent=janela_pai
                )
                close_windows(janela_pai, janela_codigo)

        except Exception as e:
            messagebox.showerror(r.json()['error'], r.json()['message'], parent=janela_pai)

    kill_windows(janela_pai, janela_codigo)

    tk.Button(janela_codigo, text="Alterar🌱", command=verificar_codigo).pack(pady=10)

def reset_senha(email, janela_pai):
    try:
        url = f"{BASE_URL}/auth/recover-token"

        data = {"email": email}

        # Envia POST com basic auth + body
        r = SESSION.post(url, json=data)
        r.raise_for_status()

        if r.status_code == 200:
            messagebox.showinfo(
                "Email enviado", "Email enviado para:" + email, parent=janela_pai
            )
            abrir_janela_codigo(janela_pai)

    except Exception as e:
        messagebox.showerror(r.json()['error'], r.json()['message'], parent=janela_pai)

def menu_login(root):
    root.withdraw()
    janela_login = tk.Toplevel(root)
    janela_login.title("LOGIN - HOTELBAO")
    janela_login.geometry("350x550")

    tk.Label(janela_login, text="HOTEL BAO", font=("Arial", 14)).pack(pady=10)

    tk.Label(janela_login, text="Email:").pack()
    entry_email = tk.Entry(janela_login, width=40)
    entry_email.pack()

    tk.Label(janela_login, text="Senha:").pack()
    entry_password = tk.Entry(janela_login, width=40)
    entry_password.pack()

    def tentar_login():
        email = entry_email.get()
        password = entry_password.get()
        if email and password:
            token = login(email, password, janela_login)
            if token:
                SESSION.headers.update({"Authorization": f"Bearer {token}"})
                decoded = jwt.decode(
                    token,
                    options={"verify_signature": False},
                    algorithms=["HS256", "RS256"],
                )
                authorities = decoded["authorities"]
                cliente = get_logged_client(decoded["username"])
                menu_principal(janela_login, authorities, cliente)
        else:
            messagebox.showwarning(
                title="Erro", message="Preencha corretamente ambos os campos!"
            )

    def esqueci_senha():
        email = entry_email.get()
        if email:
            reset_senha(email, janela_login)
        else:
            messagebox.showwarning(
                title="Erro", message="Preencha corretamente o EMAIL!"
            )

    tk.Button(
        janela_login,
        text="🔐 Login",
        command=tentar_login,
    ).pack(pady=20)
    tk.Button(
        janela_login,
        text="😓 Esqueci a senha",
        command=esqueci_senha,
    ).pack(pady=20)
    tk.Button(
        janela_login, text="❌ Sair", command=lambda: close_windows(root, janela_login)
    ).pack(pady=20)
    kill_windows(root, janela_login)


root = tk.Tk()
root.title("HOTELBAO")
root.geometry("300x300")
tk.Button(
    root,
    text="🔐 Logar",
    command=lambda: menu_login(root),
).pack(pady=10)
tk.Button(
    root,
    text="✍️Signup",
    command=lambda: signUp_cliente(root),
).pack(pady=10)
tk.Button(
    root, text="📋 Listar dados dos quartos", command=lambda: listar_quartos(root)
).pack(pady=10)

tk.Button(root, text="❌ Sair", command=root.destroy).pack(pady=10)
root.mainloop()

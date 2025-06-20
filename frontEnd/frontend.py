import tkinter as tk
from tkinter import messagebox, simpledialog
from cruds import menu_cliente, menu_estadia, menu_quarto
from relatorios import (
    export_nota_fiscal,
    listar_clientes,
    listar_estadias,
    listar_quartos,
    relatorios,
)
from config import BASE_URL, SESSION


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
root = tk.Tk()
root.title("Painel Administrativo - Hotel")
root.geometry("350x600")

tk.Label(root, text="HOTEL BAO", font=("Arial", 14)).pack(pady=10)

tk.Button(root, text="☰ Menu de Clientes", command=lambda: menu_cliente(root)).pack(
    pady=5
)
tk.Button(root, text="☰ Menu de Quarto", command=lambda: menu_quarto(root)).pack(pady=5)
tk.Button(root, text="☰ Menu de Estadia", command=lambda: menu_estadia(root)).pack(
    pady=5
)
tk.Button(
    root, text="📋 Listar dados dos clientes", command=lambda: listar_clientes(root)
).pack(pady=5)
tk.Button(
    root, text="📋 Listar dados dos quartos", command=lambda: listar_quartos(root)
).pack(pady=5)
tk.Button(
    root, text="📋 Listar estadias cadastradas", command=lambda: listar_estadias(root)
).pack(pady=5)
tk.Button(
    root, text="📜 Emitir nota fiscal", command=lambda: export_nota_fiscal(root)
).pack(pady=5)
tk.Button(
    root,
    text="🗑️ Limpar banco de dados",
    bg="red",
    fg="white",
    command=lambda: deletar_banco(root),
).pack(pady=5)
tk.Button(
    root,
    text="📈 Relatorios por cliente",
    command=lambda: relatorios(root),
).pack(pady=5)

tk.Button(root, text="❌ Sair", command=root.destroy).pack(pady=20)

root.mainloop()

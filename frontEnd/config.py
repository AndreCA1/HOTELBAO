import requests

BASE_URL = "http://localhost:8080"
# Cria uma sessão de conexão para não precisar ficar reabrindo conexão
SESSION = requests.session()

# Variaveis servidor token
CLIENT_ID = "hotelbao"
CLIENT_PASS = "myclientsecret"

# ROLES
ROLE_ADMIN = "ROLE_ADMIN"
ROLE_CLIENT = "ROLE_CLIENT"


def kill_windows(root, child):
    if child.winfo_exists():
        child.protocol("WM_DELETE_WINDOW", lambda: close_windows(root, child))


def close_windows(root, child):
    root.deiconify()
    child.destroy()

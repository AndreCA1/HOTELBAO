import requests

BASE_URL = "http://localhost:8080"
# Cria uma sessão de conexão para não precisar ficar reabrindo conexão
SESSION = requests.session()

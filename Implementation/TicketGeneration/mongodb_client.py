import os
import gridfs
from pymongo import MongoClient
from pymongo.errors import ConnectionFailure, DuplicateKeyError
from pymongo.server_api import ServerApi

# --- Configuração Inicial do MongoDB Atlas ---

# Obtenha a Connection String do seu painel do MongoDB Atlas.
# É uma boa prática armazená-la como uma variável de ambiente.
# Exemplo: "mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority"
MONGO_CONNECTION_STRING = os.environ.get("MONGO_CONNECTION_STRING")

DB_NAME = "banking_system"

mongo_client = None
db = None
fs = None

try:
    if not MONGO_CONNECTION_STRING:
        raise ValueError("A variável de ambiente MONGO_CONNECTION_STRING não está definida.")

    # Inicializa o cliente do MongoDB usando a Stable API
    mongo_client = MongoClient(MONGO_CONNECTION_STRING, server_api=ServerApi('1'))
    
    # Testa a conexão
    mongo_client.admin.command('ping')
    print("Conexão com MongoDB Atlas estabelecida com sucesso.")

    # Define o banco de dados e o GridFS
    db = mongo_client[DB_NAME]
    fs = gridfs.GridFS(db)

except ConnectionFailure as e:
    print(f"Erro de conexão com o MongoDB: {e}")
except ValueError as e:
    print(e)
except Exception as e:
    print(f"Ocorreu um erro inesperado ao conectar com o MongoDB: {e}")


def upload_ticket(file_path: str, metadata: dict) -> str | None:
    """
    Faz o upload de um arquivo PDF para o GridFS e associa metadados a ele.
    Se um arquivo com o mesmo 'id_boleto' (filename) já existir, ele não será duplicado.
    - file_path: Caminho local do arquivo PDF a ser salvo.
    - metadata: Dicionário com dados a serem salvos (ex: id_boleto, id_conta).
    Retorna o ID do arquivo no GridFS ou None se a conexão falhar.
    """
    if not fs:
        raise ConnectionError("Conexão com GridFS não estabelecida.")

    boleto_id = metadata.get("id_boleto")
    if not boleto_id:
        raise ValueError("Metadados devem conter 'id_boleto'.")

    # Verifica se o arquivo já existe para evitar duplicatas
    # Se o arquivo já existe, deleta a versão antiga antes de inserir a nova.
    # Isso garante que os dados do boleto (e seus metadados) estejam sempre atualizados.
    if fs.exists({"filename": boleto_id}):
        print(f"Boleto com id {boleto_id} já existe no GridFS. Upload ignorado.")
        # Retorna o ID do arquivo existente
        existing_file = fs.find_one({"filename": boleto_id})
        return str(existing_file._id)
        print(f"Boleto com id {boleto_id} já existe. Deletando versão antiga para atualização.")
        old_file = fs.find_one({"filename": boleto_id})
        fs.delete(old_file._id)

    with open(file_path, "rb") as pdf_file:
        # O `filename` no GridFS pode ser usado para busca.
        # Os metadados são salvos em um campo 'metadata'.
        file_id = fs.put(pdf_file, filename=boleto_id, metadata=metadata)
    
    print(f"Arquivo {file_path} salvo no GridFS com ID: {file_id}")
    return str(file_id)


def find_ticket_by_id(boleto_id: str):
    """
    Busca um arquivo no GridFS pelo 'filename' (que usaremos como id_boleto).
    Retorna um objeto GridOut, que pode ser lido, ou None se não encontrar.
    """
    if not fs:
        raise ConnectionError("Conexão com GridFS não estabelecida.")

    # `find_one` retorna o arquivo mais recente que corresponde ao critério.
    grid_out_file = fs.find_one({"filename": boleto_id})
    return grid_out_file

def close_connection():
    """Fecha a conexão com o MongoDB."""
    if mongo_client:
        mongo_client.close()
        print("Conexão com MongoDB fechada.")
from fastapi import FastAPI, Query, HTTPException
from fastapi.responses import HTMLResponse, FileResponse, StreamingResponse
from starlette.background import BackgroundTask
from jinja2 import Environment, FileSystemLoader
from datetime import datetime
import pdfkit
import os
import io
import uuid

# Importa as funções do nosso cliente MongoDB
from mongodb_client import find_ticket_by_id, upload_ticket, close_connection

app = FastAPI(title="Ticket Generation")

# Caminho absoluto da pasta de templates
base_dir = os.path.dirname(os.path.abspath(__file__))
temp_dir = os.path.join(base_dir, "temp")
env = Environment(loader=FileSystemLoader(os.path.join(base_dir, "templates")))

# Garante que o diretório temporário para PDFs exista
os.makedirs(temp_dir, exist_ok=True)

@app.on_event("shutdown")
def shutdown_event():
    """Fecha a conexão com o banco de dados quando a aplicação é encerrada."""
    close_connection()

def _generate_ticket_pdf(context: dict) -> str:
    """
    Função interna para renderizar o HTML e gerar o arquivo PDF.
    Retorna o caminho do arquivo PDF gerado.
    """
    template = env.get_template("boleto_template.html")
    html_content = template.render(**context)

    # Gera um nome de arquivo único para evitar conflitos
    filename = f"boleto_{uuid.uuid4()}.pdf"
    output_path = os.path.join(temp_dir, filename)
    
    pdfkit.from_string(html_content, output_path)
    
    return output_path


# --- Endpoint Original (Sem alterações de comportamento) ---
@app.get("/ticket-generator", response_class=HTMLResponse)
def gerar_boleto(
    first_name: str = Query(..., description="Primeiro nome do cliente"),
    last_name: str = Query(..., description="Último nome do cliente"),
    barcode: str = Query(..., description="Código de barras"),
    amount: float = Query(..., description="Valor do boleto"),
    due_date: str = Query(..., description="Data de vencimento (YYYY-MM-DD)")
):
    try:
        due = datetime.strptime(due_date, "%Y-%m-%d").strftime("%d/%m/%Y")
    except ValueError:
        return HTMLResponse("<h3>Data inválida! Use o formato YYYY-MM-DD.</h3>", status_code=400)

    context = {
        "nome": f"{first_name} {last_name}",
        "codigo_barras": barcode,
        "valor": f"R$ {amount:,.2f}",
        "vencimento": due,
        "id_conta": None,  # Não aplicável no fluxo original
        "n_conta": None    # Não aplicável no fluxo original
    }

    output_path = _generate_ticket_pdf(context)
    
    # Retorna o arquivo e o remove após o envio
    return FileResponse(
        output_path, 
        media_type="application/pdf", 
        filename=f"boleto_{first_name}.pdf",
        background=BackgroundTask(os.remove, output_path) # Limpa o arquivo temporário
    )


# --- Novo Endpoint com Persistência no MongoDB ---
@app.get("/download-ticket/{id_boleto}")
def download_ticket(
    id_boleto: str,
    id_conta: str = Query(...),
    n_conta: str = Query(...),
    first_name: str = Query(...),
    last_name: str = Query(...),
    barcode: str = Query(...),
    amount: float = Query(...),
    due_date: str = Query(...)
):
    # 1. Verifica se o boleto já existe no MongoDB
    print(f"Buscando boleto com ID: {id_boleto}")
    existing_ticket = find_ticket_by_id(id_boleto)

    if existing_ticket:
        print(f"Boleto {id_boleto} encontrado no GridFS. Servindo diretamente.")
        # Retorna o arquivo diretamente do banco de dados
        return StreamingResponse(
            io.BytesIO(existing_ticket.read()),
            media_type="application/pdf",
            headers={"Content-Disposition": f"attachment; filename=boleto_{id_boleto}.pdf"}
        )

    # 2. Se não existir, gera o boleto
    print(f"Boleto {id_boleto} não encontrado. Gerando um novo...")
    try:
        due = datetime.strptime(due_date, "%Y-%m-%d").strftime("%d/%m/%Y")
        context = {
            "nome": f"{first_name} {last_name}", "codigo_barras": barcode,
            "valor": f"R$ {amount:,.2f}", "vencimento": due,
            "id_conta": id_conta, "n_conta": n_conta
        }
        pdf_path = _generate_ticket_pdf(context)

        # 3. Salva o novo boleto no MongoDB
        metadata = {"id_boleto": id_boleto, "id_conta": id_conta, "n_conta": n_conta, "timestamp": datetime.utcnow()}
        upload_ticket(pdf_path, metadata)

        return FileResponse(
            pdf_path, 
            media_type="application/pdf", 
            filename=f"boleto_{id_boleto}.pdf", 
            background=BackgroundTask(os.remove, pdf_path)
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao gerar ou salvar o boleto: {e}")

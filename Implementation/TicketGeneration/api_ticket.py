from fastapi import FastAPI, Query
from fastapi.responses import HTMLResponse, FileResponse
from jinja2 import Environment, FileSystemLoader
from datetime import datetime
import pdfkit
import os

app = FastAPI(title="Ticket Generation")

# Caminho absoluto da pasta de templates
base_dir = os.path.dirname(os.path.abspath(__file__))
env = Environment(loader=FileSystemLoader(os.path.join(base_dir, "templates")))

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

    # Renderiza o HTML
    template = env.get_template("boleto_template.html")
    html_content = template.render(
        nome=f"{first_name} {last_name}",
        codigo_barras=barcode,
        valor=f"R$ {amount:,.2f}",
        vencimento=due
    )

    filename = f"boleto_{first_name}_{last_name}_{due_date}.pdf".replace(" ", "_")

    # Gera e envia o PDF
    output_path = os.path.join(base_dir, filename)
    pdfkit.from_string(html_content, output_path)
    return FileResponse(output_path, media_type="application/pdf", filename=filename)

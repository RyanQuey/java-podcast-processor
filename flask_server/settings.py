from dotenv import load_dotenv
import os

load_dotenv(verbose=True)

_mail_enabled = os.environ.get("MAIL_ENABLED", "true")
MAIL_ENABLED = _mail_enabled.lower() in {"1", "t", "true"}

SECRET_KEY = os.environ.get("SECRET_KEY")

if not SECRET_KEY:
    raise ValueError("No SECRET_KEY set for Flask application")
SERVER_NAME = os.environ.get("SERVER_NAME")
print(os.environ.get("SERVER_NAME"), "is my server")

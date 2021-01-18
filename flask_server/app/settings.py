from dotenv import load_dotenv
import os
from flask_cors import CORS
import logging

logging.getLogger('flask_cors').level = logging.DEBUG
load_dotenv(verbose=True)

def config(app):
    _mail_enabled = os.environ.get("MAIL_ENABLED", "true")
    MAIL_ENABLED = _mail_enabled.lower() in {"1", "t", "true"}

    SECRET_KEY = os.environ.get("SECRET_KEY")

    if not SECRET_KEY:
        raise ValueError("No SECRET_KEY set for Flask application")

    """
    # setting SERVER_NAME is breaking things right now. 
    # Maybe because of this: https://stackoverflow.com/questions/47002617/flasks-built-in-server-always-404-with-server-name-set
    # TODO Worry about it later
    SERVER_NAME = os.environ.get("SERVER_NAME") + "hi"
    print(SERVER_NAME, "is my SERVER_NAME")
    """
    print("not setting SERVER_NAME for now...")

    # One of the simplest configurations. Exposes all resources matching /api/* to
    # CORS and allows the Content-Type header, which is necessary to POST JSON
    # cross origin.
    # TODO consider adding supports_credentials=True, to allow sending cookies cross-origin also

    # CORS(app, resources={
    #     # this will also allow e.g., /search for searchkit
    #     r'/*': {"origins": "*"}
    #     #r'/api/*': {"origins": "*"}
    #     # r'/api/*': {"origins": [CLIENT_URL]}
    # })
    CORS(app)


    app.config.update(
        SECRET_KEY=SECRET_KEY
        #   NOTE don't pass this in...breaks for some reason
        #   SERVER_NAME=SERVER_NAME
    )

from flask import Flask, url_for, render_template, send_from_directory, request
import os
from app.settings import config
from app.routes import set_routes

app=Flask(__name__, static_folder='gatsby/public', template_folder='gatsby/public')
config(app)
set_routes(app)

# actually doesn't run when in docker, and actualy shouldn't (hence why the conditional is there)
if __name__ == "__main__":
    # Only for debugging while developing
    print("NOW RUNNING!!!!!!!!!!!!!!")
    app.run(host='0.0.0.0', debug=True, port=5000)
    print("started")
else:
    print("while than what is my name", __name__)

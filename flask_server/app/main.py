from flask import Flask, url_for, render_template, send_from_directory, request

import settings
from app.routes import set_routes

print("name", __name__)
app=Flask(__name__, static_folder='gatsby/public', template_folder='gatsby/public')
settings.config(app)
set_routes(app)

if __name__ == "__main__":
    # Only for debugging while developing
    app.run(host='0.0.0.0', debug=True)

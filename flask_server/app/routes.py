from flask import Flask, url_for, render_template, send_from_directory, request
from app.elasticsearch import rest_requests
import os


# avoiding error: `UserWarning: The session cookie domain is an IP address. This may not work as intended in some browsers. Add an entry to your hosts file, for example "localhost.localdomain", and use that instead` so using www.local.test
CLIENT_URL = os.environ.get("CLIENT_URL", "www.local.test:8000")

def set_routes(app):

    ####################################
    # Routes

    @app.route('/')
    def index():
        return "home page. Nothing here yet, but check out <a href='/search'>Search</a>"

    @app.route('/search', defaults={'path': ""})
    @app.route('/search/', defaults={'path': ""})
    @app.route('/search/<path:path>')
    def search(path):
        print("********************", path)
        print("path:", path)
        print("********************", path)
        return app.send_static_file('search/index.html')

    #@app.route('/api/elasticsearch/podcasts_by_language/_search', methods=['GET', 'POST', 'OPTIONS'], defaults={"es_index": "test"})
    @app.route('/api/elasticsearch/<es_index>/_search', methods=['POST'])
    def elasticsearch_index(es_index):
        print("hit our es endpoint", es_index)
        result = rest_requests.search(es_index, request)

        return result

    @app.route('/<string:path>', defaults={'subpath': ""})
    @app.route('/<path:path>/<string:subpath>')
    def page_data(path, subpath):
        """
        For all non-react static files
        catch all to grab whatever else gatsby is throwing. Otherwise need `/page-data/...` `component...` and so on. but let's be liberal about this
        note that gatsby is not namespacing their calls to /search, so we don't here either
        """
        full_path = os.path.join(path, subpath) if len(subpath) else path
        file_path = os.path.join(app.static_folder, full_path)
        print("###########################")
        print("FULL", full_path)
        print("FILE", file_path)
        print("###########################")
        return send_from_directory(app.static_folder, full_path)

    return app


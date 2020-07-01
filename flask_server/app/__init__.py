from flask import Flask, url_for, render_template, send_from_directory
import os
import app.elasticsearch.rest_requests

def create_app():
    app = Flask(__name__, static_folder='gatsby/public', template_folder='gatsby/public')
    # app = Flask(__name__, template_folder='gatsby/public')

    app.config.from_pyfile('../settings.py')

    # setup some urls for static files
    with app.app_context():
        #url_for('static', filename='index.html')
        pass

    @app.route('/hello')
    def hello_world():
        return 'Hello, World!'

    @app.route('/')
    def index():
        return "home page <a href='/search'>Search</a>"

    """
    @app.route('/', defaults={'path': ""})
    @app.route('/<path>/')
    def index(path):
        # keep this to just get it running without nesting it
        print("path:", path)
        if path != "" and os.path.exists(app.static_folder + '/' + path):
            return send_from_directory(app.static_folder, path)
        else:
            print("rendering template")
            return render_template('index.html')
        #return app.send_static_file('index.html')
        #print("hit route")
        #return "Welcome home"

    """
    @app.route('/search', defaults={'path': ""})
    @app.route('/search/', defaults={'path': ""})
    @app.route('/search/<path:path>')
    def search(path):
        print("********************", path)
        print("path:", path)
        print("********************", path)
        return app.send_static_file('search/index.html')

    @app.route('/api/elasticsearch/<es_index>')
    def elasticsearch_index(es_index):
        result = rest_requests.request(es_index)

        return result

    # catch all to grab whatever else gatsby is throwing. Otherwise need `/page-data/...` `component...` and so on. but let's be liberal about this
    # note that gatsby is not namespacing their calls to /search, so we don't here either
    @app.route('/<string:path>', defaults={'subpath': ""})
    @app.route('/<path:path>/<string:subpath>')
    def page_data(path, subpath):
        full_path = os.path.join(path, subpath) if len(subpath) else path
        file_path = os.path.join(app.static_folder, full_path)
        print("###########################")
        print("FULL", full_path)
        print("FILE", file_path)
        print("###########################")
        return send_from_directory(app.static_folder, full_path)

    with app.test_request_context():
        print("what are my routes?")
        print(url_for('hello_world'))
        print(url_for('index'))
        print(url_for('search', q='test-query'))

    return app

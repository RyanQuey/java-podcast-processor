from flask import Flask, url_for

def create_app():
    app = Flask(__name__)

    app.config.from_pyfile('../settings.py')

    # setup some urls for static files
    with app.app_context():
        url_for('static', filename='style.css')

    @app.route('/hello')
    def hello_world():
        return 'Hello, World!'

    @app.route('/')
    def index():
        return 'index'

    @app.route('/login')
    def login():
        return 'login'

    @app.route('/user/<username>')
    def profile(username):
        return '{}\'s profile'.format(escape(username))

    print("what are my routes?")
    with app.test_request_context():
        print(url_for('hello_world'))
        print(url_for('login'))
        print(url_for('login', next='/'))
        print(url_for('index'))
        print(url_for('profile', username='username'))

    return app

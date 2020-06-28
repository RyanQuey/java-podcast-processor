# Setup
### cd into this folder

```sh
cd ./flask_server
```

### Setup venv
```sh
python3 -m venv venv
. venv/bin/activate
```

### Install dependencies
```sh
pip install -r requirements.txt
```

### Setup env vars
```sh
cp .env.example .env
```
Then change according to your environment

# Start the server in development
```sh
flask run
```

or if you require remote access:

```sh
flask run --host=0.0.0.0
```

### View your app
http://127.0.0.1:5000/

# Deploy

TODO

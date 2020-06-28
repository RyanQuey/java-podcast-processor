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

### Start the server
```sh
export FLASK_APP=main.py && flask run
```

or if you require remote access:

```sh
export FLASK_APP=main.py && flask run --host=0.0.0.0
```

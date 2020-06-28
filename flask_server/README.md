# Setup
## Setup Flask
### cd into this folder

```sh
cd ./flask_server
```

### Setup venv
```sh
python3 -m venv venv
. venv/bin/activate
```

### Install Python dependencies
```sh
pip install -r requirements.txt
```

### Setup env vars
```sh
cp .env.example .env
```
Then change according to your environment

### start flask server
```sh
flask run
```

or if you require remote access:

```sh
flask run --host=0.0.0.0
```

### View your flask app
http://127.0.0.1:5000/

## Setup JS/Gatsby

We will serve a Gatsby app over flask

### JS dependencies

```sh
nvm use 
```

Note that we are currently using just one .nvmrc for the whole project, to have a consistent node version in case we later add other js subdirectories with separate microservices. Calling `nvm use` from any subdirectory of the whole podcast project should have the same node version returned

```sh
cd gatsby/
# install gatsby 
npm install -g gatsby-cli

# install all node packages
npm install
```

### start gatsby (has nice things like live reloading etc)

```sh
cd ./gatsby/
gatsby develop
```

# Deploy

TODO

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
flask run --host=www.local.test
```

or if you require remote access: (NOTE untested)

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

# Develop

## Build a new docker image
```
# remove old container 
docker stop flask-for-podcast-tool
docker rm flask-for-podcast-tool

# write new image
docker build -t flask-for-podcast-image .

# start it again
docker run --name flask-for-podcast-tool -p 5000:5000 flask-for-podcast-image:latest
```

# Deploy

```sh
cd ./gatsby/
gatsby build
# now we have the files ready to be served by flask from the gatsby/public folder

# TODO
```
TODO

# Released under MIT License

Copyright (c) 2020 Ryan Quey.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

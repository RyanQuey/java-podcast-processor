# borrowing from https://www.digitalocean.com/community/tutorials/how-to-build-and-deploy-a-flask-application-using-docker-on-ubuntu-18-04
FROM tiangolo/uwsgi-nginx-flask:python3.6
#ENV STATIC_URL /static
#ENV STATIC_PATH /var/www/app/static
# will be overwritten by whatever volume we attach (for dev), but (I believe) in prod we have no volumne, so grab all the files at the start
COPY . /app
RUN pip install -r /app/requirements.txt
# TODO try this, then can get rid of some of the startup scripts
#WORKDIR /app/app/gatsby/
#RUN nvm use
#RUN npm i -g gatsby-cli
#RUN npm i
  # RUN gatsby build


current_dir=$PWD
cd $current_dir/app/gatsby && \
  # nvm not available in bash scripts (?)
# not sure what this dot does, but it is necessary
. $HOME/.nvm/nvm.sh use && \
npm i -g gatsby-cli && \
npm i && \
gatsby build && \
cd $current_dir && \
docker-compose -f docker-compose.yml up && \
echo "SUCCESS!"

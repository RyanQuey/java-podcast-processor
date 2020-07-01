# faster than setup-and-startup, for development
# in prod though, we'll want gatsby to build the static files ahead of time
current_dir=$PWD
cd $current_dir/app/gatsby
gatsby build
cd $current_dir
docker-compose -f docker-compose.yml up
